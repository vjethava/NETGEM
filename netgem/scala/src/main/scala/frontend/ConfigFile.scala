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

import org.utilx._
import netgem._

// TODO test

/**
 * Reads pairs of parameter names and values, separated by '` = `', from the
 * specified file. One pair per line. Lines that do not conform to that
 * scheme are ignored. Consult the documentation of the NETGEM implementation
 * in use for necessary parameters.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance and reads all value pairs from the
 *              specified file.
 * @param ng The NETGEM implementation that will use this frontend
 * @param cfg Name of the configuration file to be used
 */
class ConfigFile(ng : NetgemContainer, cfg : String = "config") extends Frontend(ng) {
  // Read parameters from file and store
  ("Using config file " + cfg) logAs Note

  private val params : Map[String,String] =
    io.Source.fromFile(cfg).getLines.map { l =>
      l.split(" = ") match {
        case Array(name, value) => Some((name.trim, value.trim))
        case _                  => None
      }
    }.toSeq.flatten.toMap

  override def get(name : String)(acceptIf : String => Boolean = ng.parameters(name)) : Option[String] = {
    params.get(name) match {
      case Some(s) if acceptIf(s)  => Some(s)
      case Some(s) => {
        ("Invalid value '" + s + "' for parameter '" + name + "' in config file") logAs Warning
        None
      }
      case None => {
        ("No value for parameter '" + name + "' in config file") logAs Warning
        None
      }
    }
  }

  override def toString = "config file '" + cfg + "'"
}

/**
 * Companion of [[netgem.frontend.ConfigFile]]. It provides a factory method
 * for creating instances of that class.
 */
object ConfigFile {
  /**
   * Creates a config file frontend and reads all value pairs from the
   * specified file.
   * @param ng Netgem implementation that will use the frontend
   * @param cfg Name of the configuration file to be used
   */
  def apply(ng : NetgemContainer, cfg : String = "config") = new ConfigFile(ng, cfg)
}
