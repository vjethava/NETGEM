package netgem

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.
 *
 * netgem.core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem.core. If not, see <http://www.gnu.org/licenses/>.
 */

import netgem.core._
import org.utilx._
import netgem.frontend.Frontend
import netgem.input.{DataReader, CsvReader}
import netgem.output.{CsvWriter}
import netgem.em._

/**
 * This is the original NETGEM, that is
 *  - CSV input
 *  - Discrete edge weight space
 *  - Discrete Markov model
 *  - Distance-based dampening
 *  - CSV output
 * @see [[netgem.input.CsvReader]]
 * @see [[netgem.em.DiscreteMarkov]]
 * @see [[netgem.em.NeighbourDampening]]
 * @see [[netgem.output.CsvWriter]]
 * @author Raphael Reitzig
 */
object Netgem extends NetgemContainer {
  override val name = "Original NETGEM"

  /* Imports all members from Logger. Mainly, those are used:
   *  - Mode
   *  - Silent, Shell, File, Both : Mode
   *  - prefix
   *  - separate
   *  - finish */
  import org.utilx.Logger._

  /**
   * The following parameters are necessary for this algorithm
   * and have to be provided by a frontend to be used:
   *  - all input files: see documentation of [[netgem.input.CsvReader]]
   *  - `timesteps`:  The number of measurements per strain (integer)
   *  - `knocked_out`: One list of knocked out gene per strain, e.g. for four
   *                   trains (the first has no knocked out genes!): `;G1,G2;G1;G2`
   *                   Names have to fit the input node keys.
   *  - `max_damp_dist`: The maximum distance at which nodes are dampened by
   *                     knocked out nodes.
   *  - `weights`: The allowed edge weights (reals). Give as
   *               comma-separated list.
   *  - `sparsities`: One (real) factor per allowed target edge weight. The
   *                  higher a factor in relation to the others, the stronger
   *                  the corresponding weight is weighted in the priors.
   *  - `iterations`: The number of forward-backward iterations performed (integer)
   *  - all result files: see documentation of [[netgem.output.CsvWriter]]
   */
  override val parameters = Map[String, String => Boolean](
    // Parameters for CSV reader
    "nodes_file"              -> (_.length > 0),
    "edges_file"              -> (new java.io.File(_).exists),
    "expressions_file"        -> (new java.io.File(_).exists),
    "categories_file"         -> (_.length > 0),
    "category_map_file"       -> (new java.io.File(_).exists),
    "timesteps"               -> { s => s.isInt && s.toInt >= 0 },
    // Perturbing parameters
    "knocked_out"             -> ((_ : String) => true),
    "max_damp_dist"           -> { s => s.isInt && s.toInt >= 0 },
    // FB parameters
    "weights"                 -> { s => s.split(",").forall(_.isDouble) },
    "sparsities"              -> { s => s.split(",").forall(_.isDouble) },
    "iterations"              -> (_.isInt),
    // Parameters for CSV writer
    "results_by_name_file"    -> (_.length > 0),
    "results_by_score_file"   -> (_.length > 0),
    "result_statistics_file"  -> (_.length > 0),
    "result_nodes_file"       -> (_.length > 0),
    "result_edges_file"       -> (_.length > 0),
    "result_expressions_file" -> (_.length > 0)
  )

