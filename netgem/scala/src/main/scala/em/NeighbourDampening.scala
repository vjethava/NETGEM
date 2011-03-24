package netgem.em

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.em.
 *
 * netgem.em is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.em is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem.em. If not, see <http://www.gnu.org/licenses/>.
 */

import collection.mutable.HashMap
import netgem.core._
import org.mathx._
import org.utilx._

/**
 * This perturbing dampens expression levels of nodes based on their
 * distance from knocked out genes. Genes farther away than the passed
 * maximum distance are not dampened at all. Edges are perturbed with the
 * product of their incident nodes' dampenings.
 * @tparam K Key type of the considered graph
 * @tparam W Weight type of the considered graph
 *
 * @constructor Creates a new instance and computes all dampening factors for
 *              influenced edges. A given edge is influenced if at least one
 *              of its incident nodes is influenced. A node is influenced if
 *              it is less than `maxDist` edges away from at least one knocked
 *              out node.
 * @param g The graph in which distances and edges are evaluated
 * @param knocked Knocked out nodes for each strain in `g`
 * @param maxDist The distance up to which nodes are influenced from knock-outs
 * @note Requires a non-negative maximum distance, i.e. `maxDist >= 0`.
 * @note Requires exactle one sequence of knocked out nodes per strain, i.e.
 *       `knocked.size == g.nodes(0).expr.size`.
 * @note Index `i` in `knocked` and `g`'s expressions is associated with the
 *       same strain.
 * @author Raphael Reitzig
 */
class NeighbourDampening[K <% Key[K],W](g : WeightedHyperGraph[K,W], knocked : Seq[Seq[Node[K,W]]], maxDist : Int)
  extends Perturbing[K,W]
{
  require(maxDist >= 0, "Depth must be non-negative")
  require(g.nodes.isEmpty || knocked.size == g.nodes(0).expr.size,
          "Need as many sets of knocked out genes as strains")

  /**
   * Storage of dampening factors per edge.
   */
  private val gamma = new HashMap[(Edge[K,W],Int),Double]

  if ( knocked.map(_.size).sum > 0 ) {
    ("Dampening up to distance " + maxDist + " from knocked out genes:") logAs Note
    knocked.view.zipWithIndex.foreach { case (nodes, i) =>
      ("  Strain " + i + ": " + nodes.mkString(", ")) logAs Note
    }

    // Precompute perturbing values for all edges that are affected
    knocked.view.zipWithIndex.foreach { case (nodes, i) =>
      nodes.foreach { n =>
        /* Perform a breadth-first iteration up to the given distance from n.
         * This ensures that shortest distances will be considered. */
        g.breadthFirstWithDepth(n, (_,d) => d >= maxDist).foreach { case (node, depth) =>
          node.edges.foreach { e =>
            if ( gamma.get((e,i)) == None ) {
              gamma((e,i)) = 1.0
            }

            gamma((e,i)) *= (1 - (.5 ** depth))
          }
        }
      }
    }
    assert(gamma.values.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in perturbing!")
  }
  else {
    "No genes knocked out, so no perturbing." logAs Note
  }

  /**
   * @note Requires a proper strain index, i.e. `strain >= 0 && strain < knocked.size`.
   */
  override def apply(strain : Int, e : Edge[K,W]) : Double = {
    require(strain >= 0 && strain < knocked.size, "Parameter is not a proper strain index")

    gamma.get((e,strain)) match {
      case Some(p) => p
      case None    => 1.0
    }
  }
}
