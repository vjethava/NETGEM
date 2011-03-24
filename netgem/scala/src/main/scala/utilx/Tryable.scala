package org.utilx

/* Copyright 2011, Raphael Reitzig
*
* This file is part of org.utilx
*
* org.utilx is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* org.utilx is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with org.utilx. If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * This class offers methods to choose between two values based on
 * wether a function throws an exception or not. There are more specific
 * versions for special cases. Instances of this class are used as intermediate
 * objects by implicit conversion [[org.utilx#fun2tryable]].
 *
 * Example usage:
 * {{{
 *    val a = Seq(1,2,3)
 *    { () => a(2) } choose (2,3) // returns 2
 *    { () => a(5) } choose (2,3) // returns 3
 * }}}
 * @tparam R Return type of the wrapped function
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param f Wrapped function
 */
protected class Tryable[R](f : () => R) {
  /**
   * Executes this function and returns a boolean depending on the outcome.
   * @return `true` if and only if this function does not throw an exception.
   */
  def throws : Boolean = choose()

  /**
   * Chooses between two objects based on wether this function throws an
   * exception or not. The value not chosen is not evaluated.
   * @tparam T Type of the possible return values
   * @param c1 Return value if `f` terminates regularly
   * @param c2 Return value if `f` throws an exception
   * @return `c1` if this function does not throw an exception, `c2` otherwise.
   */
  def choose[T](c1 : => T = true, c2 : => T = false) : T = {
    try {
      f()
      c1
    }
    catch {
      case _ => c2
    }
  }

  /**
   * Specifies a default return value that is returned if and only if this
   * function throws an exception. `d` is not evaluated if `f` terminates
   * regularly.
   * @param d Default return value
   * @return Result of `f` if it terminates regularly, `d` if it throws an
   *         exception.
   */
  def or(d : => R) : R = {
    try {
      f()
    }
    catch {
      case _ => d
    }
  }

  /**
   * Executes this function and returns an option depending on the outcome.
   * @return `Some(this())` if this function does not throw an exception, `None`
   *         otherwise.
   */
  def orNone : Option[R] = {
    try {
      Some(f())
    }
    catch {
      case _ => None
    }
  }
}
