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

import java.io.File
import scala.io.{Source, BufferedSource}
import org.utilx._

// Be careful, spaghetti code included

/**
 * Reads CSV data from files and extracts the necessary features
 * from it to specify a proper graph. Up to five files can be read,
 * three of which are absolutely necessary. See documentation of
 * the `fooFile` methods for details.
 *
 * Note that this class does not do any kind of data sanitisation
 * other than checking that expression values are numbers. In
 * particular, the results provided might not describe a graph at
 * all, contain invalid key references, have missing expressions
 * and whatever you can think of.
 * It is for later stages (preprocessing or graph construction) to
 * check these things.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param valSep The string that separates values in the used source files
 */
class CsvReader(val valSep : String = ",", val comment : String = "#") extends DataReader {
  private var nodeList : Seq[String]                   = null
  private var edgeList : Seq[(String, Seq[String])]    = null
  private var exprMap  : Map[String, Seq[Seq[Double]]] = null
  private var catList  : Seq[String]                   = null
  private var catMap   : Map[String, Seq[String]]      = null

  override def nodes   = Option(nodeList)
  override def edges   = Option(edgeList)
  override def exprs   = Option(exprMap)
  override def cats    = Option(catList)
  override def catsMap = Option(catMap)

  private var nodeSrc : Option[File] = None
  private var edgeSrc : Option[File] = None
  private var exprSrc : Option[File] = None
  private var catsSrc : Option[File] = None
  private var cmapSrc : Option[File] = None
  private var strains : Option[Int]  = None

  def nodeFile : Option[String] = nodeSrc.map(_.toString)
  /**
   * Sets the source file for nodes to the specified file name.
   * This file is not mandatory. If it is missing, all node
   * identifiers found in the edge source file are used. A
   * separate file might be useful in order to filter out unwanted
   * nodes without touching your edge sources.
   * Note that such filtering is not done by this class.
   *
   * The specified file is assumed to contain one node per line.
   * Each line can contain arbitraily many values, separated by valSep.
   * The first one is used as node identifier, the rest is ignored.
   *
   * Note that this enables you to use expression or category mapping
   * source also as node source file.
   *
   * Note that all whitespaces preceding or trailing values are removed.
   * @param file Name of the file to read from.
   * @note `file` has to be a readable file (path)
   */
  def nodeFile_= (file : String) {
    val f = new File(file)
    if ( f.exists && !f.isDirectory && f.canRead) {
      this.nodeSrc = Option(f)
      ("Node source set to '" + file + "'").log
    }
    else {
      ("Tried setting node source to '" + file + "' but file does not exist or is not readable. Inferring from edges.") logAs Warning
    }
  }

  def edgeFile : Option[String] = edgeSrc.map(_.toString)
  /**
   * Sets the source file for edges to the specified file name.
   * This file is mandatory.
   *
   * The specified file is assumed to contain one edge per line.
   * Each line can contain arbitraily many values, separated by valSep.
   * The first one is used as edge identifier, the rest as identifiers of
   * the nodes that this edge connects.
   *
   * Note that all whitespaces preceding or trailing values are removed.
   * @param file Name of the file to read from.
   * @note `file` has to be a readable file (path)
   */
  def edgeFile_= (file : String) : Unit = {
    val f = new File(file)
    if ( f.exists && !f.isDirectory && f.canRead ) {
      this.edgeSrc = Option(f)
      ("Edge source set to '" + file + "'").log
    }
    else {
      ("Tried setting edge source to '" + file + "' but file does not exist or is not readable.") logAs Error
    }
  }

  def exprFile : Option[String] = exprSrc.map(_.toString)
  /**
   * Sets the source file for expressions to the specified file name.
   * This file is mandatory.
   *
   * The specified file is assumed to contain expressions for one node per line.
   * Each line can contain arbitraily many values, separated by valSep.
   * The first one is used as node identifier, the rest as rational
   * numbers (a time series of expression values).
   *
   * Note that all whitespaces preceding or trailing values are removed.
   * @param file Name of the file to read from.
   * @note `file` has to be a readable file (path)
   */
  def exprFile_= (file : String) {
    val f = new File(file)
    if ( f.exists && !f.isDirectory && f.canRead ) {
      this.exprSrc = Option(f)
      ("Expression source set to '" + file + "'").log
    }
    else {
      ("Tried setting expression source to '" + file + "' but file does not exist or is not readable.") logAs Error
    }
  }

  def catsFile : Option[String] = catsSrc.map(_.toString)
  /**
   * Sets the source file for categories to the specified file name.
   * This file is not mandatory. If it is missing, all category
   * identifiers found in the category mapping source file are used. A
   * separate file might be useful in order to filter out unwanted
   * categories without touching your category mapping source.
   * Note that such filtering is not done by this class.
   *
   * The specified file is assumed to contain one category per line.
   * Each line can contain arbitraily many values, separated by valSep.
   * The first one is used as category identifier, the rest is ignored.
   *
   * Note that all whitespaces preceding or trailing values are removed.
   * @param file Name of the file to read from.
   * @note `file` has to be a readable file (path)
   */
  def catsFile_= (file : String) {
    val f = new File(file)
    if ( f.exists && !f.isDirectory && f.canRead ) {
      this.catsSrc = Option(f)
      ("Category source set to '" + file + "'").log
    }
    else {
      ("Tried setting category source to '" + file + "' but file does not exist or is not readable. Inferring from category map.") logAs Warning
    }
  }

