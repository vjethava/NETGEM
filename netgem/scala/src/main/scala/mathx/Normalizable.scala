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
 * Provides normalisation methods for collections. Can be created by implicit
 * conversions [[org.mathx#iter2normal]], [[org.mathx#range2normal]] and
 *  [[org.mathx#array2normal]].
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param domain The collection to be normalised
 * @param bf Needed to build a collection of `domain`'s type
 */
protected class Normalizable[P, S[X] <: Iterable[X]](domain : S[P])(implicit bf : CanBuildFrom[S[P],Double,S[Double]], p2double : P => Double) {
  /**
   * Returns a new collection `c` such that for all `i`, `c(i) == domain(i) / div`.
   * @param div The divisor applied to all elements
   * @param bf Needed to build a collection of the wrapped collection's type
   * @return New collection, normalised by `div`.
   * @note Requires the divisor to be non-zero.
   */
  def normalizeBy(div : Double) : S[Double] = {
    require(div != 0, "Can not divide by zero. Doh.")
    val builder = bf(domain)
    builder ++= domain.map { e => (e : Double) / div }
    builder.result
  }

  /**
   * Returns a new collection `c` such that the elements sum up to `targetSum`
   * and for all `i`, `j`, it holds that `c(i)/c(j) == domain(i)/domain(j)`
   * (for non-zero elements). If the wrapped collection sums up to zero, the
   * result is unspecified.
   * @param targetSum The sum normalised to
   * @return New collection, normalised to sum up to `targetSum`.
   */
  def normalizeTo(targetSum : Double) : S[Double] =
    (domain.foldLeft(.0)(_+_), targetSum) match {
      case (s, t) if s != .0 && t != .0  => normalizeBy(s / t)
      case _ => (bf(domain) ++= Seq.fill(domain.size)(.0)).result
  }

  /**
   * Returns a new collection `c` such that the elements sum up to `targetSum`
   * and for all `i`, `j`, it holds that `c(i)/c(j) == domain(i)/domain(j)`
   * (for non-zero elements).
   * @return New collection, normalised to sum up to `1`.
   */
  def normalize : S[Double] = normalizeTo(1.0)
}
