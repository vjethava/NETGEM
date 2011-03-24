package netgem.frontend

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.frontend.
 *
 * netgem.frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem.frontend. If not, see <http://www.gnu.org/licenses/>.
 */

import netgem._
import org.utilx._

/**
 * This frontend pulls values on demand from the command line. It never
 * yields `None` because of invalid inputs; users are bugged until they
 * enter valid values. They can, however, force `None` by entering '`_`'.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param ng The NETGEM implementation that will use this frontend
 */
class ShellWizard(ng : NetgemContainer) extends Frontend(ng) {
  ("Running " + ng.name + ". Standby for entering parameters...") logAs Note

  override def get(name : String)(acceptIf : String => Boolean = ng.parameters(name)) : Option[String] = {
    var res : Option[String] = None
    do {
      readLine(Logger.prefix + " Enter " + name + ": ") match {
        case "_" => { res = None }
        case s   => { res = Some(s) }
      }
    } while ( res != None && !acceptIf(res.get) )
    return res
  }

  override def toString = "shell wizard"
}

/**
 * Companion of [[netgem.frontend.ShellWizard]]. It provides a factory method
 * for creating instances of that class.
 */
object ShellWizard {
  /**
   * Creates a shell frontend
   * @param ng Netgem implementation that will use the frontend
   */
  def apply(ng : NetgemContainer) = new ShellWizard(ng)
}
