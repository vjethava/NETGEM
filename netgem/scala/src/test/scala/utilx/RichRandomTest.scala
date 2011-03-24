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

import org.scalacheck._

object RichRandomTest extends Properties("RichRandom") {
  import Prop._

  // Utility functions
  def mean[T <% Double](l : Seq[T]) : Double = l.view.map { n => n : Double }.sum / l.size
  def vari[T <% Double](l : Seq[T]) : Double = {
    val m = mean(l) : Double
    l.map { e => (e-m)*(e-m) }.sum / l.size
  }

  def sample[U](n : Int, f : () => U) : Seq[U] = {
    (1 to n).map { _ => f() }
  }

  implicit def double2approx(d : Double) = new AnyRef {
    def approx(c : Double) = (d >= c - .1) && (d <= c + .1)
  }

  def pairComb[T,U](a : Option[T], b : Option[U]) : Option[(T,U)] = (a,b) match {
    case (Some(l), Some(r)) => Some(l, r)
    case _ => None
  }

  def test[T <% Double](f : () => T, m : Double, v : Double, p : (T => Boolean) = (_:T) => true) = {
    // TODO instead of fixed tolerance, use proper intervals
    val s = sample(1000, f)
    /* (mean(s) approx m) && (vari(s) approx v) && */ s.forall(p(_))
  }

  val random = new RichRandom
  import random._

  // Generators
  val posInt = Gen.choose(1, 100)
  val prob = Gen.choose(0.0, 1.0)
  val real = Gen.choose(-100.0, 100.0)
  val properProb = Gen.choose(0.0, 1.0) suchThat { p => p > 0.0 && p < 1 }
  val posReal = Gen.choose(0.0, 100.0) suchThat (_ > 0.0)
  val nonnegReal = Gen.choose(0.0, 100.0)
  val interval = real.combine(real)(pairComb) suchThat { case (a,b) => a <= b }
  val alpha = Gen.listOfN(5,posReal)
  val data = Gen.listOfN(100, Arbitrary.arbString.arbitrary)

  // Properties to test
  property("Geometric") = forAll(properProb){ p =>
    (p > 0.0 && p < 1) ==> test(() => random.nextGeom(p), 1.0/p, (1.0-p)/(p*p), { n : Long => !n.isNaN })
  }

  property("Binomial") = forAll(posInt.combine(prob)(pairComb)){ case (n, p) =>
    (n > 0 && p >= 0 && p <= 1) ==> test(() => random.nextBinom(n,p), n*p, n*p*(1-p), (k:Long) => !k.isNaN && k >= 0 && k <= n)
  }

  property("Poisson") = forAll(posReal){ l =>
    (l > 0) ==> test(() => random.nextPoisson(l), l, l, { n : Long => !n.isNaN })
  }

  property("Uniform [a,b)") = forAll(interval){ case (a, b) =>
    (a <= b) ==> test(() => nextUniform(a, b), (a+b)/2, (b-a)*(b-a)/12, (u:Double) => !u.isNaN && u >= a && u < b)
  }

  property("Uniform [0,1)") = forAll(posInt){ _ =>
    test(() => nextUniform, .5, 1.0/12, (u:Double) => !u.isNaN && u >= 0 && u < 1)
  }

  property("Gaussian") = forAll(real.combine(nonnegReal)(pairComb)) { case (m, s) =>
    (s >= 0) ==> test(() => nextGaussian(m, s), m, s*s, { n : Double => !n.isNaN })
  }

  property("Exponential") = forAll(posReal){ mu =>
    (mu > 0) ==> test(() => nextExponential(mu), mu, mu*mu, { n : Double => !n.isNaN })
  }

  property("Gamma") = forAll(posReal){ a =>
    (a > 0) ==> test(() => nextGamma(a), a, a, { n : Double => !n.isNaN })
  }

  property("Beta") = forAll(posReal.combine(posReal)(pairComb)){ case (a, b) =>
    (a > 0 && b > 0) ==> test(() => nextBeta(a, b), a/(a+b), a*b/((a+b)*(a+b)*(a+b+1)), (c:Double) => !c.isNaN && c > 0 && c < 1)
  }

  property("Chi²") = forAll(posInt){ v =>
    (v > 0) ==> test(() => nextChiSq(v), v, 2*v, { n : Double => !n.isNaN })
  }

  property("Snedecor F") = forAll(posInt.combine(posInt)(pairComb)){ case (v1, v2) =>
    (v1 > 0 && v2 > 4) ==> test(() => nextSnedecorF(v1, v2), (v2 : Double)/(v2 - 2), 2*v2*v2*(v1 + v2 -2)/(v1*(v2-2)*(v2-2)*(v2-4)), { n : Double => !n.isNaN })
  }

  property("Student T") = forAll(posInt){ v =>
    (v > 2) ==> test(() => nextStudentT(v), 0.0, (v:Double)/(v-2), { n : Double => !n.isNaN })
  }

  property("Dirichlet") = forAll(alpha){ a =>
    (a.forall(_>0) && a.size > 1) ==> {
      sample(1000, () => nextDirichlet(a)).flatten.forall(!_.isNaN)
      /*
      val s = sample(1000, () => nextDirichlet(a))
      val a0 = a.sum

      def col(i : Int) = s.map(_(i))

      s.forall(_.size == a.size) && a.zipWithIndex.forall { case (e, i) =>
        val c = col(i)
        (mean(c) approx a(i)/a0) && (vari(c) approx a(i)*(a0 - a(i))/(a0*a0*(a0+1)))
      } */
    }
  }

  property("Sampler") = forAll(posInt.combine(data)(pairComb)){ case (n, d) =>
    (!d.isEmpty && n <= d.size && n >= 0) ==> {
      val s = sample(1000, () => random.sample(n, d))

      def count(e : String, smpl : Seq[Seq[String]]) : Int = {
        smpl.map(_.count(_ == e)).sum
      }

      s.forall(_.size == n) && d.forall(count(_, s) approx (n : Double)/d.size)
    }
  }

  // Multinom
  // nextInt(p : Seq[Double] = IndexedSeq.fill(0)(1.0)) : Int
  // nextGaussian(mu : Seq[Double], sigma : Seq[Seq[Double]])
  // next(f : Double => Double)
}
