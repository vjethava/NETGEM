package netgem.core

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
 * Classes implementing this trait can be used as wrappers
 * for values that are to be used as keys in graphs. Therefore,
 * implementations of compare should usually adhere as closely to
 * possible to natural orderings of the wrapped type.
 * @tparam T The type this class is the key type of.
 * @author Raphael Reitzig
 */
trait Key[T] extends Ordered[Key[T]] {
  /**
   * This method is used to resolve key conflicts.
   * Therefore, the following should hold for any
   * instance `t` of `T` and integers `i, j`:
   *   `t.disambiguate(i) == t.disambiguate(j) <=> i == j`
   * @param i A number with respect to which this key is to be disambiguated
   * @return A copy of this key that is no different from all other copies
   *         created with other parameters.
   */
  def disambiguate(i : Int) : Key[T]

  /**
   * Returns (a copy of) the wrapped value.
   */
  def toBase : T

  /**
   * Alias for disambiguate
   */
  def ~>(i : Int) = disambiguate(i)
}

/**
 * Companion of trait [[netgem.core.Key]]. Provides an
 * extraction method.
 */
object Key {
  def unapply[T](k : Key[T]) = Some(k.toBase)
}
