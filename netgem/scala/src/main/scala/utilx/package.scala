package org

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
 * This package provides a number of classes and methods (via implicit
 * conversions) that might be helpful in every-day programming situations.
 * For details on the provided functionality see the respective classes'
 * documentation.
 * @author Raphael Reitzig
 */
package object utilx {
  implicit def fun2cached[P,R](f : P => R) = new Cacheable(f)

  implicit def seq2cartesian[T](t : Seq[T]) = new CartesianableSeq[T](t)
  implicit def set2cartesian[T](t : Set[T]) = new CartesianableSet[T](t)

  /**
   * An alias of [[org.utilx.Logger.Kind.Note]]. Use to classify messages
   * sent to [[org.utilx.Logger]].
   */
  val Note = Logger.Kind.Note
  /**
   * An alias of [[org.utilx.Logger.Kind.Warning]]. Use to classify messages
   * sent to [[org.utilx.Logger]].
   */
  val Warning = Logger.Kind.Warning
  /**
   * An alias of [[org.utilx.Logger.Kind.Error]]. Use to classify messages
   * sent to [[org.utilx.Logger]].
   */
  val Error = Logger.Kind.Error
  implicit def any2Loggable(o : Any) = new Loggable(o)

  implicit def string2numericable(s : String) = new Numericable(s)

  implicit def fun2tryable[R](f : () => R) = new Tryable(f)
}
