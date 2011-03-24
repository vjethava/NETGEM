package netgem.output

/* Copyright 2011, Raphael Reitzig
*
* This file is part of netgem.output.
*
* netgem.output is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* netgem.output is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with netgem.output. If not, see <http://www.gnu.org/licenses/>.
*/

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import netgem.core._
import netgem.core.string2key
import scala.collection.mutable.ArrayBuffer

class DiscreteStatisticsTest extends WordSpec with MustMatchers {
  import DiscreteStatistics._

  // A trivial instance with dummy results
  private val trivial = WeightedHyperGraph[String,Seq[Double]](
    Seq("a", "b", "c", "d"),
    Seq(("ab",Seq("a","b")),("ac",Seq("a","b")),("ad",Seq("a","d")),
        ("bc",Seq("b","c")),("bd",Seq("b","d")),("cd",Seq("c","d"))),
    x => Some(Seq(Seq(0.0,0.0))),
    Seq("c1"),
    x => Some(Seq("c1")),
    Seq(0.0)
  )

  // An empty graph
  private val empty = WeightedHyperGraph[String,Seq[Double]](Seq(), Seq(), x => Some(Seq(Seq(0.0))), Seq(), x => Some(Seq()),Seq(0.0))

  // A non-trivial graph
  private val nontrivial = WeightedHyperGraph[String,Seq[Double]](
  Seq("a", "b", "c", "d"),
    Seq(("ab",Seq("a","b")),("ac",Seq("a","b")),("ad",Seq("a","d")),
        ("bc",Seq("b","c")),("bd",Seq("b","d")),("cd",Seq("c","d"))),
    x => Some(Seq(Seq(0.0,0.0,0.0))),
    Seq("c1"),
    x => Some(Seq("c1")),
    Seq(0.0)
  )

  "Statistics" should {
    "have a non-empty statistics set" in {
      codes must not be ('empty)
    }

    "yield values for all listed statistics on valid graphs" in {
      trivial.edges.foreach { e =>
        e.weight = ArrayBuffer(1.0,1.0,1.0)
      }
      val fcts = compute(trivial)_
      codes.foreach( x => {
        fcts(x) must not be (None)
      })
    }

    "reject missing statistics safely" in {
      compute(trivial)("xyz") must be (None)
    }

    "compute change score correctly" in {
      changeScore(Seq(1.0,1.0,1.0))  must be (Some(0.0 / 3))
      changeScore(Seq(1.0,2.0,1.0))  must be (Some(2.0 / 3))
      changeScore(Seq(1.0,-1.0,1.0)) must be (Some(8.0 / 3))
      changeScore(Seq(1.0))          must be (None)
      changeScore(Seq())             must be (None)
    }

    "compute trivial case correctly" in {
      val fcts = compute(trivial)_
      fcts("mean") must be (Some(0.0))
      fcts("stdv") must be (Some(0.0))
      fcts("var")  must be (Some(0.0))
    }

    "compute non-trivial case correctly" in {
      nontrivial.edges(0).weight = ArrayBuffer(1.0,1.0,1.0)
      nontrivial.edges(1).weight = ArrayBuffer(1.0,2.0,3.0)
      nontrivial.edges(2).weight = ArrayBuffer(1.0,3.0,6.0)
      nontrivial.edges(3).weight = ArrayBuffer(1.0,-1.0,1.0)
      nontrivial.edges(4).weight = ArrayBuffer(1.0,1.0,1.0)
      nontrivial.edges(5).weight = ArrayBuffer(1.0,1.0,1.0)

      // Test that multiple execution does not change anything
      val fcts = compute(nontrivial)_
      (0 to 5).foreach( { _ =>
        fcts("mean") match {
          case Some(d) => d must be (3.83333/3 plusOrMinus 0.0001)
          case None    => fail("Mean may not be None")
        }
      })

      (0 to 5).foreach( { _ =>
        fcts("stdv") match {
          case Some(d) => d must be (4.98051/3 plusOrMinus 0.0001)
          case None    => fail("Standard deviation may not be None")
        }
      })

      (0 to 5).foreach( { _ =>
        fcts("var")  match {
          case Some(d) => d must be (24.80555/9 plusOrMinus 0.0001)
          case None    => fail("Variance may not be None")
        }
      })
    }

    "handle graph without edges" in {
      val fcts = compute(empty)_
      fcts("mean") must be (None)
      fcts("stdv") must be (None)
      fcts("var")  must be (None)
    }

    "provide meaningful pretty strings" in {
      codes.foreach( e => {
        pretty(e) must not be (null)
        pretty(e) must not be ("unknown")
      })
    }
  }
}
