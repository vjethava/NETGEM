package netgem.em

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.em.
 *
 * netgem.em is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.em is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem.em. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * An EM (expectation maximisation) algorithm finds a model
 * for given data that maximises likelihood (locally).
 * All initialisation should happen in the constructor.
 */
trait EM {
  /**
   * The seed used for creating random numbers in this EM.
   * Running the same kind of EM on the same parameters
   * with the same seed yields the same result.
   */
  def seed : Long
  /**
   * Run the EM.
   */
  def infer : Unit
}
