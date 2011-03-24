package org

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

import collection.generic.CanBuildFrom

/**
 * This package provides a number of methods that might be helpful in
 * expressing arithmetics concisely. Those methods are implicitly
 * provided on standard types by importing the implicit conversions
 * in this package. For details on the provided methods see the
 * respective intermediate object's documentation.
 * @author Raphael Reitzig
 */
package object mathx {
  implicit def iter2argmax[P, S[X] <: Iterable[X]](i : S[P])(implicit bf : CanBuildFrom[S[P],P,S[P]]) = new ArgMax[P,S](i)
  implicit def array2argmax[P](a : Array[P]) = new ArgMax[P,Seq](a.toSeq)
  implicit def range2argmax(r : Range) = new ArgMax[Int,IndexedSeq](r)

  implicit def short2power(s : Short) = new PowerableLong(s)
  implicit def int2power(i : Int) = new PowerableLong(i)
  implicit def long2power(l : Long) = new PowerableLong(l)
  implicit def float2power(f : Float) = new Powerable(f)
  implicit def double2power(d : Double) = new Powerable(d)

  implicit def bool2kronecker(b : Boolean) = new Kronecker(b)

  /* Uncomment once NumericOption is implemented
   * implicit def opt2numopt[T](o : Option[T]) = new NumericOption[T](o)
   * implicit def num2numopt[T](o : T) = new NumericOption[T](Some(o)) */

  implicit def iter2normal[P, S[X] <: Iterable[X]](i : S[P])(implicit p2double : P => Double, bf : CanBuildFrom[S[P],Double,S[Double]]) =
    new Normalizable[P,S](i)
  implicit def array2normal[P <% Double](a : Array[P]) = new Normalizable[P,Seq](a.toSeq)
  implicit def range2normal(r : Range) = new Normalizable[Int, IndexedSeq](r)
}
