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

import netgem.core.{Edge, Key}

 /**
 * Knowledge about different strains might influence inference.
 * A perturbing abstracts this and computes a perturbing factor
 * for any edge in a proper strain. A smaller factor implies less
 * influence in inference.
 * @tparam K The key type of the edges this `Perturbing` processes
 * @tparam W The weight type of the edges this `Perturbing` processes
 * @author Raphael Reitzig
 */
trait Perturbing[K,W] {
  /**
   * Returns the perturbing factor of the specified edge in the
   * specified strain.
   * @param strain Strain index. Must fit the graph this perturbing
   *               is used on.
   * @param e      An arbitrary edge
   * @return A number from [0,1] depending on how strong the specified
   *         edge is suppressed in the specified strain. If the edge
   *         is not known, 1.0 (i.e. no effect) is returned.
   */
  def apply(strain : Int, e : Edge[K,W]) : Double
}
