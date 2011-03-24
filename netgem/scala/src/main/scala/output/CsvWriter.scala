package netgem.output

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.output.
 *
 * netgem.output is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netgem.output is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with netgem.output. If not, see <http://www.gnu.org/licenses/>.
 */

import netgem.core.{WeightedHyperGraph, Edge, Key}
import org.utilx._
import java.io.{File,BufferedWriter,FileWriter}

// Be careful, spaghetti code included

/**
 * Provides methods to write NETGEM's results into CSV files.
 * Constructor parameter `valSep` sets the separating character;
 * choose it such that there are no conflicts with the keys used
 * in your graphs.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param valSep The string that will separate values in the result files
 */
class CsvWriter(val valSep : String = ",") extends ResultWriter[String, Seq[Double]] {
  private var nameFile  : Option[File] = None
  private var scoreFile : Option[File] = None
  private var statsFile : Option[File] = None
  private var nodesFile : Option[File] = None
  private var edgesFile : Option[File] = None
  private var exprsFile : Option[File] = None

  /**
   * The file edges with their change score, sorted by name, will
   * be written to.
   */
  def byNameFile : Option[String] = nameFile.map(_.toString)
  /**
   * Sets the file edges with their change score, sorted by name, will
   * be written to. Names of directories are ignored.
   * @param file Name of the file to write to.
   * @note `file` has to be a writable file (path)
   */
  def byNameFile_= (file : String) {
    val f = new File(file)
    if ( (!f.exists && f.createNewFile) || (!f.isDirectory && f.canWrite) ) {
      this.nameFile = Option(f)
      ("Edge-by-name target file set to '" + file + "'").log
      if ( f.exists ) {
        ("File '" + file + "' exists, contents will be overwritten") logAs Warning
      }
    }
    else {
      ("File '" + file + "' is a directory or not writable.") logAs Warning
    }
  }

  /**
   * The file edges with their change score, sorted by score, will
   * be written to.
   */
  def byScoreFile : Option[String] = scoreFile.map(_.toString)
  /**
   * Sets the file edges with their change score, sorted by score, will
   * be written to. Names of directories are ignored.
   * @param file Name of the file to write to.
   * @note `file` has to be a writable file (path)
   */
  def byScoreFile_= (file : String) {
    val f = new File(file)
    if ( (!f.exists && f.createNewFile) || (!f.isDirectory && f.canWrite) ) {
      this.scoreFile = Option(f)
      ("Edge-by-score target file set to '" + file + "'").log
      if ( f.exists ) {
        ("File '" + file + "' exists, contents will be overwritten") logAs Warning
      }
    }
    else {
      ("File '" + file + "' is a directory or not writable.") logAs Warning
    }
  }

  /**
   * The file statistics will be written to.
   */
  def statisticsFile : Option[String] = statsFile.map(_.toString)
  /**
   * Sets the file statistics will be written to. Names of directories are
   * ignored.
   * @param file Name of the file to write to.
   * @note `file` has to be a writable file (path)
   */
  def statisticsFile_= (file : String) {
    val f = new File(file)
    if ( (!f.exists && f.createNewFile) || (!f.isDirectory && f.canWrite) ) {
      this.statsFile = Option(f)
      ("Edge-by-score target file set to '" + file + "'").log
      if ( f.exists ) {
        ("File '" + file + "' exists, contents will be overwritten") logAs Warning
      }
    }
    else {
      ("File '" + file + "' is a directory or not writable.") logAs Warning
    }
  }

  /**
   * The file nodes with their categories will be written to.
   */
  def graphNodesFile : Option[String] = nodesFile.map(_.toString)
  /**
   * Sets the file nodes will be written to. Names of directories are ignored.
   * @param file Name of the file to write to.
   * @note `file` has to be a writable file (path)
   */
  def graphNodesFile_= (file : String) {
    val f = new File(file)
    if ( (!f.exists && f.createNewFile) || (!f.isDirectory && f.canWrite) ) {
      this.nodesFile = Option(f)
      ("Graph nodes target file set to '" + file + "'").log
      if ( f.exists ) {
        ("File '" + file + "' exists, contents will be overwritten") logAs Warning
      }
    }
    else {
      ("File '" + file + "' is a directory or not writable.") logAs Warning
    }
  }

