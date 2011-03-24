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

// TODO implement, document

/**
 * @author Raphael Reitzig
 */
class Gui(netgem : NetgemContainer) extends Frontend(netgem) {
  // Offer possibility to check while entering

    // Create GUI
    // Show GUI
    // Block until commit

  override def get(parameterName : String)(acceptIf : String => Boolean = (_ => true)) : Option[String] = {
    // TODO get from GUI and reshow with notice if not acceptable
    return None
  }

  override def toString = "GUI"
}

object Gui {
  def apply(netgem : NetgemContainer) = new Gui(netgem)
}
