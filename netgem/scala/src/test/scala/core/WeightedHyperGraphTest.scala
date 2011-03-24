package netgem.core

/* Copyright 2011, Raphael Reitzig
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

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec

class WeightedHyperGraphTest extends WordSpec with MustMatchers {

  org.utilx.Logger.mode = org.utilx.Logger.Silent

  val trivial = WeightedHyperGraph[String,Double](
    Seq("c", "a", "d", "b"),
    Seq(("bd",Seq("b","d")),("ab",Seq("a","b")),("ad",Seq("a","d")),("bc",Seq("b","c")),("ac",Seq("c","a")),("cd",Seq("d","c"))),
    _ => Some(Seq(Seq(0.0,1.0,2.0), Seq(2.0,1.0,0.0))),
    Seq("c2", "c1"),
    x  => x match {
      case ("a"|"b") => Some(Seq("c1", "c1"))
      case _         => Some(Seq("c2","c1"))
    },
    0.0
  )


  "A well-formed hypergraph" should {
    "have a structure consistent to its constructor parameters" in {
      trivial.nodes(0).key.isInstanceOf[String] must be (true)
      trivial.edges(0).key.isInstanceOf[String] must be (true)

      trivial.nodes(0).edges.map(_.key) must be (Seq("ab","ac","ad"))
      trivial.nodes(1).edges.map(_.key) must be (Seq("ab","bc","bd"))
      trivial.nodes(2).edges.map(_.key) must be (Seq("ac","bc","cd"))
      trivial.nodes(3).edges.map(_.key) must be (Seq("ad","bd","cd"))

      trivial.edges(0).nodes.map(_.key) must be (Seq("a", "b"))
      trivial.edges(1).nodes.map(_.key) must be (Seq("a", "c"))
      trivial.edges(2).nodes.map(_.key) must be (Seq("a", "d"))
      trivial.edges(3).nodes.map(_.key) must be (Seq("b", "c"))
      trivial.edges(4).nodes.map(_.key) must be (Seq("b", "d"))
      trivial.edges(5).nodes.map(_.key) must be (Seq("c", "d"))

      trivial.nodes(2).edges(0) must be theSameInstanceAs trivial.edges(1)
      trivial.nodes(3).edges(1) must be theSameInstanceAs trivial.edges(4)
      trivial.edges(1).nodes(0) must be theSameInstanceAs trivial.nodes(0)
      trivial.edges(1).nodes(1) must be theSameInstanceAs trivial.nodes(2)

      trivial.nodes.foreach { _.expr must be (Seq(Seq(0.0,1.0,2.0), Seq(2.0,1.0,0.0))) }
      trivial.nodes.foreach { n => n.key match {
        case ("a"|"b") => n.cats must be (Seq("c1"))
        case _         => n.cats must be (Seq("c1", "c2"))
      }}
    }

    "have a node list" in {
      trivial.nodes must not be (null)
    }

    "have a sorted node list" in {
      trivial.nodes.sliding(2).forall {
        case List(a,b) => (a.key : StringKey) <= (b.key : StringKey)
        case _         => true
      } must be (true)
    }

    "have an edge list" in {
      trivial.edges must not be (null)
    }

    "have a sorted edge list" in {
      trivial.edges.sliding(2).forall {
        case List(a,b) => (a.key : StringKey) <= (b.key : StringKey)
        case _         => true
      } must be (true)
    }

    "have a category list" in {
      trivial.categories must not be (null)
    }

    "have a sorted category list" in {
      trivial.categories.sliding(2).forall {
        case List(a,b) => (a.key : StringKey) <= (b.key : StringKey)
        case _         => true
      } must be (true)
    }
  }

  "All nodes" should {
    "have edges" in {
      trivial.nodes.foreach { n =>
        n.edges must not be (null)
        n.edges must not be ('empty)
      }
    }

    "have sorted edge list" in {
      trivial.nodes.foreach { n =>
        n.edges.sliding(2).forall {
          case List(a,b) => (a.key : StringKey) <= (b.key : StringKey)
          case _         => true
        } must be (true)
      }
    }

    "have every edge only once" in {
      trivial.nodes.foreach { n => n.edges.distinct must have size (n.edges.size) }
    }

    "have expressions" in {
      trivial.nodes.foreach { n =>
        n.expr must not be (null)
        n.expr must not be ('empty)
      }
    }

    "the correct expressions" in {
      trivial.nodes.foreach { n => n.expr must be === Seq(Seq(0.0,1.0,2.0), Seq(2.0,1.0,0.0)) }
    }

    "have the same number of expressions" in {
      trivial.nodes.foreach { n =>
        n.expr must not be (null)
        n.expr must have size (2)
        n.expr.foreach { _ must have size (3) }
      }
    }

    "have categories" in {
      trivial.nodes.foreach { n =>
        n.cats must not be (null)
        n.cats must not be ('empty)
      }
    }

    "have every category only once" in {
      trivial.nodes.foreach { n => n.cats.distinct must have size (n.cats.size) }
    }

    "have sorted category list" in {
      trivial.nodes.foreach { n =>
        n.cats.sliding(2).forall {
          case List(a,b) => (a.key : StringKey) <= (b.key : StringKey)
          case _         => true
        } must be (true)
      }
    }

    "have pairwise different identifiers" in {
      val keys = trivial.nodes.map(_.key)
      keys.distinct must have size (keys.size)
    }
  }

  "All edges" should {
    "have nodes" in {
      trivial.edges.foreach { e =>
        e.nodes must not be (null)
        e.nodes must not be ('empty)
      }
    }

    "have sorted node list" in {
      trivial.edges.foreach { e =>
        e.nodes.sliding(2).forall {
          case List(a,b) => (a.key : StringKey) <= (b.key : StringKey)
          case _         => true
        } must be (true)
      }
    }

    "have every node only once" in {
      trivial.edges.foreach(e => e.nodes.distinct must have size (e.nodes.size))
    }

    "have default weight" in {
      trivial.edges.foreach(e => {
        e.weight must be (0.0)
      })
    }

    "have mutable weight" in {
      trivial.edges.foreach(_.weight = 1.0)
    }

    "have pairwise different identifiers" in {
      val keys = trivial.edges.map(_.key)
      keys.distinct must have size (keys.size)
    }
  }

  "Categories" should {
    "have pairwise different identifiers" in {
      trivial.categories.distinct must have size (trivial.categories.size)
    }
  }

  "Nodes without categories" should {
    "be deleted (with chain reaction)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => x match {
          case "a" => None
          case _   => Some(Seq("c1"))
        },
        0.0
      )

      g.nodes must have size (2)
      g.nodes(0).key must be ("b")
      g.nodes(1).key must be ("c")
      g.edges must have size (1)
      g.edges(0).key must be ("bc")
    }
  }

  "Nodes without expressions" should {
    "be deleted (None, with chain reaction)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => x match {
          case "a" => None
          case _   => Some(Seq(Seq(0.0,1.0,2.0)))
        },
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must have size (2)
      g.nodes(0).key must be ("b")
      g.nodes(1).key must be ("c")
      g.edges must have size (1)
      g.edges(0).key must be ("bc")
    }

    "be deleted (Empty, with chain reaction)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => x match {
          case "a" => Some(Seq())
          case _   => Some(Seq(Seq(0.0,1.0,2.0)))
        },
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must have size (2)
      g.nodes(0).key must be ("b")
      g.nodes(1).key must be ("c")
      g.edges must have size (1)
      g.edges(0).key must be ("bc")
    }

    "be deleted (Empty strains, with chain reaction)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => x match {
          case "a" => Some(Seq(Seq(),Seq()))
          case _   => Some(Seq(Seq(0.0,1.0,2.0)))
        },
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must have size (2)
      g.nodes(0).key must be ("b")
      g.nodes(1).key must be ("c")
      g.edges must have size (1)
      g.edges(0).key must be ("bc")
    }
  }

  "Different expression list lengths" should {
    "be leveled out" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => x match {
          case "a" => Some(Seq(Seq(0.0),Seq(0.0,1.0)))
          case "c" => Some(Seq(Seq(0.0,1.0,2.0),Seq(1.0)))
          case _   => Some(Seq(Seq(0.0,1.0),Seq(0.0,1.0,2.0)))
        },
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes.foreach { _.expr.foreach { _ must have size (3) }}
    }
  }

  "Different number of strains" should {
    "leveled out" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => x match {
          case "a" => Some(Seq(Seq(0.0)))
          case "c" => Some(Seq(Seq(0.0,1.0,2.0),Seq(1.0), Seq(0.0)))
          case _   => Some(Seq(Seq(0.0,1.0),Seq(0.0,1.0,2.0)))
        },
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes.foreach { _.expr must have size (3) }
      g.nodes.foreach { _.expr.foreach { _ must have size (3) }}
    }
  }

  "Duplicate identifiers" should {
    "be deleted on nodes" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "a", "b"),
        Seq(("ab",Seq("a","b"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must have size (2)
      g.nodes(0).key must be ("a")
      g.nodes(1).key must be ("b")
    }

    "be renamed on edges" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c"),
        Seq(("ab",Seq("a","b")),("ab",Seq("b","c"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.edges must have size (2)
      g.edges(0).key must be ("ab_0")
      g.edges(1).key must be ("ab_1")
    }

    "be dropped on categories" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c"),
        Seq(("ab",Seq("a","b")),("bc",Seq("b","c"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1", "c1"),
        x => Some(Seq("c1", "c1")),
        0.0
      )

      g.categories must have size (1)
      g.nodes(0).cats must be (Seq("c1"))
    }
  }

  "Dead references to nodes" should {
    "be ignored" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b"),
        Seq(("ab",Seq("a","b")),("ac",Seq("a","c"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.edges must have size (1)
      g.edges(0).key must be ("ab")
    }
  }


  "Dead references to categories" should {
    "be ignored" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c")),("ad",Seq("a","d"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => x match {
          case "a" => Some(Seq("c1", "c2"))
          case _   => Some(Seq("c1"))
        },
        0.0
      )

      g.nodes(0).key must be ("a")
      g.nodes(0).cats must be (Seq("c1"))
    }

    "lead to delection if none left" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c"),
        Seq(("ab",Seq("a","b")), ("bc",Seq("b","c"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => x match {
          case "a" => Some(Seq("c2"))
          case _   => Some(Seq("c1"))
        },
        0.0
      )

      g.nodes(0).key must be ("b")
      g.edges must have size (1)
    }
  }

  "Missing input" should {
    "result in empty graph (no nodes)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq(),
        Seq(("ab",Seq("a","b"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must be ('empty)
      g.edges must be ('empty)
    }

    "result in empty graph (no edges)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b"),
        Seq(),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must be ('empty)
      g.edges must be ('empty)
    }

    "result in empty graph (no expressions)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b"),
        Seq(("ab",Seq("a","b"))),
        x => None,
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must be ('empty)
      g.edges must be ('empty)
    }

    "result in empty graph (no categories)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b"),
        Seq(("ab",Seq("a","b"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq(),
        x => Some(Seq("c1")),
        0.0
      )

      g.nodes must be ('empty)
      g.edges must be ('empty)
    }

    "result in empty graph (no category association)" in {
      val g = WeightedHyperGraph[String,Double](
        Seq("a", "b"),
        Seq(("ab",Seq("a","b"))),
        x => Some(Seq(Seq(0.0,1.0,2.0))),
        Seq("c1"),
        x => None,
        0.0
      )

      g.nodes must be ('empty)
      g.edges must be ('empty)
    }
  }

  "Breadth first iterator" should {
    val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d", "e", "f", "g"),
        Seq(("ab",Seq("a","b")),("ac",Seq("a","c")),("ad",Seq("a","d")),
            ("be",Seq("b","e")),("ce",Seq("c","e")),("bg",Seq("b","g")),
            ("dg",Seq("d","g")),("ef",Seq("e","f")),("fg",Seq("f","g"))),
        x => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

    val a = g.nodes(0)
    assert(a.key == "a")

    "maintain correct order" in {
      g.breadthFirst(a).map(_.key).toSeq must be === Seq("a", "b", "c", "d", "e", "g", "f")
    }
    "have same order in both versions" in {
      g.breadthFirst(a).map(_.key).toSeq must be === g.breadthFirstWithDepth(a).map(_._1.key).toSeq
    }
    "yield correct depths" in {
      g.breadthFirstWithDepth(a).map(_._2).toSeq must be === Seq(0,1,1,1,2,2,3)
    }
    "stop correctly" in {
      g.breadthFirstWithDepth(a, (n,d) => d >= 2).map(_._1.key).toSeq must be  === Seq("a", "b", "c", "d", "e", "g")
    }
  }

  "Depth first iterator" should {
    val g = WeightedHyperGraph[String,Double](
        Seq("a", "b", "c", "d", "e", "f", "g"),
        Seq(("ab",Seq("a","b")),("ac",Seq("a","c")),("ad",Seq("a","d")),
            ("be",Seq("b","e")),("ce",Seq("c","e")),("bg",Seq("b","g")),
            ("dg",Seq("d","g")),("ef",Seq("e","f")),("fg",Seq("f","g"))),
        x => Some(Seq(Seq(0.0))),
        Seq("c1"),
        x => Some(Seq("c1")),
        0.0
      )

    val a = g.nodes(0)
    assert(a.key == "a")

    "maintain correct order" in {
      g.depthFirst(a).map(_.key).toSeq must be === Seq("a", "b", "e", "c", "f", "g", "d")
    }
    "have same order in both versions" in {
      g.depthFirst(a).map(_.key).toSeq must be === g.depthFirstWithDepth(a).map(_._1.key).toSeq
    }
    "yield correct depths" in {
      g.depthFirstWithDepth(a).map(_._2).toSeq must be === Seq(0,1,2,3,3,4,5)
    }
    "stop correctly" in {
      g.depthFirstWithDepth(a, (n,d) => d >= 3).map { case (n,d) => (n.key, d) }.
        toSeq must be  === Seq(("a",0), ("b",1), ("e",2), ("c",3), ("f",3), ("g",2), ("d",3))
      g.depthFirstWithDepth(a, (n,d) => n.key == "b" || d >= 3).map { case (n,d) => (n.key, d) }.
        toSeq must be  === Seq(("a",0), ("b",1), ("c",1), ("e",2),("f",3),("d",1),("g",2))
    }
  }
}
