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

/**
 * A cartesian product of two iterables contains all combinations of
 * left and right elements. For example,
 * {{{
 *    (1 to 3) * (1 to 2) == Seq( (1,1), (1,2),
 *                                (2,1), (2,2),
 *                                (3,1), (3,2) )
 * }}}
 * The most convenient way to create cartesians is by using the `*` operator,
 * available on sequences and sets via implicit conversions in [[org.utilx]].
 * You can also use factory methods on companion [[org.utilx.Cartesian]].
 *
 * Note that `Cartesian` merely wraps the source collections. That is, if those
 * are mutable and changed after construction of the cartesian, the
 * representation of the cartesian will change accordingly. However, if both
 * members are immutable, the cartesian is immutable, too.
 * @tparam T Type of the left elements
 * @tparam U Type of the right elements
 * @author Raphael Reitzig
 */
sealed trait Cartesian[+T,+U] extends Iterable[(T,U)] {
  /**
   * The left citizen of this cartesian. Left components of
   * this cartesian's elements are taken from it.
   */
  val left : Iterable[T]
  /**
   * The right citizen of this cartesian. Right components of
   * this cartesian's elements are taken from it.
   */
  val right : Iterable[U]

  override def size = left.size * right.size

  override def iterator = new Iterator[(T,U)] {
    private val liter = left.iterator
    /* Since we traverse the right member multiple times, we
     * keep a copy of its iterator. We do not request new ones
     * from the collection itself since it might have changed. */
    private var (riter, tmp) = right.iterator.duplicate
    private var curL : Option[T] = None

    override def hasNext = (curL != None && riter.hasNext) || (liter.hasNext)

    override def next = {
      if ( curL == None ) { curL = Some(liter.next) }

      if ( !riter.hasNext ) {
        /* This line fails if we are finished, i.e. both
         * iterators are empty */
        curL = Some(liter.next)

        /* If an iterator is duplicated, it can not be used because
         * both spawns are forwarded if the original is forwarded. */
        val (t1, t2) = tmp.duplicate
        riter = t1
        tmp = t2
      }

      (curL.get, riter.next)
    }
  }
}

/**
 * Companion of trait [[org.utilx.Cartesian]]. Provides construction and
 * extraction methods for cartesian sets and sequences as well as a general
 * extractor.
 * @author Raphael Reitzig
 */
object Cartesian {
  /**
   * Creates a new cartesian sequence from the specified sequences.
   * @param l The sequence left components will be taken from
   * @param r The sequence right components will be taken from
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   * @return A cartesian sequence constructed of the specified parameters
   */
  def apply[T,U](l : Seq[T], r : Seq[U]) : Seq[(T,U)] = new CartesianSeq(l,r)
  /**
   * Creates a new cartesian set from the specified sets.
   * @param l The set left components will be taken from
   * @param r The set right components will be taken from
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   * @return A cartesian set constructed of the specified parameters
   */
  def apply[T,U](l : Set[T], r : Set[U]) : Set[(T,U)] = new CartesianSet(l,r)

  /**
   * Extracts the underlying collections from the specified cartesian.
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   */
  def unapply[T,U](c : Cartesian[T,U]) : Option[(Iterable[T], Iterable[U])] = Some((c.left,c.right))
}

/**
 * A cartesian sequence is the cartesian product of two sequences. If the
 * specified sequences are mutable and changed after construction
 * of their cartesian sequence, the representation of the cartesian
 * will also change.
 * You can create a cartesian sequence in the following three equivalent ways:
 * {{{
 *  val (l, r) = (Seq(...), Seq(...))
 *
 *  val a = Cartesian(l, r)
 *  val b = CartesianSeq(l, r)
 *
 *  import org.utilx.seq2cartesian
 *  val c = l * r
 *
 *  assert(a == b && b == c)
 * }}}
 * Note that you can chain the creation of cartesians, i.e.
 * `s1 * ... * sN` will create a cartesian sequence
 * of type `Seq[(((...(T1, T2)...), TN-1),TN)]`.
 * @tparam T Type of the left elements
 * @tparam U Type of the right elements
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param left Sequence left elements are taken from
 * @param right Sequence right elements are taken from
 */
class CartesianSeq[+T,+U](override val left : Seq[T], override val right : Seq[U])
  extends Seq[(T,U)] with Cartesian[T,U]
{
  /* We iterate the right member first, therefore we
   * can compute target indices like this.
   */
  override def apply(i : Int) = {
    val r = right.size
    (left(i / r), right(i % r))
  }

  override def length = size
}

/**
 * Companion of class [[org.utilx.CartesianSeq]]. Provides construction and
 * extraction methods for cartesian sequences.
 * @author Raphael Reitzig
 */
