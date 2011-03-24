package netgem.input

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
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

/**
 * Reads data from some external source and extracts the necessary features
 * from it to specify a proper graph.
 * @author Raphael Reitzig
 */
trait DataReader {
  /**
   * Returns an option with the read node identifiers, or
   * None if none have been read (so far).
   */
  def nodes   : Option[Seq[String]]

  /**
   * Returns an option with the read edge identifiers, or
   * None if none have been read (so far).
   */
  def edges   : Option[Seq[(String, Seq[String])]]

  /**
   * Returns an option with the read node to expressions mapping, or
   * None if none has been read (so far). There is one expression
   * sequence per strain.
   */
  def exprs   : Option[Map[String, Seq[Seq[Double]]]]

  /**
   * Returns an option with the read category identifiers, or
   * None if none have been read (so far).
   */
  def cats    : Option[Seq[String]]

  /**
   * Returns an option with the read node to categories mapping, or
   * None if none has been read (so far).
   */
  def catsMap : Option[Map[String, Seq[String]]]

  /**
   * Reads data and stores them in this reader for later use.
   */
  def read    : Unit
}
