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

/**
 * A ResultWriter is used to export NETGEM's results.
 * @tparam K The written graph's key type
 * @tparam W The written graph's edge weight type
 * @author Raphael Reitzig
 */
trait ResultWriter[K,W] {
  /**
   * Writes the passed graph in an appropriate representation.
   * Should include at least nodes with categories and edges with
   * incident nodes.
   * @param g The written graph
   */
  def writeGraph(g : WeightedHyperGraph[K,W]) : Unit

  /**
   * Writes statistics on the inferred edge weights. Should
   * write all statistics offered by Statistics if not otherwise
   * stated.
   * @param g The graph whose statistics are written
   */
  def writeStatistics(g : WeightedHyperGraph[K,W]) : Unit
}
