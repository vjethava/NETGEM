package netgem.output

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

class CsvWriterTest extends WordSpec with MustMatchers {
  val dir = "src/test/resources/"
  val exists = dir + "dummy_nodes"
  val target = "test_output"

  "Setting target files" should {
    "work if target is nonexistent or file" in {
      val w = new CsvWriter

      w.byNameFile = target
      w.byNameFile must be (Some(target))
      w.byNameFile = exists
      w.byNameFile must be (Some(exists))

      w.byScoreFile = target
      w.byScoreFile must be (Some(target))
      w.byScoreFile = exists
      w.byScoreFile must be (Some(exists))

      w.statisticsFile = target
      w.statisticsFile must be (Some(target))
      w.statisticsFile = exists
      w.statisticsFile must be (Some(exists))

      w.graphNodesFile = target
      w.graphNodesFile must be (Some(target))
      w.graphNodesFile = exists
      w.graphNodesFile must be (Some(exists))

      w.graphEdgesFile = target
      w.graphEdgesFile must be (Some(target))
      w.graphEdgesFile = exists
      w.graphEdgesFile must be (Some(exists))
    }

    "fail if target is directory" in {
      val w = new CsvWriter

      w.byNameFile = dir
      w.byNameFile must be (None)

      w.byScoreFile = dir
      w.byScoreFile must be (None)

      w.statisticsFile = dir
      w.statisticsFile must be (None)

      w.graphNodesFile = dir
      w.graphNodesFile must be (None)

      w.graphEdgesFile = dir
      w.graphEdgesFile must be (None)
    }

    "fail if target file not writable" is (pending)
  }

  "Writing by-name edge list" should {
    "work if file set" is (pending)
    "do nothing if file not set" is (pending)
  }

  "Writing by-score edge list" should {
    "work if file set" is (pending)
    "do nothing if file not set" is (pending)
  }

  "Writing statistics" should {
    "work if file set" is (pending)
    "do nothing if file not set" is (pending)
  }

  "Writing node list" should {
    "work if file set" is (pending)
    "do nothing if file not set" is (pending)
  }

  "Writing edge list" should {
    "work if file set" is (pending)
    "do nothing if file not set" is (pending)
  }
}