  /**
   * Build a graph from an input reader's results. Generally, it is recommended
   * to use [[netgem.core.WeightedHyperGraph#apply]] because of the rigoruous
   * checks this method performs. However, you may want to preprocess the read
   * data somehow, and you can to this by setting this variable.
   * In this version, expression data are centered around 0 and scaled to fit
   * into [-1, 1] (per strain).
   */
  protected var preprocess : DataReader => WeightedHyperGraph[String,Seq[Double]] = { r : DataReader =>
    def stats[T](i : Iterable[T])(implicit num : Fractional[T]) : (Option[T], T, Option[T]) = {
      import num._
      val size = i.size
      var (min, max, sum) = (Option.empty[T], Option.empty[T], num.zero)

      i foreach { e =>
        if ( min == None || lt(e, min.get) ) { min = Some(e) }
        if ( max == None || gt(e, max.get) ) { max = Some(e) }
        sum = plus(e, sum)
      }

      (min, div(sum, fromInt(size)), max)
    }

    // Get all expressions for resp strains in one seq.
    val flatExpr = r.exprs.get.values.transpose.map(_.flatten)
    val mods = Array.ofDim[Double => Double](flatExpr.size)

    flatExpr.view.zipWithIndex foreach { case (strain, i) =>
      stats(strain) match {
        case (Some(min), mean, Some(max)) => {
          val bound = math.abs(min) max math.abs(max)
          mods(i) = { e : Double => (e - mean) / bound }
        }
        case (_,mean,_) =>  mods(i) = { e : Double => e - mean }
      }
    }

    val expr : Map[String, Seq[Seq[Double]]] = r.exprs.get.map { case (k, v) =>
      (k, v.view.zipWithIndex.map { case (s,i) => s.map { e => mods(i)(e) } })
    }

    WeightedHyperGraph[String,Seq[Double]](r.nodes.get, r.edges.get, expr.lift,
                                           r.cats.get, r.catsMap.get.lift, Seq(0.0))
  }

  override def run(front : Frontend = Frontend(this), logMode : Mode = Both("log")) {
    // Setup Logger
    prefix = "[netgem] "
    mode = logMode
    ("Running " + name) logAs Note
    ("Using " + front + " and logging to " + logMode + ".") logAs Note

    /**
     * Helper function that gets the parameter with the
     * specified name from the frontend. If frontend yields
     * <code>None</code>, the specified alternative <code>or</code>
     * is returned.
     */
    def getOr(name : String, or : String = "") : String = {
      val res = front.get(name)()
      res match {
        case Some(s) => s
        case None    => or
      }
    }

    try {
      separate
      "Starting CSV reading...".log
      val reader = new CsvReader()
      reader.nodeFile = getOr("nodes_file")
      reader.edgeFile = getOr("edges_file")
      reader.exprFile = getOr("expressions_file")
      reader.catsFile = getOr("categories_file")
      reader.cmapFile = getOr("category_map_file")
      reader.strainsLength = getOr("timesteps", "0").toInt
      reader.read
      separate

      "Constructing graph...".log
      val g = preprocess(reader)
      separate

      if ( g.nodes.isEmpty ) {
        "Empty graph left over; can't do nothing." logAs Note
        return
      }

      "Computing perturbing...".log
      val knockedOutNames = getOr("knocked_out", "; " * (g.nodes(0).expr.size - 1)).
            split(";").map(_.split(",").map(_.trim))
      // Find node objects corresponding to those keys! Dead references are dropped silently
      val knockedOut = knockedOutNames.map(_.flatMap { name => g.nodes.find(_.key == name) }.toSeq)
      val maxDist = getOr("max_damp_dist", "0").toInt
      val perturb = new NeighbourDampening(g, knockedOut, maxDist)
      separate

      "Computing priors...".log
      val weights = getOr("weights", "0").split(",").map(_.toDouble)
      val sparsity = getOr("sparsities", weights.map { _ => 1 }.mkString(",")).split(",").map(_.toDouble)
      val iters = getOr("iterations", "1").toInt
      val inf = new DiscreteMarkov(g, weights, sparsity, perturb, iters)
      separate

      "Starting infering; you can get a coffee now!".log
      inf.infer
      separate

      "Starting output...".log
      val writer = new CsvWriter()
      writer.byNameFile = getOr("results_by_name_file")
      writer.byScoreFile = getOr("results_by_score_file")
      writer.statisticsFile = getOr("result_statistics_file")
      writer.graphNodesFile = getOr("result_nodes_file")
      writer.graphEdgesFile = getOr("result_edges_file")
      writer.expressionsFile = getOr("result_expressions_file")
      writer.writeStatistics(g)
      writer.writeGraph(g)
      "Done.".log
      separate
    }
    catch {
      case e => {
        (e + ": " + e.getMessage) logAs Error
        e.printStackTrace
        "Aborted" logAs Note
      }
    }
    finally {
      // Finish logging
      finish
    }
  }
}
