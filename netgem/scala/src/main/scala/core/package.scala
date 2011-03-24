package netgem

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.core.
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

/**
 * This package contains core modules of netgem, that is those defining the
 * data structures used.
 */
package object core {
  /**
   * Implicit conversion to transform a key back to its wrapped value
   */
  implicit def key2base[T](k : Key[T]) : T = k.toBase
  /**
   * Implicit ordering on T if it can be viewed as Key[T]
   */
  implicit def ordering[T <% Key[T]] = new Ordering[T]{def compare(x: T, y: T) = x compare y}

  implicit def string2key(s : String) = StringKey(s)
}
