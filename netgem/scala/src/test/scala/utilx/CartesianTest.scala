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
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
// TODO move to Scalacheck
class CartesianTest extends WordSpec with MustMatchers {
  "CartesianSeq" should {
    "contain correct elements" in {
      val cart = new CartesianSeq((0 to 5), (6 to 10))
      for ( a <- (0 to 5); b <- (6 to 10) ) { cart must contain (a,b) }
    }
    "have correct order" in {
      val cart = new CartesianSeq((0 to 5), (6 to 10))
      val ref = for ( a <- (0 to 5); b <- (6 to 10) ) yield { (a,b) }
    }
    "have correct size value" in {
      val cart = new CartesianSeq((0 to 5), (6 to 10))
      cart must have size (30)
    }
    "be accessible correctly by apply" in {
      val cart = new CartesianSeq((0 to 5), (6 to 10))
      evaluating { cart(-1) } must produce [Exception]
      var i = 0;
      for ( a <- (0 to 5); b <- (6 to 10) ) { cart(i) must be (a,b); i += 1 }
      evaluating { cart(i) } must produce [Exception]
    }
    "change if mutable parts" is (pending)
  }

  "CartesianSet" should {
    "contain correct elements" in {
      val cart = new CartesianSet((0 to 5).toSet, (6 to 10).toSet)
      for ( a <- (0 to 5); b <- (6 to 10) ) { cart must contain (a,b) }
    }
    "have correct size value" in {
      val cart = new CartesianSet((0 to 5).toSet, (6 to 10).toSet)
      cart must have size (30)
    }
    "have corrent contains" is (pending)
    "have corrent +" is (pending)
    "have corrent -" is (pending)
    "change if mutable parts" is (pending)
  }

  "Convenience factories" should {
    "be equivalent for seqs" in {
      Cartesian((1 to 5), (6 to 10)) must be (new CartesianSeq((1 to 5), (6 to 10)))
      CartesianSeq((1 to 5), (6 to 10)) must be (new CartesianSeq((1 to 5), (6 to 10)))
    }
    "be equivalent for sets" in {
      Cartesian((1 to 5).toSet, (6 to 10).toSet) must be (new CartesianSet((1 to 5).toSet, (6 to 10).toSet))
      CartesianSet((1 to 5).toSet, (6 to 10).toSet) must be (new CartesianSet((1 to 5).toSet, (6 to 10).toSet))
    }
  }

  "Star operator" should {
    "work properly" in {
      (0 to 5) * (6 to 10) must be (new CartesianSeq((0 to 5), (6 to 10)))
    }

    "work properly if nested" in {
      (0 to 5) * (6 to 10) * (11 to 15) must be (new CartesianSeq(new CartesianSeq((0 to 5), (6 to 10)), (11 to 15)))
      (0 to 5) * ((6 to 10) * (11 to 15)) must be (new CartesianSeq((0 to 5), new CartesianSeq((6 to 10), (11 to 15))))
    }
  }
}
