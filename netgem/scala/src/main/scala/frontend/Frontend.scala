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

/**
 * Enables the backend to retrieve parameters from some input source.
 * @author Raphael Reitzig
 *
 * @constructor Creates and initialises a new instance
 * @param ng The NETGEM implementation that will use this frontend
 */
abstract class Frontend(val ng : NetgemContainer) {
  /**
   * This method retrieves a value `v` for the parameter with the
   * specified name. Returns `Some(v)` if `acceptIf(v) == true`,
   * `None` else. By default, the checking function provided by
   * `ng.parameters` is used.
   * @param name Name of the parameter to be retrieved
   * @param acceptIf Function for checking value validty
   */
  def get(name : String)(acceptIf : String => Boolean = ng.parameters(name)) : Option[String]
}

/**
 * Companion of [[netgem.frontend.Frontend]]. It provides a factory method
 * for creating a default frontend.
 */
object Frontend {
  /**
   * Provides a default implementation of frontend that makes the least
   * assumptions about the runtime environment.
   * @param ng Netgem implementation that will use this frontend
   */
  def apply(ng : NetgemContainer) = ShellWizard(ng)
}
