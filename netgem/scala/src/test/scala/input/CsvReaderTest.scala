package netgem.input

/* Copyright 2011, Raphael Reitzig
 *
 * This file is part of netgem.input.
 *
 * netgem.input is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.input is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem.input. If not, see <http://www.gnu.org/licenses/>.
 */

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

class CsvReaderTest extends WordSpec with MustMatchers {
  val pre = "src/test/resources/"
  val nodes = pre + "dummy_nodes"
  val edges = pre + "dummy_edges"
  val expr = pre + "dummy_expr"
  val expr_err = pre + "dummy_expr_err"
  val cats = pre + "dummy_cats"
  val cmap = pre + "dummy_cmap"
  val failer = "dsads/dsnaofwsa"

  def baseReader : CsvReader = {
    val reader = new CsvReader
    reader.edgeFile = edges
    reader.exprFile = expr
    reader.cmapFile = cmap
    return reader
  }

  "Setting directory as source" should {
    "fail" in {
      val reader = new CsvReader
      reader.nodeFile = pre
      reader.edgeFile = pre
      reader.exprFile = pre
      reader.catsFile = pre
      reader.cmapFile = pre

      reader.nodeFile must be (None)
      reader.edgeFile must be (None)
      reader.exprFile must be (None)
      reader.catsFile must be (None)
      reader.cmapFile must be (None)
    }
  }

  "Setting file as source" should {
    "work if exists" in {
      val reader = new CsvReader
      reader.nodeFile = nodes
      reader.edgeFile = nodes
      reader.exprFile = nodes
      reader.catsFile = nodes
      reader.cmapFile = nodes

      reader.nodeFile must be (Some(nodes))
      reader.edgeFile must be (Some(nodes))
      reader.exprFile must be (Some(nodes))
      reader.catsFile must be (Some(nodes))
      reader.cmapFile must be (Some(nodes))
    }

    "fail if not exists" in {
      val reader = new CsvReader
      reader.nodeFile = failer
      reader.edgeFile = failer
      reader.exprFile = failer
      reader.catsFile = failer
      reader.cmapFile = failer

      reader.nodeFile must be (None)
      reader.edgeFile must be (None)
      reader.exprFile must be (None)
      reader.catsFile must be (None)
      reader.cmapFile must be (None)
    }

    "fail if target file not readable" is (pending)
  }

  "Setting strain length" should {
    "fail if smaller than 0" in {
      val reader = new CsvReader
      reader.strainsLength = -5
      reader.strainsLength must be (None)
      reader.strainsLength = -1
      reader.strainsLength must be (None)
      reader.strainsLength = 1
      reader.strainsLength = -5
      reader.strainsLength must be (Some(1))
    }

    "succeed if at least 0" in {
      val reader = new CsvReader
      reader.strainsLength = 0
      reader.strainsLength must be (Some(0))
      reader.strainsLength = 1
      reader.strainsLength must be (Some(1))
      reader.strainsLength = 5
      reader.strainsLength must be (Some(5))
    }
  }

  "Result members" should {
    "be None before reading" in {
      val r = baseReader
      r.nodes must be (None)
      r.edges must be (None)
      r.exprs must be (None)
      r.cats must be (None)
      r.catsMap must be (None)
    }
  }

  "Reading with all files" should {
    "produce correct values" in {
      val r = baseReader
      r.nodeFile = nodes
      r.catsFile = cats

      r.read

      r.nodes must be (Some(Seq("a","b","c","d","f","g","h","a")))
      r.edges must be (Some(Seq(("ab", Seq("b","a")),
                                ("ac", Seq("a","c")),
                                ("bc", Seq("c","b")),
                                ("ad", Seq("a","d")),
                                ("de", Seq("d","e")),
                                ("af", Seq("a","f")),
                                ("fg", Seq("f","g")),
                                ("bc", Seq("b","c")))))
      r.exprs must be (Some(Map("a" -> Seq(Seq(1.0,2.0,3.0)),
                                "b" -> Seq(Seq(3.0,2.0,1.0)),
                                "c" -> Seq(Seq(0.0,0.0,0.0)),
                                "d" -> Seq(Seq(0.0)),
                                "e" -> Seq(Seq(0.0,1.0)),
                                "g" -> Seq(Seq(0.0)))))
      r.cats  must be (Some(Seq("c1","c2","c3","c4","c2")))
      r.catsMap must be (Some(Map("a" -> Seq("c2"),
                                  "b" -> Seq("c2"),
                                  "c" -> Seq("c1","c2"),
                                  "e" -> Seq("c2"),
                                  "f" -> Seq("c1"),
                                  "g" -> Seq("c2","c4"))))
    }
  }

  "Reading without nodes file" should {
    val r = baseReader
    r.catsFile = cats
    r.read

    "produce inferred nodes" in {
      r.nodes.get.sorted must be (Seq("a","b","c","d","e","f","g"))
    }
    "produce rest correctly" in {
      r.edges must be (Some(Seq(("ab", Seq("b","a")),
                                ("ac", Seq("a","c")),
                                ("bc", Seq("c","b")),
                                ("ad", Seq("a","d")),
                                ("de", Seq("d","e")),
                                ("af", Seq("a","f")),
                                ("fg", Seq("f","g")),
                                ("bc", Seq("b","c")))))
      r.exprs must be (Some(Map("a" -> Seq(Seq(1.0,2.0,3.0)),
                                "b" -> Seq(Seq(3.0,2.0,1.0)),
                                "c" -> Seq(Seq(0.0,0.0,0.0)),
                                "d" -> Seq(Seq(0.0)),
                                "e" -> Seq(Seq(0.0,1.0)),
                                "g" -> Seq(Seq(0.0)))))
      r.cats  must be (Some(Seq("c1","c2","c3","c4","c2")))
      r.catsMap must be (Some(Map("a" -> Seq("c2"),
                                  "b" -> Seq("c2"),
                                  "c" -> Seq("c1","c2"),
                                  "e" -> Seq("c2"),
                                  "f" -> Seq("c1"),
                                  "g" -> Seq("c2","c4"))))
    }
  }

