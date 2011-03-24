package netgem.output

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.output.
 *
 * netgem.output is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.output is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem.output. If not, see <http://www.gnu.org/licenses/>.
 */

import netgem.core.{WeightedHyperGraph, Key}
import org.utilx._
import org.mathx._

/**
 * Provides some statistics on graphs with discrete edge weight sets in
 * order to evaluate inference results.
 * Currently, the following statistics are implemented:
 *  - Change Score
 *   - Mean
 *   - Variance
 *   - Standard Deviation
 * @author Raphael Reitzig
 */
object DiscreteStatistics {
  private type Graph = WeightedHyperGraph[_,Seq[Double]]
  private type Statistic = Graph => Option[Double]

  /**
   * The set of statistic codes `DiscreteStatistics` can handle.
   * Function `compute(_)(code)` computes the statistic for codes found here.
   */
  val codes = Seq("mean", "var", "stdv")

  private def statistics(name : String) : (String, Statistic) = name match {
    case "mean" => ("Change Score Mean", { g : Graph =>
        (g.edges.size > 0) o {
           g.edges.view.flatMap(e  => changeScore(e.weight)).sum / g.edges.length
        }
      }.cached)
    case "var"  => ("Change Score Variance", { g : Graph =>
        compute(g)("mean") match {
          case Some(mean) => {
            Some(g.edges.view.flatMap(e => changeScore(e.weight)).map(e =>
              (e - mean)*(e - mean)).sum / g.edges.length)
          }
          case None    => None
        }
      }.cached)
    case "stdv" => ("Change Score Standard Deviation", { g : Graph =>
        compute(g)("var") match {
          case Some(v) => Some(math.sqrt(v))
          case None    => None
        }
      })
    case _      => ("Unknown statistic", { g : Graph => None })
  }

  /**
   * Computes the statistic whose code is passed on the passed graph.
   * If the statistic is unknown, None is returned.
   * @param g The graph to investigate
   * @param stat The statistic to be computed on `g`, i.e. its code (from `codes`)
   * @return The result wrapped result value if the specified statistic could
   *         be calculated, `None` otherwise.
   */
  def compute(g : Graph)(stat : String) : Option[Double] =
    statistics(stat)._2(g)

  /**
   * Returns a pretty name for the statistic whose code is passed.
   * A dummy string is returned if the code is unknown.
   * @param stat A string from `codes`
   * @return A pretty description of `stat`
   */
  def pretty(stat : String) : String = statistics(stat)._1

  /**
   * Computes the change score of the passed sequence of numbers, i.e.
   * `1/series.size * \sum_{i=0}^{series.length-2} (series(i) - series(i+1))^2`
   * @param The series The number the change score will be calculated of
   * @return `Some(c)` if the change score is `c`, `None` if the score could
   *         not be calculated (i.e. the series is too short).
   */
  def changeScore(series : Seq[Double]) : Option[Double] = {
    if ( series.length < 2 ) {
      None
    }
    else {
      series.view.sliding(2).map(e => {
        if ( e.length > 1 ) (e(0)-e(1))*(e(0)-e(1)) / series.size
        else                0.0
      }).reduceLeftOption(_+_)
    }
  }
}
