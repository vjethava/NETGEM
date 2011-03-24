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

import util.Random
import math._
import org.mathx._
import collection.generic.CanBuildFrom

/**
 * Provides methods for generating random numbers with respect to
 * several often used distributions. Uses the basic pseudorandom
 * number generator from standard library.
 *
 * Note that generating numbers of more complicated distributions involves
 * creating a lot of basic random numbers and some arithmetics, that is it
 * might be slow. If you experience performance issues and are sure you can
 * never pass improper parameters, try removing elidable method calls during
 * compilation.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param self The simple random number generator used by this instance
 */
class RichRandom(self : java.util.Random = new java.util.Random()) extends Random(self) {
  /**
   * Creates a new rich random number generator using
   * a single long seed.
   * @param seed The seed used to create the internal simple random
   *             number generator
   */
  def this(seed: Long) = this(new java.util.Random(seed))

  /**
   * Returns the next pseudorandom number drawn from a geometric distribution
   * with success probability `p` using numbers from this random number
   * generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p131
   * @param p Real number in (0,1)
   * @return A positive integer
   */
  def nextGeom(p : Double) : Long = {
    require(p > 0 && p < 1, "Parameter has to be in (0,1)")
    (log(nextUniform) / log(1-p)).ceil.round
  }

  /**
   * Returns the next pseudorandom number drawn from a binomial
   * distribution with success probability `p` using numbers from
   * this random number generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p131
   * @param n Non-negative integer
   * @param p Real number in (0,1]
   * @return An integer in `[0,n]`
   */
  def nextBinom(n : Long = 1, p : Double = 0.5) : Long = {
    require(n >= 0, "Parameter n must be non-negative")
    require(p >= 0 && p <= 1, "Parameter p has to be in [0,1]")

    if ( n < 15 ) { // TODO Magic constant! Experiment and find proper value
      (1L to n).map(_ => nextUniform).count(_<p)
    }
    else {
      val (a, b) = (1 + n/2, n - n/2)
      val x = nextBeta(a, b)
      if ( x >= p ) {
        nextBinom(a-1, p/x)
      }
      else {
        a + nextBinom(b-1, (p-x)/(1-x))
      }
    }
  }

  /**
   * Returns the next pseudorandom number drawn from a Poisson
   * distribution with mean `lambda` using numbers from this random
   * number generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p132
   * @param lambda Positive real number
   * @return A non-negative integer
   */
  def nextPoisson(lambda : Double) : Long = {
    require(lambda > 0, "Parameter n must be positive")

    if ( lambda < 10 ) { // TODO Magic constant! Experiment and find proper value
      val b = E ** -lambda
      var (i, p) = (1, nextUniform)
      while ( p > b ) {
        p *= nextUniform
        i += 1
      }
      i - 1
    }
    else {
      val m = (7.0/8.0 * lambda).floor.round
      val x = nextGamma(m)
      if ( x < lambda ) {
        m + nextPoisson(lambda - x)
      }
      else {
        nextBinom(m-1, lambda / x)
      }
    }
  }

  /**
   * Not implemented
   */
  def nextMultinom() = {
    throw new UnsupportedOperationException("Yet to be implemented")
    //TODO implement
  }

  /**
   * Returns the next pseudorandom integer drawn from the interval
   * `[0, p.size)` according to the probability weights obtained from
   * normalizing `p` using numbers from this random number generator's
   * sequence. Integer `i` will be returned with probability `p(i)/p.sum`.
   *
   * Not yet implemented.
   * @param p Sequence of (relative) probability weights.
   * @note Requires `p` to contain at least one non-zero weight
   * @note Requires all values in `p` to be non-negative
   * @return An integer in `[0, p.size)`
   */
  def nextInt(p : Seq[Double] = IndexedSeq.fill(0)(1.0)) : Int = {
    require(p != null && p.size > 0, "Have to pass a non-empty sequence")
    require(p.forall(_>=0), "Weights have to be non-negative")
    require(p.exists(_>0), "There has to be at least one non-zero weight")

    val weights = p.normalize
    throw new UnsupportedOperationException("Yet to be implemented")
    //TODO implement (cf Knuth 114, 550, ex7)
  }

