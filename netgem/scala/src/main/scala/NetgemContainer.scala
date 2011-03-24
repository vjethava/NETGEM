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

import frontend._
import org.utilx._

/**
 * This trait abstracts the outermost box aroung a certain
 * flavor of NETGEM, i.e. you can give an implementation to
 * an end user. A container wraps a specific set of the following
 * components:
 *  - An [[netgem.input.DataReader]]
 *  - A [[netgem.frontend.Frontend]]
 *  - An [[netgem.em.EM]], probably with a [[netgem.em.Perturbing]]
 *  - A [[netgem.output.ResultWriter]]
 * @author Raphael Reitzig
 */
trait NetgemContainer {
  /**
   * This particular NETGEM flavor's name
   */
  def name : String

  /**
   * Names of parameters a frontend must deliver with functions
   * to check wether a particular value is legal. That is frontends
   * may only deliver values `v` for parameter `p` that satisfy
   * `parameters(p)(v) == true`.
   */
  def parameters : Map[String, String => Boolean]

  /**
   * Runs this netgem, using the specified frontend and logging mode.
   * @param front The frontend to be used during this run
   * @param log The logging mode to be used during this run
   */
  def run(front : Frontend, log : Logger.Mode) : Unit
}
