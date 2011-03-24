package org.utilx

/* Copyright 2011, Raphael Reitzig
*
* This file is part of org.utilx
*
* org.utilx is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* org.utilx is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with org.utilx. If not, see <http://www.gnu.org/licenses/>.
*/

import util.logging.Logged
import java.io.FileWriter
import java.lang.System
import java.util.Date
import java.text.{DateFormat, SimpleDateFormat}

// TODO logging levels resp. select kinds to be logged

/**
 * This object can be used for rudimentary logging purposes.
 * It provides several logging modes and message kinds.
 *
 * Note that importing `org.utilx._` will import aliases for message kinds
 * and an implicit conversion that provide quite convenient logging, e.g.
 * {{{
 *  try {
 *  [...]
 *    "Hello World" logAs Note
 *  }
 *  catch {
 *  case e => e logAs Error
 * }
 * }}}
 * @author Raphael Reitzig
 * @todo provide selective logging
 */
object Logger extends Logged {
  /**
   * String that is prefixed to shell outputs
   */
  var prefix = ""

  private var imode : Mode = Shell
  /**
   * The current logging mode.
   */
  def mode : Mode = this.imode

  /**
   * Sets a new mode. Note that when setting a mode
   * that writes to files, the target file will be overwritten.
   * @param newMode The mode that will be used after this method terminates
   */
  def mode_= (newMode : Mode ) : Unit = {
    this.mode.detach
    this.imode = newMode
  }

  /**
   * Logs the passed message using the current mode
   * @param msg Message to log
   */
  override def log(msg : String) : Unit = {
    this.mode.log(msg)
  }

  /**
   * Provides several message kinds that will be presented differently.
   */
  object Kind extends Enumeration {
    type Kind = Value
    /**
     * Use this Kind if you want to log a simple
     * note without special significance.
     */
    val Note =    Value("")
    /**
     * Use this Kind if you want to log a
     * warning, that is something that is not
     * critical but you want to alert the user of.
     */
    val Warning = Value("Warning: ")

    /**
     * Use this Kind if you want to log an
     * error, that is a critical issue.
     */
    val Error =   Value("Error: ")
  }
  import Kind._

  /**
   * Logs the specified message of the specified kind using the
   * current logging mode.
   * @param msg Message to log
   * @param t Message kind
   */
  def apply(msg : String, t : Kind = Note) : Unit = {
    this.log(t + msg)
  }

  /**
   * Logs the specified message as notification.
   * @param msg Message to log
   */
  def note(msg : String) : Unit = {
    this(msg, Note)
  }

  /**
   * Logs the specified message as warning.
   * @param msg Message to log
   */
  def warn(msg : String) : Unit = {
    this(msg, Warning)
  }

  /**
   * Logs the specified message as error.
   * @param msg Message to log
   */
  def error(msg : String) : Unit = {
    this(msg, Error)
  }

  /**
   * Use this method to separate messages from before
   * the call from such that are logged afterwards.
   */
  def separate : Unit = this.mode.separate

  /**
   * Finish logging. Releases all used resources.
   */
  def finish : Unit = this.mode = Silent

  /**
   * This abstract class specifies different logging modes.
   * Implement your own if you need something else.
   */
  abstract class Mode {
    /**
     * Logs the passed message
     * @param msg Message to log
     */
    def log(msg : String)

    /**
   * Use this method to separate messages from before
   * the call from such that are logged afterwards.
   */
    def separate() : Unit = {}

    /**
     * Cleans up after this logging mode. Call when
     * you stop using it.
     */
    def detach() : Unit = {}
  }

  /**
   * This Mode logs nothing.
   */
  case object Silent extends Mode {
    override def log(msg : String) {}

    override def toString = "nothing"
  }

  /**
   * This Mode logs only to shell.
   */
  case object Shell extends Mode {
    override def log(msg : String) : Unit = {
      println(prefix + msg)
    }

    override def separate = { println() }

    override def toString = "to shell"
  }

  /**
   * This Mode logs only to the specified file. Timestamps will be included.
   * You might have to detach it in order to flush the target file.
   * @author Raphael Reitzig
   *
   * @constructor Creates a new instance
   * @param filename File to log to
   * @note Target file has to be writable
   */
  case class File(filename : String) extends Mode {
    {
      val f = new java.io.File(filename)
      require(f.canWrite || f.createNewFile, "Target file not writable")
    }
    private val file = new FileWriter(filename)
    private val dtfmt = new SimpleDateFormat("HH:mm:ss")

    private def timestamp : String = {
      return dtfmt.format(new Date)
    }

    private val sep = System.getProperty("line.separator")

    override def log(msg : String) : Unit = {
      file.write(timestamp + " " + msg + sep)
      // file.flush // uncomment for debugging
    }

    override def separate = {
      file.write(sep)
      file.flush
    }

    /**
     * Closes the current file.
     */
    override def detach : Unit = {
      file.close
    }

    override def toString = "to file " + filename
  }

  /**
   * This modes logs both to shell and to file. You might have to detach it
   * in order to flush the target file.
   * @author Raphael Reitzig
   *
   * @constructor Creates a new instance
   * @param file File to log to
   * @note Target file has to be writable
   */
  case class Both(file : String) extends Mode {
    private val fileLog = File(file)

    override def log(msg : String) : Unit = {
      Shell.log(msg)
      fileLog.log(msg)
    }

    override def separate = {
      Shell.separate
      fileLog.separate
    }

    override def detach : Unit = {
      Shell.detach
      fileLog.detach
    }

    override def toString = Shell.toString + " and " + fileLog.toString
  }
}

/**
 * Intermediate type any other type can implicitly be converted to by
 *  [[org.utilx#any2Loggable]]. Provides methods for logging the string
 * representation of the wrapped object.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param o The object whose representation will be logged
 */
protected class Loggable(val o : Any) {
  import Logger.Kind._

  /**
   * Logs the string representation of the wrapped
   * object as message of the specified kind.
   */
  def logAs(kind : Kind = Note) {
    Logger(o.toString, kind)
  }

  /**
   * Logs the string representation of the wrapped
   * object as message of kind `Note`.
   */
  def log = logAs()
}