  def cmapFile : Option[String] = cmapSrc.map(_.toString)
  /**
   * Sets the source file for category mappings to the specified file name.
   * This file is mandatory.
   *
   * The specified file is assumed to contain categories for one node per line.
   * Each line can contain arbitraily many values, separated by valSep.
   * The first one is used as node identifier, the rest as category identifiers.
   *
   * Note that all whitespaces preceding or trailing values are removed.
   * @param file Name of the file to read from.
   * @note `file` has to be a readable file (path)
   */
  def cmapFile_= (file : String) {
    val f = new File(file)
    if ( f.exists && !f.isDirectory && f.canRead ) {
      this.cmapSrc = Option(f)
      ("Category map source set to '" + file + "'").log
    }
    else {
      ("Tried setting category map source to '" + file + "' but file does not exist or is not readable.") logAs Error
    }
  }

  def strainsLength = strains
  /**
   * Sets the number of timesteps for each strain this reader will expect
   * to read. If zero, all expressions will be used for one strain.
   */
  def strainsLength_= (nr : Int) {
    if ( nr >= 0 ) {
      strains = Some(nr)
    }
  }

  override def read {
    if ( edgeSrc == None || exprSrc == None || cmapSrc == None ) {
      throw new javax.naming.InsufficientResourcesException("Critical data sources not available. Cannot recover.")
    }

    val nodeListBuilder = Seq.newBuilder[String]
    val edgeListBuilder = Seq.newBuilder[(String, Seq[String])]
    val exprMapBuilder  = Map.newBuilder[String, Seq[Seq[Double]]]
    val catListBuilder  = Seq.newBuilder[String]
    val catMapBuilder   = Map.newBuilder[String, Seq[String]]
    var reader = null : BufferedSource

    // Read nodes if source available
    if ( nodeSrc != None ) {
      reader = Source.fromFile(nodeSrc.get)
      reader.getLines foreach { line =>
        if ( !(line startsWith comment) ) {
          // Take first value as name, ignore rest
          val parts = line.split(valSep).map(_.trim).filter(_.size > 0)
          if ( parts.size > 0 ) {
            nodeListBuilder += parts.head
          }
        }
      }
      reader.close
    }
    else {
      "No node source specified. Will infer from edges.".log
    }

    // Read edges
    reader = Source.fromFile(edgeSrc.get)
    reader.getLines foreach { line =>
      if ( !(line startsWith comment) ) {
        // Take first value as name, rest as nodes
        val parts = line.split(valSep).map(_.trim).filter(_.size > 0)
        if ( parts.size > 1 ) {
          edgeListBuilder += ((parts.head, parts.tail))

          if ( nodeSrc == None ) {
            nodeListBuilder ++= parts.tail
          }
        }
      }
    }
    reader.close

    nodeList = nodeListBuilder.result
    // Keep duplicates if separate file used to produce warnings for user later
    if ( nodeSrc == None ) {
      nodeList = nodeList.toSet.toSeq
    }

    edgeList = edgeListBuilder.result

    // Read expressions
    if ( strains == None ) {
      "Strain length not set. Treat data as one strain." logAs Warning
      strains = Some(0)
    }

    def split(seq : Seq[Double]) : Seq[Seq[Double]] = {
      val b = Seq.newBuilder[Seq[Double]]

      if ( strains.get == 0 ) {
        b += seq
      }
      else {
        var s = seq

        while ( s.size > 0 ) {
          b += s.take(strains.get)
          s = s.drop(strains.get)
        }
      }

      b.result
    }

    reader = Source.fromFile(exprSrc.get)
    reader.getLines foreach { line =>
      if ( !(line startsWith comment) ) {
        // Take first value as node name, rest as expr values
        val parts = line.split(valSep).map(_.trim).filter(_.size > 0)
        if ( parts.size > 1 ) {
          try {
            exprMapBuilder += ((parts.head, split(parts.tail.map(_.toDouble))))
          }
          catch {
            case _:java.lang.NumberFormatException =>
              throw new java.lang.NumberFormatException("Expression file contains non-numerical expressions.")
          }
        }
      }
    }
    reader.close
    exprMap = exprMapBuilder.result

    // Read categories if source available
    if ( catsSrc != None ) {
      reader = Source.fromFile(catsSrc.get)
      reader.getLines foreach { line =>
        if ( !(line startsWith comment) ) {
          // Take first value as name, ignore rest
          val parts = line.split(valSep).map(_.trim).filter(_.size > 0)
          if ( parts.size > 0 ) {
            catListBuilder += parts.head
          }
        }
      }
      reader.close
    }
    else {
      "No category source specified. Will infer from mappings.".log
    }

    // Read category map
    reader = Source.fromFile(cmapSrc.get)
    reader.getLines foreach { line =>
      if ( !(line startsWith comment) ) {
        // Take first value as node name, rest as expr values
        val parts = line.split(valSep).map(_.trim).filter(_.size > 0)
        if ( parts.size > 1 ) {
          catMapBuilder += ((parts.head, parts.tail))

          if ( catsSrc == None ) {
            catListBuilder ++= parts.tail
          }
        }
      }
    }
    reader.close
    catMap = catMapBuilder.result

    catList = catListBuilder.result
    // Keep duplicates if separate file used to produce warnings for user later
    if ( catsSrc == None ) {
      catList = catList.toSet.toSeq
    }

    ("Found " + nodes.get.size + " nodes.").log
    ("Found " + edges.get.size + " edges.").log
    ("Found " + cats.get.size  + " categories.").log
  }
}
