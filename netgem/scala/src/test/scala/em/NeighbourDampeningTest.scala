package netgem.em

/* Copyright 2011, Raphael Reitzig
*
* This file is part of netgem.em
*
* netgem.em is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* netgem.em is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with netgem.em. If not, see <http://www.gnu.org/licenses/>.
*/

import org.scalacheck._
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

import netgem.core.WeightedHyperGraph
import org.mathx._

class NeighbourDampeningTest extends WordSpec with MustMatchers {
  org.utilx.Logger.mode = org.utilx.Logger.Silent

  "NeighbourDampening" should {
    "yield correct values" in {
      val linear = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d", "e"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c")),("cd",Seq("d","c")),("de",Seq("d","e"))),
        _ => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x  => Some(Seq("c1")),
        0.0
      )
      val (a,b,c,d,e) = (linear.nodes(0),linear.nodes(1),linear.nodes(2),linear.nodes(3),linear.nodes(4))
      val (ab,bc,cd,de) = (linear.edges(0),linear.edges(1),linear.edges(2),linear.edges(3))

      var damp = new NeighbourDampening(linear, Seq(Seq(a)), 4)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0)
      damp(0,cd) must be (3.0/4.0 * 7.0/8.0)
      damp(0,de) must be (7.0/8.0 * 15.0/16.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a)), 3)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0)
      damp(0,cd) must be (3.0/4.0 * 7.0/8.0)
      damp(0,de) must be (7.0/8.0 * 1.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a)), 2)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0)
      damp(0,cd) must be (3.0/4.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a)), 1)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(0,bc) must be (1.0/2.0 * 1.0)
      damp(0,cd) must be (1.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a)), 0)
      damp(0,ab) must be (0.0 * 1.0)
      damp(0,bc) must be (1.0 * 1.0)
      damp(0,cd) must be (1.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0)
    }

    "use shortest paths only" in {
      val circle = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c")),("ac",Seq("a","c"))),
        _ => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x  => Some(Seq("c1")),
        0.0
      )
      val (a,b,c) = (circle.nodes(0),circle.nodes(1),circle.nodes(2))
      val (ab,ac,bc) = (circle.edges(0),circle.edges(1),circle.edges(2))


      var damp = new NeighbourDampening(circle, Seq(Seq(a)), 2)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(0,bc) must be (1.0/2.0 * 1.0/2.0)
      damp(0,ac) must be (0.0 * 1.0/2.0)

      damp = new NeighbourDampening(circle, Seq(Seq(a)), 1)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(0,bc) must be (1.0/2.0 * 1.0/2.0)
      damp(0,ac) must be (0.0 * 1.0/2.0)

      damp = new NeighbourDampening(circle, Seq(Seq(a)), 0)
      damp(0,ab) must be (0.0 * 1.0)
      damp(0,bc) must be (1.0 * 1.0)
      damp(0,ac) must be (0.0 * 1.0)
    }

    "handle multiple knock-outs properly" in {
      val linear = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d", "e"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c")),("cd",Seq("d","c")),("de",Seq("d","e"))),
        _ => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x  => Some(Seq("c1")),
        0.0
      )
      val (a,b,c,d,e) = (linear.nodes(0),linear.nodes(1),linear.nodes(2),linear.nodes(3),linear.nodes(4))
      val (ab,bc,cd,de) = (linear.edges(0),linear.edges(1),linear.edges(2),linear.edges(3))

      var damp = new NeighbourDampening(linear, Seq(Seq(a, e)), 4)
      damp(0,ab) must be (0.0 * 1.0/2.0        *  7.0/8.0 * 15.0/16.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0    *  3.0/4.0 * 7.0/8.0)
      damp(0,cd) must be (3.0/4.0 * 7.0/8.0    *  1.0/2.0 * 3.0/4.0)
      damp(0,de) must be (7.0/8.0 * 15.0/16.0  *  0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a, e)), 3)
      damp(0,ab) must be (0.0 * 1.0/2.0     * 7.0/8.0 * 1.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0 * 3.0/4.0 * 7.0/8.0)
      damp(0,cd) must be (3.0/4.0 * 7.0/8.0 * 1.0/2.0 * 3.0/4.0)
      damp(0,de) must be (7.0/8.0 * 1.0     * 0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a, e)), 2)
      damp(0,ab) must be (0.0 * 1.0/2.0     * 1.0 * 1.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0 * 3.0/4.0 * 1.0)
      damp(0,cd) must be (3.0/4.0 * 1.0     * 1.0/2.0 * 3.0/4.0)
      damp(0,de) must be (1.0 * 1.0         * 0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a, e)), 1)
      damp(0,ab) must be (0.0 * 1.0/2.0 * 1.0 * 1.0)
      damp(0,bc) must be (1.0/2.0 * 1.0 * 1.0 * 1.0)
      damp(0,cd) must be (1.0 * 1.0     * 1.0/2.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0     * 0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a, e)), 0)
      damp(0,ab) must be (0.0 * 1.0   * 1.0 * 1.0)
      damp(0,bc) must be (1.0 * 1.0   * 1.0 * 1.0)
      damp(0,cd) must be (1.0 * 1.0   * 1.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0   * 0.0 * 1.0)
    }

    "handle multiple strains properly" in {
      val linear = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d", "e"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c")),("cd",Seq("d","c")),("de",Seq("d","e"))),
        _ => Some(Seq(Seq(0.0), Seq(0.0))),
        Seq("c1"),
        x  => Some(Seq("c1")),
        0.0
      )
      val (a,b,c,d,e) = (linear.nodes(0),linear.nodes(1),linear.nodes(2),linear.nodes(3),linear.nodes(4))
      val (ab,bc,cd,de) = (linear.edges(0),linear.edges(1),linear.edges(2),linear.edges(3))

      var damp = new NeighbourDampening(linear, Seq(Seq(a), Seq(e)), 4)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(1,ab) must be (7.0/8.0 * 15.0/16.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0)
      damp(1,bc) must be (3.0/4.0 * 7.0/8.0)
      damp(0,cd) must be (3.0/4.0 * 7.0/8.0)
      damp(1,cd) must be (1.0/2.0 * 3.0/4.0)
      damp(0,de) must be (7.0/8.0 * 15.0/16.0)
      damp(1,de) must be (0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a), Seq(e)), 3)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(1,ab) must be (7.0/8.0 * 1.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0)
      damp(1,bc) must be (3.0/4.0 * 7.0/8.0)
      damp(0,cd) must be (3.0/4.0 * 7.0/8.0)
      damp(1,cd) must be (1.0/2.0 * 3.0/4.0)
      damp(0,de) must be (7.0/8.0 * 1.0)
      damp(1,de) must be (0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a), Seq(e)), 2)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(1,ab) must be (1.0 * 1.0)
      damp(0,bc) must be (1.0/2.0 * 3.0/4.0)
      damp(1,bc) must be (3.0/4.0 * 1.0)
      damp(0,cd) must be (3.0/4.0 * 1.0)
      damp(1,cd) must be (1.0/2.0 * 3.0/4.0)
      damp(0,de) must be (1.0 * 1.0)
      damp(1,de) must be (0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a), Seq(e)), 1)
      damp(0,ab) must be (0.0 * 1.0/2.0)
      damp(1,ab) must be (1.0 * 1.0)
      damp(0,bc) must be (1.0/2.0 * 1.0)
      damp(1,bc) must be (1.0 * 1.0)
      damp(0,cd) must be (1.0 * 1.0)
      damp(1,cd) must be (1.0/2.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0)
      damp(1,de) must be (0.0 * 1.0/2.0)

      damp = new NeighbourDampening(linear, Seq(Seq(a), Seq(e)), 0)
      damp(0,ab) must be (0.0 * 1.0)
      damp(1,ab) must be (1.0 * 1.0)
      damp(0,bc) must be (1.0 * 1.0)
      damp(1,bc) must be (1.0 * 1.0)
      damp(0,cd) must be (1.0 * 1.0)
      damp(1,cd) must be (1.0 * 1.0)
      damp(0,de) must be (1.0 * 1.0)
      damp(1,de) must be ( 0.0 * 1.0)
    }

    "handle hyperedges properly" in {
      val hyperG = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c")),("cd",Seq("d","c")),("hyper",Seq("b","c","d"))),
        _ => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x  => Some(Seq("c1")),
        0.0
      )
      val (a,b,c,d) = (hyperG.nodes(0),hyperG.nodes(1),hyperG.nodes(2),hyperG.nodes(3))
      val hyper = hyperG.edges(3)

      var damp = new NeighbourDampening(hyperG, Seq(Seq(a)), 4)
      damp(0,hyper) must be (1.0/2.0 * 3.0/4.0 * 3.0/4.0)
      damp = new NeighbourDampening(hyperG, Seq(Seq(a)), 3)
      damp(0,hyper) must be (1.0/2.0 * 3.0/4.0 * 3.0/4.0)
      damp = new NeighbourDampening(hyperG, Seq(Seq(a)), 2)
      damp(0,hyper) must be (1.0/2.0 * 3.0/4.0 * 3.0/4.0)
      damp = new NeighbourDampening(hyperG, Seq(Seq(a)), 1)
      damp(0,hyper) must be (1.0/2.0 * 1.0 * 1.0)
      damp = new NeighbourDampening(hyperG, Seq(Seq(a)), 0)
      damp(0,hyper) must be (1.0 * 1.0 * 1.0)
    }

    // This test might fail if elidible methods are removed
    "reject implausible inputs" in {
      val linear = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d", "e"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c")),("cd",Seq("d","c")),("de",Seq("d","e"))),
        _ => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x  => Some(Seq("c1")),
        0.0
      )

      evaluating { new NeighbourDampening(linear, Seq(), 0) } must produce [Exception]
      evaluating { new NeighbourDampening(linear, Seq(Seq(), Seq()), 0) } must produce [Exception]
      evaluating { new NeighbourDampening(linear, Seq(Seq()), -1) } must produce [Exception]
    }
  }
}
