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

import org.utilx._
import org.mathx._
import netgem.core._

import collection.mutable.HashMap
import math._

/**
 * Applies a forward backward algorithm on the specified graph, assuming
 * that weights evolve like a Markov chain.
 * @tparam K The used graph's key type
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance and computes priors
 * @param g The graph inference will be ran on
 * @param w A sequence of allowed edge weights
 * @param sparsity Regulates the dominance of edge weight values in priors. The
 *                 higher `sparsity(i)` is in relation to other elements, the
 *                 more often will `w(i)` occurr.
 * @param p The perturbing used to model strain differences
 * @param iters The number of forward-backward iterations performed
 * @param seed Seed for the random number generator used for creating priors
 * @note Requires some allowed weights to choose from, i.e. `w.size > 1`.
 * @note Requires sparsities to fit allowed weights, i.e. `w.size == sparsity.size`.
 */
class DiscreteMarkov[K <% Key[K]](val g : WeightedHyperGraph[K,Seq[Double]], val w : Seq[Double], val sparsity : Seq[Double], val p : Perturbing[K,Seq[Double]], val iters : Int, val seed : Long = System.currentTimeMillis)
  extends EM
{
  require(w.size == sparsity.size, "Sparsity factors do not fit weights")
  require(w.size > 1, "Need more than one possible weight")
  require(g.nodes.map(_.expr).flatten.flatten.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in expressions!")
  require(w.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in weights!")
  require(sparsity.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in sparsity!")

  // Some aliases
  /** Number of strains */
  private val strains = g.nodes(0).expr.size
  /** Number of time steps */
  private val steps   = g.nodes(0).expr(0).size
  /** Available categories */
  private val cats    = g.categories

  // Initialise random number generator
  ("Using seed " + seed) logAs Note
  private val random = new RichRandom(seed)
  import random._

  // Parameter prior for transition matrices
  private val theta = Array.ofDim[Double](w.size, w.size)
  theta.foreach { row =>
    // each elements holds a random number in (0,1], scaled by sparsity factor
    row.indices.foreach { i => row(i) = (1.0 - nextUniform) * sparsity(i) }
    val sum = row.sum
    row.indices.foreach { i => row(i) /= sum }
  }
  assert(theta.flatten.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in theta!")

  // Priors for transition matrices
  /**
   * Transition matrices. First component holds result from prior
   * iteration. second one is written to.
   * Category indices correspond to those in the specified graph's
   * category sequence.
   */
  private var q = Array.ofDim[Double](2, cats.size, w.size, w.size)
  q(0).foreach { qh =>
    qh.zipWithIndex.foreach { case (row, i) =>
      nextDirichlet(theta(i)) copyToArray row
    }
  }

  /**
   * Implicitly wraps an edge in its inferring wrapper. Since this method is
   * cached, the wrappers' values and caches are kept.
   */
  private implicit val edge2inferable = { (e : Edge[K,Seq[Double]]) => new InferableEdge(e) }.cached

  /* Initialise priors for edge-wise parameters
   * by implicitly creating the wrapper objects
   * for the first time */
  g.edges.foreach { edge2inferable(_) }

  /** Temporary array */
  private val tmpSum = Array.ofDim[Double](cats.size, w.size, w.size)
  /** Temporary array */
  private val tmpDenom = Array.ofDim[Double](cats.size, w.size)

  /**
   * Perform the next forward-backward iteration. Will result in new transition
   * matrices, stored in the first component of `q`, plus the
   * effects on single edges.
   */
  private def iter {
    assert(q(0).flatten.flatten.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in q!")

    "  Computing new mixtures..." logAs Note
    g.edges.foreach(_.iter)

    "  Computing new transition matrices..." logAs Note
    var t, c1, c2, w1, w2 = 0
    var sum = 0.0

    // Filling temporary sums
    while ( c1 < cats.size ) {
      w1 = 0;
      while ( w1 < w.size ) {
        w2 = 0
        while ( w2 < w.size ) {
          sum = 0.0
          g.edges foreach { e =>
            t = 0
            while ( t < steps - 1 ) {
              c2 = 0
              while ( c2 < cats.size ) {
                sum += e.x(t,w1,w2,c1,c2)
                c2 += 1
              }
              t += 1
            }
          }
          tmpSum(c1)(w1)(w2) = sum
          w2 += 1
        }
        w1 += 1
      }
      c1 += 1
    }

    // Filling temporary denoms
    // TODO interleave witm tmpSum computation
    c1 = 0
    while ( c1 < cats.size ) {
      w1 = 0;
      while ( w1 < w.size ) {
        sum = 0.0
        w2 = 0
        while ( w2 < w.size ) {
          sum += theta(w1)(w2) - 1 + tmpSum(c1)(w1)(w2)
          w2 += 1
        }
        tmpDenom(c1)(w1) = sum
        w1 += 1
      }
      c1 += 1
    }

    // Updating q
    c1 = 0
    while ( c1 < cats.size ) {
      w1 = 0;
      while ( w1 < w.size ) {
        w2 = 0
        while ( w2 < w.size ) {
          q(1)(c1)(w1)(w2) = (theta(w1)(w2) - 1 + tmpSum(c1)(w1)(w2)) / tmpDenom(c1)(w1)
          w2 += 1
        }
        w1 += 1
      }
      c1 += 1
    }

    // Exchange transition matrix versions
    q = q.reverse
  }

  private var done = false
  /**
   * Runs a forward-backward on the specified graph as per the set
   * parameters. When finished properly, the graph's edges hold the
   * infered weights.
   */
  def infer {
    if ( g.nodes.isEmpty ) {
      "Graph is empty, can't infer nothing!" logAs Note
    }
    else if ( done ) {
      throw new Exception("This inferer is used up!")
    }
    else {
      ("Running " + iters + " iterations forward-backward...") logAs Note
      (1 to iters).foreach { i =>
        iter
        ("Iteration " + i + " done") logAs Note
      }

      "Determining most likely edge weights..." logAs Note
      g.edges.foreach(_.chooseWeights)

      done = true

      // Clear edge cache in order to remove all intermediate results from heap
      edge2inferable.clear
    }
  }

  /**
   * Provides (cached) functions on edges needed for
   * forward-backward iterations. Should only be created
   * via implicit conversion `edge2inferable`.
   */
  private class InferableEdge(val e : Edge[K,Seq[Double]]) {
    // Parameter prior for mixture
    private val lambda = {
      val a = Array.ofDim[Double](cats.size)
      // Start with 1 iff incident node has resp category
      e.nodes.map(_.cats).flatten.distinct.foreach { c =>
        a(cats.indexOf(c)) = 1.0
      }

      // Apply Gaussian noise and force positive values
      val r = Array.ofDim[Double](a.size)
      do {
        r.indices.foreach { i => r(i) = abs(a(i) + nextGaussian(0.0, 0.1)) }
      } while ( r.contains(0.0) )

      r.normalize copyToArray r
      r
    }
    assert(lambda.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in lambda!")

    // Mixture prior
    /**
     * Second component holds results from the current iteration. They
     * are swapped to the first component at the beginning of
     * every iteration.
     */
    private var alpha = Array.ofDim[Double](2, cats.size)
    nextDirichlet(lambda) copyToArray alpha(1)

    /** Observation likelihoods. First index is time step, second weight index. */
    private val o = {
      val target = Array.ofDim[Double](steps, w.size)

      (0 until steps) foreach { t =>
        var denom = 0.0
        w.indices foreach { w1 =>
          target(t)(w1) = exp(-w(w1) * (0 until strains).view.map { s =>
            e.nodes.view.map { _.expr(s)(t) }.product * p(s, e)
          }.sum)
          denom += target(t)(w1)
        }
        if ( denom != 0.0 ) {
          w.indices foreach { w1 =>
            target(t)(w1) /= denom
          }
        }
      }

      target
    }
    assert(o.flatten.forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in o!")

    /** Forward probabilities. First index is time step, second weight index,
     *  third category index. */
    private val f = Array.ofDim[Double](steps, w.size, cats.size)
    w.indices * cats.indices foreach { case (w1, c1) =>
      f(0)(w1)(c1) = 1.0
    }

    /** Backward probabilities. First index is time step, second weight index,
     *  third category index. */
    private val b = Array.ofDim[Double](steps, w.size, cats.size)
    w.indices * cats.indices foreach { case (w1, c1) =>
      b(steps - 1)(w1)(c1) = 1.0
    }

    /** Weight & category transition probability */
    def x(t : Int, w1 : Int, w2 : Int, c1 : Int, c2 : Int) = {
      f(t)(w1)(c1) * alpha(0)(c2) * q(0)(c1)(w1)(w2) * o(t+1)(w2) * b(t+1)(w2)(c2)
    } // Don't cache that one. Not much to do here, but lots of values if caching!

    private val xsum = Array.ofDim[Double](cats.size)

    /**
     * Performs the next iteration of computations for this edge.
     * Results in updated category mixtures, stored in the second
     * component of `alpha`
     */
    def iter {
      assert(alpha(1).forall { e => !e.isNaN && !e.isInfinite }, "NaN or infinity in alpha!")

      // Move alpha values
      alpha = alpha.reverse

      var t, w1, w2, c1, c2 = 0
      var sum1, sum2 = 0.0

      // Compute new f values
      t = 1
      while ( t < steps ) {
        w1 = 0;
        while ( w1 < w.size ) {
          c1 = 0
          while ( c1 < cats.size ) {
            sum1 = 0.0
            w2 = 0;
            while ( w2 < w.size ) {
              c2 = 0
              while ( c2 < cats.size ) {
                sum1 += q(0)(c2)(w2)(w1) * f(t-1)(w2)(c2)
                c2 += 1
              }
              w2 += 1
            }
            f(t)(w1)(c1) = sum1 * o(t)(w1) * alpha(0)(c1)
            assert(!f(t)(w1)(c1).isNaN && !f(t)(w1)(c1).isInfinite, "NaN or infinity in f!")
            c1 += 1
          }
          w1 += 1
        }
        t += 1
      }

      // Compute new b values
      t = steps - 2
      while ( t >= 0 ) {
        w1 = 0;
        while ( w1 < w.size ) {
          c1 = 0
          while ( c1 < cats.size ) {
            sum1 = 0.0
            w2 = 0;
            while ( w2 < w.size ) {
              sum2 = 0.0
              c2 = 0
              while ( c2 < cats.size ) {
                sum2 += alpha(0)(c2) * b(t+1)(w2)(c2)
                c2 += 1
              }
              sum1 += sum2 * q(0)(c1)(w1)(w2) * o(t+1)(w2)
              w2 += 1
            }
            b(t)(w1)(c1) = sum1
            assert(!b(t)(w1)(c1).isNaN && !b(t)(w1)(c1).isInfinite, "NaN or infinity in b!")
            c1 += 1
          }
          w1 += 1
        }
        t -= 1
      }

      // Compute new xsum values
      c2 = 0
      while ( c2 < cats.size ) {
        sum1 = 0.0
        t = 0
        while ( t < steps - 1 ) {
          w1 = 0;
          while ( w1 < w.size ) {
            w2 = 0;
            while ( w2 < w.size ) {
              c1 = 0
              while ( c1 < cats.size ) {
                sum1 += x(t,w1,w2,c1,c2)
                c1 += 1
              }
              w2 += 1
            }
            w1 += 1
          }
          t += 1
        }
        xsum(c2) = sum1
        assert(!xsum(c2).isNaN && !xsum(c2).isInfinite, "NaN or infinity in xsum!")
        c2 += 1
      }

      // Compute new, better mixture
      c2 = 0; sum1 = 0.0
      while ( c2 < cats.size ) {
        alpha(1)(c2) = lambda(c2) - 1.0 + xsum(c2)
        sum1 += alpha(1)(c2)
        c2 += 1
      }
      c2 -= 1
      while ( c2 >= 0 ) {
        alpha(1)(c2) /= sum1
        assert(!alpha(1)(c2).isNaN && !alpha(1)(c2).isInfinite, "NaN or infinity in xsum!")
        c2 -= 1
      }
      //("    Done edge " + e) logAs Note // uncomment for debugging
    }

    /**
     * Sets the most likely weights for this edge based on the current
     * forward and backward probabilities.
     */
    def chooseWeights {
      val a = Array.ofDim[Double](steps)

      (0 until steps).foreach { t =>
        a(t) = w(w.indices argmax { w1 =>
          cats.indices.view.map { c1 =>
            f(t)(w1)(c1) * b(t)(w1)(c1)
          }.sum
        })
      }

      e.weight = a
    }
  }
}
