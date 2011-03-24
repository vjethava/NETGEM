package netgem

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.
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

import netgem.frontend._
import org.utilx.Logger

/**
 * Executes NETGEM in a certain flavor. Consider documentation of
 * [[netgem.Main#main]] for usage details.
 * @author Raphael Reitzig
 */
object Main {

  private implicit def strings2container(s : Seq[String]) : NetgemContainer = {
    s match {
      case Seq("original", _*) => Netgem
      // Add new flavors here
      case _                   => Netgem
    }
  }

  private implicit def strings2frontend(p : (NetgemContainer, Seq[String])) : Frontend = {
    p._2 match {
      case Seq("shell", _*)     => ShellWizard(p._1)
      case Seq("config", c, _*) => ConfigFile(p._1,c)
      case Seq("config")        => ConfigFile(p._1)
      case Seq("gui", _*)       => Gui(p._1)
      // Add new frontends here
      case _                    => Frontend(p._1)
    }
  }

  private implicit def strings2mode(s : Seq[String]) : Logger.Mode = {
    s match {
      case Seq("silent", _*)  => Logger.Silent
      case Seq("shell", _*)   => Logger.Shell
      case Seq("file", f, _*) => Logger.File(f)
      case Seq("file")        => Logger.File("log")
      case Seq("both", f, _*) => Logger.Both(f)
      case Seq("both")        => Logger.Both("log")
      // Add new modes here
      case _                  => Logger.Both("log")
    }
  }

  /**
   * Executes the specified flavor of NETGEM, using the specified
   * frontend and logging mode.
   * @param args A sequence of parameters of the form `[name]=[values]`
   * where `values` is a comma-separated list of values.
   * See below for a list of parameter names and valid values. Leaving
   * out a parameter or giving invalid values will result in defaults
   * being used.
   *
   *  - `flavor`: Chooses which NETGEM algorithm to run. Possible values
   *    are `original`. Default is `original`.
   *  - `frontend`: Chooses which frontend to use. Possible values are
   *    `shell`, `config` and `gui`. Default is `shell`. If using `config`,
   *    you can supply the name of a config file as second value. Default
   *    is `config`.
   *  - `log`: Chooses which logging mode is to be used. Possible
   *    values are `silent`, `shell`, `file` and `both`. The latter two
   *    can take a target file name as second parameter, default is `log`.
   * @see [[netgem.NetgemContainer]]
   * @see [[netgem.frontend]]
   * @see [[org.utilx.Logger]]
   */
  def main(args : Array[String]) {
    val map = collection.mutable.HashMap[String, Seq[String]](
      "flavor" -> Seq(), "frontend" -> Seq(), "log" -> Seq())

    map ++= args.flatMap { param =>
      val parts = param.split("=")
      if ( parts.size > 1 ) {
        Some((parts(0).trim, parts(1).split(",").map(_.trim).toSeq))
      }
      else None
    }

    try {
      val ng : NetgemContainer = map("flavor")
      ng.run((ng, map("frontend")),map("log"))
    }
    catch {
      case e => println(e + ": " + e.getMessage)
    }
  }
}