  "Reading without category file" should {
    val r = baseReader
    r.nodeFile = nodes
    r.read

    "produce inferred categories" in {
      r.cats.get.sorted must be (Seq("c1","c2","c4"))
    }
    "produce rest correctly" in {
      r.nodes must be (Some(Seq("a","b","c","d","f","g","h","a")))
      r.edges must be (Some(Seq(("ab", Seq("b","a")),
                                ("ac", Seq("a","c")),
                                ("bc", Seq("c","b")),
                                ("ad", Seq("a","d")),
                                ("de", Seq("d","e")),
                                ("af", Seq("a","f")),
                                ("fg", Seq("f","g")),
                                ("bc", Seq("b","c")))))
      r.exprs must be (Some(Map("a" -> Seq(Seq(1.0,2.0,3.0)),
                                "b" -> Seq(Seq(3.0,2.0,1.0)),
                                "c" -> Seq(Seq(0.0,0.0,0.0)),
                                "d" -> Seq(Seq(0.0)),
                                "e" -> Seq(Seq(0.0,1.0)),
                                "g" -> Seq(Seq(0.0)))))
      r.catsMap must be (Some(Map("a" -> Seq("c2"),
                                  "b" -> Seq("c2"),
                                  "c" -> Seq("c1","c2"),
                                  "e" -> Seq("c2"),
                                  "f" -> Seq("c1"),
                                  "g" -> Seq("c2","c4"))))
    }
  }

  "Reading with minimal files" should {
    val r = baseReader
    r.read

    "produce inferred nodes" in {
      r.nodes.get.sorted must be (Seq("a","b","c","d","e","f","g"))
    }
    "produce inferred categories" in {
      r.cats.get.sorted must be (Seq("c1","c2","c4"))
    }
    "produce rest correctly" in {
      r.edges must be (Some(Seq(("ab", Seq("b","a")),
                                ("ac", Seq("a","c")),
                                ("bc", Seq("c","b")),
                                ("ad", Seq("a","d")),
                                ("de", Seq("d","e")),
                                ("af", Seq("a","f")),
                                ("fg", Seq("f","g")),
                                ("bc", Seq("b","c")))))
      r.exprs must be (Some(Map("a" -> Seq(Seq(1.0,2.0,3.0)),
                                "b" -> Seq(Seq(3.0,2.0,1.0)),
                                "c" -> Seq(Seq(0.0,0.0,0.0)),
                                "d" -> Seq(Seq(0.0)),
                                "e" -> Seq(Seq(0.0,1.0)),
                                "g" -> Seq(Seq(0.0)))))
      r.catsMap must be (Some(Map("a" -> Seq("c2"),
                                  "b" -> Seq("c2"),
                                  "c" -> Seq("c1","c2"),
                                  "e" -> Seq("c2"),
                                  "f" -> Seq("c1"),
                                  "g" -> Seq("c2","c4"))))
    }
  }

  "Reading multiple strains" should {
    "split correctly (2)" in {
      val r = baseReader
      r.strainsLength = 2
      r.read

      r.exprs must be (Some(Map("a" -> Seq(Seq(1.0,2.0),Seq(3.0)),
                                "b" -> Seq(Seq(3.0,2.0),Seq(1.0)),
                                "c" -> Seq(Seq(0.0,0.0),Seq(0.0)),
                                "d" -> Seq(Seq(0.0)),
                                "e" -> Seq(Seq(0.0,1.0)),
                                "g" -> Seq(Seq(0.0)))))
    }

    "split correctly (1)" in {
      val r = baseReader
      r.strainsLength = 1
      r.read

      r.exprs must be (Some(Map("a" -> Seq(Seq(1.0),Seq(2.0),Seq(3.0)),
                                "b" -> Seq(Seq(3.0),Seq(2.0),Seq(1.0)),
                                "c" -> Seq(Seq(0.0),Seq(0.0),Seq(0.0)),
                                "d" -> Seq(Seq(0.0)),
                                "e" -> Seq(Seq(0.0),Seq(1.0)),
                                "g" -> Seq(Seq(0.0)))))
    }
  }

  "Reading without edges file" should {
    "throw exception" in {
      val r = new CsvReader
      r.edgeFile = failer
      r.exprFile = expr
      r.cmapFile = cmap
      evaluating { r.read } must produce [Exception]
    }
  }

  "Reading without expression file" should {
    "throw exception" in {
      val r = new CsvReader
      r.edgeFile = edges
      r.exprFile = failer
      r.cmapFile = cmap
      evaluating { r.read } must produce [Exception]
    }
  }

  "Reading without category map file" should {
    "throw exception" in {
      val r = new CsvReader
      r.edgeFile = edges
      r.exprFile = expr
      r.cmapFile = failer
      evaluating { r.read } must produce [Exception]
    }
  }

  "Reading with non-numerical expressions" should {
    "throw exception" in {
      val r = baseReader
      r.exprFile = expr_err
      evaluating { r.read } must produce [java.lang.NumberFormatException]
    }
  }
}
