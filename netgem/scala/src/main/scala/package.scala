/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.
 *
 * netgem is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netgem. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This package contains implementations of the NETGEM inference family.
 * Those are provided as implementations of trait [[netgem.NetgemContainer]].
 * They usually consist of at least one of the following:
 *
 *  - A data reader from [[netgem.input]]
 *  - A datastructure from [[netgem.core]]
 *  - An inference algorithm from [[netgem.em]]
 *  - A result writer from [[netgem.output]]
 *
 * They are accessed by executing [[netgem.Main]] with one of several frontends
 * from [[netgem.frontend]].
 *
 * Many implementations depend on packages [[org.utilx]] (e.g. logging) and
 *  [[org.mathx]] (e.g. random number generation).
 */
package object netgem {
}
