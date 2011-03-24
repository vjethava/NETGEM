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
 * Key wrapper for strings. Disambiguates by appending _X.
 * Can be created implicitly from strings by [[netgem.core#string2key]].
 * @author Raphael Reitzig
 *
 * @constructor Wrappes the specified string.
 * @param key The string to be wrapped as key
 */
class StringKey(val key : String) extends Key[String] {
  /**
   * Returns a new StringKey whose key is the same as
   * this ones, but with '_i' appended.
   */
  override def disambiguate(i : Int) = {
    StringKey(this.key + "_" + i)
  }

  override def toBase = key

  override def compare(other : Key[String]) = this.key.compare(other.toBase)

  override def toString = key
}

/**
 * Companion of class [[netgem.core.StringKey]]. Provides a factory and an
 * extraction method.
 */
object StringKey {
  /**
   * Wraps the passed key in a new StringKey
   */
  def apply(key : String) = {
    new StringKey(key)
  }

  def unapply(k : StringKey) = Some(k.key)
}
