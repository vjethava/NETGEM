package org.mathx

/* Copyright 2011, Raphael Reitzig
*
* This file is part of org.mathx
*
* org.mathx is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* org.mathx is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with org.mathx. If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * Empowers booleans to act as Kronecker delta. Can be created by implicit
 * conversion [[org.mathx#bool2kronecker]].
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param b The wrapped boolean
 */
protected class Kronecker(b : Boolean) {
  /**
   * Use wrapped boolean as Kronecker delta. Expression `nr` is not evaluated
   * if the wrapped value is `false`.
   * @param num A numerical value
   * @return `nr` if the wrapped value is `true`, zero otherwise
   *
   * @usecase def *(nr : Int) : Int
   */
  def *[T](nr : => T)(implicit num : Numeric[T]) : T =  b match {
    case true  => nr
    case false => num.zero
  }

  /**
   * An alias for `&&`
   * @param that Another boolean
   * @return `that` if the wrapped value is `true`, `false` otherwise
   */
  def *(that : => Boolean) : Boolean = b && that

  /**
   * An alias for `if`, i.e. `b * { ... }` is equivalent to
   * `if ( b ) { ... }`.
   * @param that Block to be executed if (and only of) the wrapped value
   *             is `true`
   */
  def *(that : => Unit) : Unit = b match {
    case true  => that
    case false => { }
  }

  /**
   * Wraps the specified value in an `Option` if (and only if) the wrapped value
   * is `true`. Expression `that` is not evaluated if the wrapped value is `false`.
   * @param that Any value
   * @param `Some(that)` if the wrapped value is `true`, `None` otherwise
   */
  def o[T](that : => T) : Option[T] = b match {
    case true  => Some(that)
    case false => None
  }
}
