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

import collection.mutable.HashMap

/**
 * A function whose results are cached. There are different, equivalent ways
 * to transform any function or method to a cached function:
 * {{{
 *  def foo(pr : T1, ..., pN : TN) = { [...] }
 *  val bar = Cached((foo _).tupled)
 *
 *  import org.utilx.fun2cached
 *
 *  val bar2 = (foo _).tupled.cached
 *
 *  val bar3 = { (p1 : T1, ..., pN : TN) =>
 *    [...]
 *  }.cached
 * }}}
 * Now, only the first call with a specific set of parameters executes
 * `[...]`. Subsequent calls with the same parameters --- identified with
 * `equals` --- will yield the same result. The cache can be reset with
 * [[org.utilx.Cached#clear]] or overwritten for a particular parameter
 * by [[org.utilx.Cached#reapply]].
 *
 * Note that caching is completely transparent when the wrapped function does
 * not depend on values other than its parameters and does not have side
 * effects. If it does either, repeated calls with the same parameters might
 * not behave in the same way the wrapped function would if called directly.
 *
 * This implementation is not thread-safe, that is parallel calls to
 * [[org.utilx.Cached#apply]] and [[org.utilx.Cached#reapply]] with the
 * same parameter will all be computed explicitly and overwrite each
 * other in cache in unspecified order.
 * @tparam P The wrapped function's parameter type
 * @tparam R The wrapped function's result type
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param f The function whose results will be cached
 */
class Cached[P,R](private val f : P => R) extends Function1[P,R] {
  private val cache = new HashMap[P,R]

  /**
   * Returns the result of this function applied to the specified parameter.
   * If it has been computed for the same parameter before, the result of
   * the prior run is returned, that is the function is not executed.
   * @return Result of this function on `p`, probably from cache
   */
  override def apply(p : P) = {
    cache.get(p) match {
      case Some(r) => r
      case None    => {
        val r = f(p)
        cache += p -> r
        r
      }
    }
  }

  /**
   * Recomputes this function on the specified parameter and writes the result
   * to cache, overwriting prior results if present.
   * @return Result of this function on `p`
   */
  def reapply(p : P) = {
    val r = f(p)
    cache += p -> r
    r
  }

  /**
   * Executes the wrapped function with the specified parameters, that is
   * the result is computed anew and not stored to the cache.
   * @param Parameter for this function
   * @return The result of this function on `p`
   */
  def applyRaw(p : P) = f(p)

  /**
   * Clears the cache of this function. After a call of this method,
   * this cached function behaves as if newly created.
   */
  def clear = cache.clear

  override def toString = f.toString + " (cached)"
}

/**
 * Companion of class [[org.utilx.Cached]]. Provides construction and
 * extraction methods for cached functions.
 * @author Raphael Reitzig
 */
object Cached {
  /**
   * Creates a new cached function
   * @return A cached version of `f`.
   */
  def apply[P,R](f : P => R) = new Cached[P,R](f)

  /**
   * Extracts the wrapped function from a cached one.
   * @return The function wrapped in `c`
   */
  def unapply[P,R](c : Cached[P,R]) : Option[P => R] = Some(c.f)
}

/**
 * Provides anmethod on the wrapped function to convert it into a cached version
 * of itself. Created by implicit conversion [[org.utilx#fun2cached]].
 * @tparam P Parameter type of the wrapped function
 * @tparam R Result type of the wrapped function
 * @author Raphael Reitzig
 *
 * @constructor Creates a new intermediate wrapper
 * @param f The wrapped function
 */
protected class Cacheable[P,R](val f : P => R) {
  /**
   * Wraps this function in a caching environment.
   * @return A cached version of the this function.
   */
  def cached = Cached(f)
}