  /**
   * Returns the next pseudorandom integer drawn from a uniform distribution
   * on interval `[a,b)`, using numbers from this random number
   * generator's sequence.
   * @param a Lower bound
   * @param b Upper bound
   * @return A real number in `[a,b)`
   */
  def nextUniform(a : Double = 0.0, b : Double = 1.0) : Double = {
    require(a <= b, "Lower bound has to be smaller than upper bound")
    a + (b - a) * nextDouble
  }

  /**
   * Returns the next pseudorandom integer drawn from a uniform distribution
   * on interval `[0,1)`, using numbers from this random number
   * generator's sequence.
   * @return A real number in `[0,1)`
   */
  def nextUniform : Double = nextUniform()

  /**
   * Returns the next pseudorandom integer drawn from a Gaussian distribution
   * with mean `mu` and standard deviation `std`, using numbers from this
   * random number generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p127
   * @param mu Real number
   * @param sigma Non-negative real number.
   * @return A real number
   */
  def nextGaussian(mu : Double = 0.0, sigma : Double = 1.0) : Double = {
    require (sigma >= 0, "Standard deviation has to be non-negative")
    mu + sigma * super.nextGaussian
  }

  /**
   * Returns the next pseudorandom vector drawn from a multivariate
   * gaussian distribution with mean `mu` and covariance matrix `sigma`
   * using numbers from this random number generator's sequence.
   *
   * Not yet implemented
   * @param mu Vector of n positive real numbers.
   * @param cov A positive-definite nxn matrix of real, non-negative numbers
   * @return A `mu.size`-dimensional vector of real numbers
   */
  def nextGaussian(mu : Seq[Double], sigma : Seq[Seq[Double]]) : Seq[Double] = {
    require(mu.size == sigma.size && sigma.forall(_.size == mu.size),
            "Parameter dimensions do not coincide.")
    require(sigma.forall(_.forall(_>=0)),
            "Covariance matrix can not contain negative values")
    throw new UnsupportedOperationException("Yet to be implemented")
    //TODO implement (cf Knuth 551, ex13)
  }

  /**
   * Returns the next pseudorandom number drawn from an exponential
   * distribution with mean `1/mu` using numbers from this random number
   * generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p128
   * @param mu Positive real number.
   * @return Non-negative real number
   */
  def nextExponential(mu : Double = 1.0) : Double = {
    require(mu > 0, "Parameter has to be positive.")
    -mu * log(1 - nextUniform)
  }

  /**
   * Returns the next pseudorandom number drawn from a gamma
   * distribution with shape parameter (or: of order) `a` and scale parameter
   *  `1` using numbers from this random number generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p129
   * @param a Positive real number.
   * @return A non-negative real number
   */
  def nextGamma(a : Double) : Double = {
    require(a > 0, "Parameter has to be positive.")
    if ( a < 1.0 ) {
      val p = E / (a + E)
      val u = nextUniform
      if ( u < p/E ) {
        pow((1 - nextUniform), 1.0/a)
      }
      else {
        var y, x, q = 0.0
        do {
          y = nextExponential(1.0)
          if ( u < p ) {
            x = pow(E, -y/a)
            q = p * pow(E, -x)
          }
          else {
            x = 1 + y
            q =  p + (1-p)*pow(x, a-1)
          }
        }
        while ( u >= q )
        x
      }
    }
    else if ( a == 1.0 ) {
      nextExponential(1.0)
    }
    else {
      val y = tan(Pi * nextUniform)
      val x = sqrt(2*a - 1) * y + a - 1
      if ( x <= 0.0 || nextUniform > (1 + y*y) * exp((a-1)*log(x/(a-1)) - sqrt(2*a-1)*y) ) {
        nextGamma(a)
      }
      else {
        x
      }
    }
  }

