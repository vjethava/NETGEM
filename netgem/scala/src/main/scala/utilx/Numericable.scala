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
 * Offers methods on strings (via implicit conversion
 *  [[org.utilx#string2numericable]]) that check wether or not a string
 * can be converted to a certain numeric type.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param s String to be checked
 */
protected class Numericable(s : String) {
  private val rep = s.trim.toLowerCase

  /**
   * Checks for convertability to `Boolean`
   * @return `true` if and only if this string is a proper `Boolean`
   *         representation.
   */
  def isBoolean = rep == "true" || rep == "false"

  /**
   * Checks for convertability to `Byte`
   * @return `true` if and only if this string is a proper `Byte`
   *         representation.
   */
  def isByte    = { () => rep.toByte }.throws

  /**
   * Checks for convertability to `Short`
   * @return `true` if and only if this string is a proper `Short`
   *         representation.
   */
  def isShort   = { () => rep.toShort }.throws

  /**
   * Checks for convertability to `Int`
   * @return `true` if and only if this string is a proper `Int`
   *         representation.
   */
  def isInt     = { () => rep.toInt }.throws

  /**
   * Checks for convertability to `Long`
   * @return `true` if and only if this string is a proper `Long`
   *         representation.
   */
  def isLong    = { () => rep.toLong }.throws

  /**
   * Checks for convertability to `Float`
   * @return `true` if and only if this string is a proper `Float`
   *         representation.
   */
  def isFloat   = { () => rep.toFloat }.throws

  /**
   * Checks for convertability to `Double`
   * @return `true` if and only if this string is a proper `Double`
   *         representation.
   */
  def isDouble  = { () => rep.toDouble }.throws

  /**
   * Checks for convertability to `Double`
   * @return `true` if and only if this string is a proper `Double`
   *         representation.
   */
  def isNumeric = isDouble
}
