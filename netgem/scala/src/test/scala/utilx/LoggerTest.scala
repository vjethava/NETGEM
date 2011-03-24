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

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import Logger.Kind._
import Logger._

class LoggerTest extends WordSpec with MustMatchers {
  "Logger" should {
    "change modes" in {
      var log : Logger.Mode = new Both("testlog")
      Logger.mode = log
      Logger.mode must be theSameInstanceAs (log)
      Logger("Test Both", Note)

      Logger.mode = Shell
      Logger.mode must be theSameInstanceAs (Logger.Shell)
      Logger("Test Shell", Warning)

      log = new File("testlog")
      Logger.mode = log
      Logger.mode must be theSameInstanceAs (log)
      Logger("Test File", Error)

      Logger.mode = new File("testlog")
    }
  }
}