object CartesianSeq {
  /**
   * Creates a new cartesian sequence from the specified sequences.
   * @param l The sequence left components will be taken from
   * @param r The sequence right components will be taken from
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   * @return A cartesian sequence constructed of the specified parameters
   */
  def apply[T,U](l : Seq[T], r : Seq[U]) : Seq[(T,U)] = new CartesianSeq(l,r)

  /**
   * Extracts the underlying sequences from the specified cartesian sequence.
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   */
  def unapply[T,U](c : CartesianSeq[T,U]) : Option[(Seq[T], Seq[U])] = Some((c.left,c.right))
}

/**
 * A cartesian set is the cartesian product of two sets. If the
 * specified sets are mutable and changed after construction
 * of their cartesian set, the representation of the cartesian
 * will also change.
 * You can create a cartesian set in the following three equivalent ways:
 * {{{
 *  val (l, r) = (Set(...), Set(...))
 *
 *  val a = Cartesian(l, r)
 *  val b = CartesianSet(l, r)
 *
 *  import org.utilx.set2cartesian
 *  val c = l * r
 *
 *  assert(a == b && b == c)
 * }}}
 * Note that you can chain the creation of cartesians, i.e.
 * `s1 * ... * sN` will create a cartesian set
 * of type `Set[(((...(T1, T2)...), TN-1),TN)]`.
 * @tparam T Type of the left elements
 * @tparam U Type of the right elements
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance
 * @param left Set left elements are taken from
 * @param right Set right elements are taken from
 */
class CartesianSet[T,U](override val left : Set[T], override val right : Set[U])
  extends Cartesian[T,U] with Set[(T,U)]
{
  override def contains(e : (T, U)) = left.contains(e._1) && right.contains(e._2)

  /**
   * Creates a new set with an additional element, unless the
   * element is already present. The result set will be of the
   * same type as the left underlying set.
   * @param elem The element to be removed
   * @return A new set that contains all elements of this set but
   *         that does not contain <code>elem</code>.
   */
  override def +(e : (T,U)) : Set[(T,U)] = {
    val b = left.genericBuilder[(T,U)]
    b ++= iterator
    if ( !contains(e) ) { b += e }
    b.result
  }

  /**
   * Creates a new set with a given element removed from this set.
   * The result set will be of the same type as the left underlying set.
   * @param elem The element to be added
   * @return A new set that contains all elements of this set and
   *         that also contains <code>elem</code>.
   */
  override def -(e : (T,U)) : Set[(T,U)] = {
    val b = left.genericBuilder[(T,U)]
    b ++= iterator.filter(_ != e)
    b.result
  }
}

/**
 * Companion of class [[org.utilx.CartesianSet]]. Provides construction and
 * extraction methods for cartesian sets.
 * @author Raphael Reitzig
 */
object CartesianSet {
  /**
   * Creates a new cartesian set from the specified sets.
   * @param l The set left components will be taken from
   * @param r The set right components will be taken from
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   * @return A cartesian set constructed of the specified parameters
   */
  def apply[T,U](l : Set[T], r : Set[U]) : Set[(T,U)] = new CartesianSet(l,r)

  /**
   * Extracts the underlying sets from the specified cartesian set.
   * @tparam T Type of the left elements
   * @tparam U Type of the right elements
   */
  def unapply[T,U](c : CartesianSet[T,U]) : Option[(Set[T], Set[U])] = Some((c.left,c.right))
}

/**
 * Provides an operator on the wrapped sequence for constructing the cartesian
 * product of the wrapped seequence and another. Created by implicit conversion
 *  [[org.utilx#seq2cartesian]].
 * @tparam T Element type of the wrapped sequence
 * @author Raphael Reitzig
 *
 * @constructor Creates a new intermediate wrapper
 * @param t The wrapped sequence
 */
protected class CartesianableSeq[T](val t : Seq[T]) {
  /**
   * Constructs a sequence containing the cartesian product of this sequence
   * and the specified one.
   * @param that Some sequence
   * @tparam Element type of the passed sequence
   * @return Cartesian product if this and the specified sequence.
   */
  def *[U](that : Seq[U]) : Seq[(T, U)] = new CartesianSeq[T,U](t, that)
}

/**
 * Provides an operator on the wrapped set for constructing the cartesian product
 * of the wrapped set and another. Created by implicit conversion
 *  [[org.utilx#set2cartesian]].
 * @tparam T Element type of the wrapped set
 * @author Raphael Reitzig
 *
 * @constructor Creates a new intermediate wrapper
 * @param t The wrapped set
 */
protected class CartesianableSet[T](val t : Set[T]) {
  /**
   * Constructs a set containing the cartesian product of this set
   * and the specified one.
   * @param that Some set
   * @tparam Element type of the passed set
   * @return Cartesian product if this and the specified set.
   */
  def *[U](that : Set[U]) : Set[(T, U)] = new CartesianSet[T,U](t, that)
}
