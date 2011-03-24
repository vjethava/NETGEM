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

// TODO fix, document, test

/**
 * This is a work-in-progress, do not use!
 */
protected class NumericOption[T](opt : Option[T]) {
  def +[U](that : U)(implicit num : Numeric[T], trans : U => T) : Option[T] = opt match {
    case None    => None
    case Some(n) => Some(num.plus(n,that))
  }
  def +[U](that : Option[U])(implicit num : Numeric[T], trans : U => T) : Option[T] = (opt, that) match {
    case (Some(a), Some(b)) => Some(num.plus(a,b))
    case _                  => None
  }

  def -[U](that : U)(implicit num : Numeric[T], trans : U => T) : Option[T] = opt match {
    case None    => None
    case Some(n) => Some(num.minus(n,that))
  }
  def -[U](that : Option[U])(implicit num : Numeric[T], trans : U => T) : Option[T] = (opt, that) match {
    case (Some(a), Some(b)) => Some(num.minus(a,b))
    case _                  => None
  }

  def *[U](that : U)(implicit num : Numeric[T], trans : U => T) : Option[T] = opt match {
    case None    => None
    case Some(n) => Some(num.times(n,that))
  }
  def *[U](that : Option[U])(implicit num : Numeric[T], trans : U => T) : Option[T] = (opt, that) match {
    case (Some(a), Some(b)) => Some(num.times(a,b))
    case _                  => None
  }

  def /[U](that : U)(implicit num : Integral[T], trans : U => T) : Option[T] = opt match {
    case None    => None
    case Some(n) => Some(num.quot(n,that))
  }
  def /[U](that : Option[U])(implicit num : Integral[T], trans : U => T) : Option[T] = (opt, that) match {
    case (Some(a), Some(b)) => Some(num.quot(a,b))
    case _                  => None
  }

  def /[U](that : U)(implicit num : Fractional[T], trans : U => T) : Option[T] = opt match {
    case None    => None
    case Some(n) => Some(num.div(n,that))
  }
  def /[U](that : Option[U])(implicit num : Fractional[T], trans : U => T) : Option[T] = (opt, that) match {
    case (Some(a), Some(b)) => Some(num.div(a,b))
    case _                  => None
  }

  def %[U](that : U)(implicit num : Integral[T], trans : U => T) : Option[T] = opt match {
    case None    => None
    case Some(n) => Some(num.rem(n,that))
  }
  def %[U](that : Option[U])(implicit num : Integral[T], trans : U => T) : Option[T] = (opt, that) match {
    case (Some(a), Some(b)) => Some(num.rem(a,b))
    case _                  => None
  }

  // TODO add **
}