  /**
   * Returns the next pseudorandom number drawn from a beta distribution
   * with parameters `a` and `b` using numbers from this random number
   * generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p129
   * @param a Positive real number.
   * @param b Positive real number.
   * @return Real number in `(0,1)`
   */
  def nextBeta(a : Double, b : Double) : Double = {
    require(a > 0 && b > 0, "Parameters have to be positive.")
    val (x1, x2) = (nextGamma(a), nextGamma(b))
    x1/(x1 + x2)
  }

  /**
   * Returns the next pseudorandom number drawn from a chi-square
   * distribution with `v` degrees of freedom using numbers from this random
   * number generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p130
   * @param v Positive integer.
   * @return A non-negative real number
   */
  def nextChiSq(v : Int) : Double = {
    require(v > 0, "Parameter has to be positive")
    2 * nextGamma(.5 * v)
  }

  /**
   * Returns the next pseudorandom number drawn from Snedecor's F
   * distribution with `v1, v2` degrees of freedom using numbers from this
   * random number generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p130
   * @param v1 Positive integer.
   * @param v2 Positive integer.
   * @return A non-negative real number
   */
  def nextSnedecorF(v1 : Int, v2 : Int) : Double = {
    require(v1 > 0 && v2 > 0, "Parameters have to be positive")
    (nextChiSq(v1) * v2)/(nextChiSq(v2) * v1)
  }

  /**
   * Returns the next pseudorandom number drawn from a student t distribution
   * with `v` degrees of freedom using numbers from this random number
   * generator's sequence.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p130
   * @param v Positive integer.
   * @return A real number
   */
  def nextStudentT(v : Int) : Double = {
    require(v > 0, "Parameter has to be positive")
    nextGaussian() / sqrt(nextChiSq(v) / v)
  }

  /**
   * Returns the next pseudorandom vector drawn from a dirichlet distribution
   * with parameter `alpha` using numbers from this random number generator's
   * sequence.
   *
   * Taken from [[http://en.wikipedia.org/wiki/Dirichlet_distribution Wikipedia]],
   * accessed March 11th, 2011.
   * @param alpha Vector of positive reals
   * @return Vector of real numbers in `(0,1)`
   */
  def nextDirichlet(alpha : Seq[Double]) : Seq[Double] = {
    require(alpha.size >= 2, "Needs at most two parameters")
    require(alpha.forall(_>0), "Needs positive parameters")
    (0 until alpha.size).map { i => nextGamma(alpha(i)) }.normalize
  }

  /**
   * Returns the next pseudorandom number drawn from the
   * distribution specified by the passed distribution function
   * `f` using numbers from this random number generator's sequence.
   *
   * Not yet implemented
   * @param f Continuous distribution function
   */
  def next(f : Double => Double) : Double = {
    throw new UnsupportedOperationException("Yet to be implemented")
    //TODO implement
  }

  /**
   * Returns a random sample of size `n` of elements from `data` using
   * numbers from this random number generator's sequence. Each
   * element is chosen with equal probability `n / data.size`.
   *
   * Taken from Knuth, The Art of Computer Programming,
   * Vol 2, 2nd edition, p137
   * @param n Positive integer, smaller than data.size
   * @param data Elements to pick from.
   * @return A random sample from `data` of size `n`
   */
  def sample[T, S[X] <: Iterable[X]](n : Int, data : S[T])(implicit bf: CanBuildFrom[S[T], T, S[T]]) : S[T] = {
    require(n >= 0, "Sample size has to be non-negative")
    require(data.size > 0, "Data size has to be positive")
    require(data.size >= n, "Not enough data for sample size")

    val (iter, s) = (data.iterator, data.size)
    var (t, m) = (0, 0)
    val build = bf(data)

    while ( m < n ) {
      assume(iter.hasNext, "But Knuth promises we'd never run out of elements! Doh.")
      val cur = iter.next
      if ( (s - t) * nextUniform < (n - m) ) {
        build += cur
        m += 1
      }
      t += 1
    }

    assert(build.result.size == n, "We sampled the wrong number of elements!")
    build.result
  }
}