  /**
   * The file edges with their nodes will be written to.
   */
  def graphEdgesFile : Option[String] = edgesFile.map(_.toString)
  /**
   * Sets the file edges with their nodes will be written to. Names of
   * directories are ignored.
   * @param file Name of the file to write to.
   * @note `file` has to be a writable file (path)
   */
  def graphEdgesFile_= (file : String) {
    val f = new File(file)
    if ( (!f.exists && f.createNewFile) || (!f.isDirectory && f.canWrite) ) {
      this.edgesFile = Option(f)
      ("Graph edges target file set to '" + file + "'").log
      if ( f.exists ) {
        ("File '" + file + "' exists, contents will be overwritten") logAs Warning
      }
    }
    else {
      ("File '" + file + "' is a directory or not writable.") logAs Warning
    }
  }

  /**
   * The file expressions as used will be written to.
   */
  def expressionsFile : Option[String] = exprsFile.map(_.toString)
  /**
   * Sets the file expressions as used will be written to. Names of
   * directories are ignored.
   * @param file Name of the file to write to.
   * @note `file` has to be a writable file (path)
   */
  def expressionsFile_= (file : String) {
    val f = new File(file)
    if ( (!f.exists && f.createNewFile) || (!f.isDirectory && f.canWrite) ) {
      this.exprsFile = Option(f)
      ("Expressions target file set to '" + file + "'").log
      if ( f.exists ) {
        ("File '" + file + "' exists, contents will be overwritten") logAs Warning
      }
    }
    else {
      ("File '" + file + "' is a directory or not writable.") logAs Warning
    }
  }

  override def writeStatistics(g : WeightedHyperGraph[String, Seq[Double]]) {
    import DiscreteStatistics._

    if ( g.edges.size > 0 && g.edges.head.weight.size >= 2 ) {
      if ( nameFile == None ) {
        "No target file for edges by name" logAs Warning
      }
      else {
        val writer = new BufferedWriter(new FileWriter(nameFile.get))

        g.edges.foreach { e =>
          writer.write(e.key + valSep + changeScore(e.weight).getOrElse("nA") + valSep + e.weight.mkString(valSep))
          writer.newLine
        }

        writer.close
      }

      if ( scoreFile == None ) {
        "No target file for edges by score" logAs Warning
      }
      else {
        val writer = new BufferedWriter(new FileWriter(scoreFile.get))

        val comp = { (e1 : Edge[String,Seq[Double]], e2 : Edge[String,Seq[Double]]) =>
          changeScore(e1.weight).getOrElse(0.0) >= changeScore(e2.weight).getOrElse(0.0)
        }
        g.edges.view.sortWith(comp).foreach { e =>
          writer.write(e.key + valSep + changeScore(e.weight).getOrElse("nA") + valSep + e.weight.mkString(valSep))
          writer.newLine
        }

        writer.close
      }

      if ( statsFile == None ) {
        "No target file for statistics"  logAs Warning
      }
      else {
        val writer = new BufferedWriter(new FileWriter(statsFile.get))

        val stat = compute(g)_
        codes.foreach { c =>
          writer.write(pretty(c) + valSep + stat(c).getOrElse("nA"))
          writer.newLine
        }

        writer.close
      }
    }
    else {
      "Empty graph or less than two time steps; no statistics generated." logAs Warning
    }
  }

  override def writeGraph(g : WeightedHyperGraph[String,Seq[Double]]) {
    if ( nodesFile == None ) {
      "No target file for graph nodes" logAs Warning
    }
    else {
      if ( g.nodes.isEmpty ) {
        "No nodes to export" logAs Warning
      }
      else {
        val writer = new BufferedWriter(new FileWriter(nodesFile.get))
        g.nodes.foreach { n =>
          writer.write(n.key + valSep + n.cats.mkString(valSep))
          writer.newLine
        }
        writer.close
      }
    }

    if ( edgesFile == None ) {
      "No target file for edges nodes" logAs Warning
    }
    else {
      if ( g.edges.isEmpty ) {
        "No edges to export" logAs Warning
      }
      else {
        val writer = new BufferedWriter(new FileWriter(edgesFile.get))
        g.edges.foreach { e =>
          writer.write(e.key + valSep + e.nodes.view.map(_.key).mkString(valSep))
          writer.newLine
        }
        writer.close
      }
    }

    if ( exprsFile == None ) {
      "No target file for expressions" logAs Warning
    }
    else {
      if ( g.nodes.isEmpty ) {
        "No expressions to export" logAs Warning
      }
      else {
        val writer = new BufferedWriter(new FileWriter(exprsFile.get))
        writer.write("# " + g.nodes(0).expr.size + " strains, " + g.nodes(0).expr(0).size + " timesteps each")
        writer.newLine
        g.nodes.foreach { n =>
          writer.write(n.key + valSep + n.expr.flatten.mkString(valSep))
          writer.newLine
        }
        writer.close
      }
    }
  }
}
