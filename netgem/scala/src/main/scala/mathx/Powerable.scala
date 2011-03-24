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

import math._

/**
 * Provides operator `**` for exponentiation on floating point numbers.
 * Can be created by implicit conversions [[org.mathx#float2power]]
 * and [[org.mathx#double2power]].
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param b The wrapped number, i.e. the base of exponentiation
 */
protected class Powerable(val b : Double) {
  /**
   * Alias of [[scala.math#pow]], i.e. computes `this^e`
   * @param e The exponent
   */
  def **(e : Double) = pow(b,e)
}

/**
 * Provides operator `**` for exponentiation on integers.
 * Can be created by implicit conversions [[org.mathx#short2power]],
 *  [[org.mathx#int2power]] and [[org.mathx#long2power]].
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param b The wrapped number, i.e. the base of exponentiation
 */
protected class PowerableLong(b : Long) extends Powerable(b) {
  /**
   * Alias of [[scala.math#pow]], i.e. computes `this^e`.
   * @param e The exponent
   */
  def **(e : Long) = pow(b,e).round
}
