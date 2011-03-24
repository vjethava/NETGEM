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

import collection.generic.CanBuildFrom

/**
 * Provides methods on collections that find maximising/minimising parameters
 * of functions. Can be created by implicit conversions [[org.mathx#iter2argmax]],
 * [[org.mathx#range2argmax]] and [[org.mathx#array2argmax]].
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param domain The collection parameters will be taken from
 * @param bf Needed to build a collection of `domain`'s type
 * @note Requires the domain to be not empty.
 */
protected class ArgMax[P, S[X] <: Iterable[X]](domain : S[P])(implicit bf : CanBuildFrom[S[P],P,S[P]]) {
  require(domain.size > 0, "Domain can not be empty")

  /**
   * Finds all elements of this sequence that maximise the specified
   * function wrt this sequence.
   * @param f Function to be maximised
   * @return All maximisers for `f` (wrt all values in this sequence).
   *
   * @usecase def argmaxs(f : P => Int) : S[P]
   */
  def argmaxs[T](f : P => T)(implicit ord : Ordering[T]) : S[P] = {
    import collection.mutable.HashSet
    var (maxP, maxV) = (new HashSet[P], f(domain.head))
    maxP += domain.head

    domain.foreach { p =>
      val pV = f(p)
      if ( ord.gt(pV, maxV) ) {
        maxP.clear
        maxP += p
        maxV = pV
      }
      else if ( ord.equiv(pV, maxV) ) {
        maxP += p
      }
    }

    val builder = bf(domain)
    builder ++= maxP
    builder.result
  }

  /**
   * Finds all elements of this sequence that minimise the specified
   * function wrt this sequence.
   * @param f Function to be minimised
   * @return All minimisers for `f` (wrt all values in this sequence).
   *
   * @usecase def argmins(f : P => Int) : S[P]
   */
  def argmins[T](f : P => T)(implicit ord : Ordering[T]) : S[P] = {
    argmaxs(f)(ord.reverse)
  }

  /**
   * Computes an element of this sequence that maximises the specified
   * function wrt this sequence.
   * @param f Function to be maximised
   * @return A maximiser for `f` (wrt all values in this sequence).
   *
   * @usecase def argmax(f : P => Int) : P
   */
  def argmax[T](f : P => T)(implicit ord : Ordering[T]) : P = {
    argmaxs(f).head
  }

  /**
   * Finds an element `m` of this sequence that minimises the specified
   * function wrt this sequence.
   * @param f Function to be minimised
   * @return A minimiser for `f` (wrt all values in this sequence).
   *
   * @usecase def argmin(f : P => Int) : P
   */
  def argmin[T](f : P => T)(implicit ord : Ordering[T]) : P = {
    argmins(f).head
  }
}
