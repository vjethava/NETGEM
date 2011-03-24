package netgem.core

/* Copyright 2011, Raphael Reitzig
 * Chalmers University of Technology
 * Department of Computer Science and Engineering
 *
 * This file is part of netgem.core.
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

import collection.mutable.{ArrayBuffer, HashMap, HashSet, Stack, Queue}
import org.utilx._

/**
 * This class models hypergraphs with weighted edges. Nodes are associated
 * with some expression series' and at least one category.
 *
 * The default constructor assumes that the passed values together form
 * a valid graph. See factory method `netgem.core.WeightedHyperGraph#apply`
 * for details.
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance. Uses the specified values as is.
 * @param nodes Sequence of nodes
 * @param edges Sequence of edges
 * @param categories Sequence of allowed category.
 */
class WeightedHyperGraph[K <% Key[K],W](val nodes : Seq[Node[K,W]], val edges : Seq[Edge[K,W]], val categories : Seq[K]) {
  /**
   * Yields an iterator over this graph, that is its nodes. Iteration
   * is done depth first in preorder. Nodes come with their depth in
   * the particular visit tree, with the root having depth 0.
   * The iterator is not thread-safe.
   * @param root The node the iterator starts at.
   * @param stopIf If `stopIf(n,d) == true`, edges originating in `n` at depth
   *               `d` are not traversed, i.e. recursion stops.
   * @return Iterator over the visited nodes in appropriate order.
   */
  def depthFirstWithDepth(root : Node[K,W], stopIf : (Node[K,W], Int) => Boolean = (_,_) => false) : Iterator[(Node[K,W],Int)] = new Iterator[(Node[K,W],Int)] {
    private val visited = new HashSet[Node[K,W]]
    private val stack = new Stack[(Node[K,W],Int)]

    if ( nodes contains root ) { stack push ((root, 0)) }

    /* There might be already visited nodes on stack, so check that there
     * are really nodes left to visit. Cannot check number of visited nodes
     * against number of nodes (faster) since not all nodes might get visited. */
    override def hasNext = {
      while ( !stack.isEmpty && visited.contains(stack.head._1) ) { stack.pop }
      !stack.isEmpty
    }

    override def next = {
      while ( !stack.isEmpty && visited.contains(stack.head._1) ) { stack.pop }
      val (n, d) = stack.pop
      assert(!visited.contains(n))

      visited += n
      if ( !stopIf(n, d) ) {
        /* Put all those neighbouring nodes on the stack that have not been visited.
         * Sort in reverse order so that children are visited in left-right-order. */
        stack pushAll n.edges.view.map(_.nodes).reduceLeft(_++_).distinct.
          sorted(Ordering.ordered[Node[K,W]].reverse).view.filter(!visited.contains(_)).map((_,d+1))
      }
      (n,d)
    }
  }

  /**
   * Yields an iterator over this graph, that is its nodes. Iteration
   * is done breadth first in preorder. Nodes come with their depth in
   * the particular visit tree, with the root having depth 0.
   * The iterator is not thread-safe.
   * @param root The node the iterator starts at.
   * @param stopIf If `stopIf(n,d) == true`, edges originating in `n` at depth
   *               `d` are not traversed, i.e. recursion stops.
   * @return Iterator over the visited nodes in appropriate order.
   */
  def breadthFirstWithDepth(root : Node[K,W], stopIf : (Node[K,W], Int) => Boolean = (_,_) => false) : Iterator[(Node[K,W],Int)] = new Iterator[(Node[K,W],Int)] {
    private val visited = new HashSet[Node[K,W]]
    private val queue = new Queue[(Node[K,W],Int)]

    if ( nodes contains root ) {
      queue enqueue ((root, 0))
      visited += root
    }

    override def hasNext = !queue.isEmpty
    override def next = {
      val (n, d) = queue.dequeue

      if ( !stopIf(n, d) ) {
        /* Enqueue all those neighbouring nodes that have not been enqueued yet
         * Sort so that children are visited in left-right order. */
        queue enqueue (n.edges.view.map(_.nodes).reduceLeft(_++_).distinct.sorted.
                        view.filter(visited add _).map((_,d+1)) : _*)
      }
      (n, d)
    }
  }

  /**
   * Yields an iterator over this graph, that is its nodes. Iteration
   * is done depth first in preorder.
   * The iterator is not thread-safe.
   * @param root The node the iterator starts at.
   * @param stopIf If `stopIf(n) == true`, edges originating in `n` are not
   *               traversed, i.e. recursion stops.
   * @return Iterator over the visited nodes in appropriate order.
   */
  def depthFirst(root : Node[K,W], stopIf : Node[K,W] => Boolean = _ => false)
  : Iterator[Node[K,W]] = new Iterator[Node[K,W]] {
    private val iter = depthFirstWithDepth(root, (n,d) => stopIf(n))

    override def hasNext = iter.hasNext
    override def next = iter.next._1
  }

  /**
   * Yields an iterator over this graph, that is its nodes. Iteration
   * is done breadth first in preorder.
   * The iterator is not thread-safe.
   * @param root The node the iterator starts at.
   * @param stopIf If `stopIf(n) == true`, edges originating in `n` are not
   *               traversed, i.e. recursion stops.
   * @return Iterator over the visited nodes in appropriate order.
   */
  def breadthFirst(root : Node[K,W], stopIf : Node[K,W] => Boolean = _ => false)
  : Iterator[Node[K,W]] = new Iterator[Node[K,W]] {
    private val iter = breadthFirstWithDepth(root, (n,d) => stopIf(n))

    override def hasNext = iter.hasNext
    override def next = iter.next._1
  }
}

/**
 * Models graph nodes which are classified by categories and hold expression values.
 * @tparam K Key type
 * @tparam W Edge weight type
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance. `edgs` is evaluated lazily.
 * @param key This node's key
 * @param edgs Provides this node's incident edges
 * @param cats This node's categories
 * @param expr This node's expression values
 */
class Node[K <% Key[K],W](val key : K, edgs : => Seq[Edge[K,W]], val cats : Seq[K], val expr : Seq[Seq[Double]]) extends Ordered[Node[K,W]] {
  /**
   * This node's incident edges.
   */
  lazy val edges = edgs

  override def compare(that : Node[K,W]) = this.key compare that.key

  override def toString = key.toString
}
/**
 * Companion of class [[netgem.core.Node]]. Provides constructor alias and
 * extractor.
 */
object Node {
  /**
   * Creates a new instance of [[netgem.core.Node]].
   * @param key The new node's key
   * @param edgs Provides this node's incident edges
   * @param cats The new node's categories
   * @param expr The new node's expression values
   */
  def apply[K <% Key[K],W](key : K, edges : => Seq[Edge[K,W]], cats : Seq[K], expr : Seq[Seq[Double]]) = {
    new Node[K,W](key, edges, cats, expr)
  }

  def unapply[K <% Key[K],W](n : Node[K,W]) = Some((n.key, n.edges, n.cats, n.expr))
}

/**
 * Models graph edges which hold some weight.
 * @tparam K Key type
 * @tparam W Weight type
 * @author Raphael Reitzig
 *
 * @constructor Creates a new instance. `nods` is evaluated lazily.
 * @param key This edge's key
 * @param nods Provides this edge's incident nodes
 * @param weigth This edge's edge
 */
class Edge[K <% Key[K],W](val key : K, nods : => Seq[Node[K,W]], var weight : W) extends Ordered[Edge[K,W]] {
  /**
   * This edge's incident nodes.
   */
  lazy val nodes = nods

  override def compare(that : Edge[K,W]) = this.key compare that.key

  override def toString = key.toString
}
/**
 * Companion of class [[netgem.core.Edge]]. Provides constructor alias and
 * extractor.
 */
object Edge {
 /**
  * Creates a new instance of [[netgem.core.Edge]].
  * @param key The new edge's key
  * @param nods Provides this edge's incident nodes
  * @param weigth The new edge's edge
  */
  def apply[K <% Key[K],W](key : K, nodes : => Seq[Node[K,W]], weight : W) = {
    new Edge[K,W](key, nodes, weight)
  }

  def unapply[K <% Key[K],W](e : Edge[K,W]) = Some((e.key, e.nodes, e.weight))
}

/**
 * The companion of class [[netgem.core.WeightedHyperGraph]]. It provides an
 * extractor as well as a factory method for such graphs with powerful
 * guarantees. Check out its documentation for details.
 * @author Raphael Reitzig
 */
object WeightedHyperGraph {
  /**
   * This method takes raw graph data and constructs a valid `WeightedHyperGraph`
   * from it. That implies the following guarantees:
   *
   *  - Nodes, edges and categories have pairwise disjoint key sets, respectively
   *  - Node and category key duplicates are dropped
   *  - Edge key duplicates are disambiguated by renaming
   *  - There is exactle one `Node` (`Edge`) object per proper node (edge)
   *  - All node categories are listed in the categories member. If the mapping
   *    points to a key not present in the passed list, that key is dropped.
   *  - Category identifiers are pairwise different
   *  - Never referenced categories are dropped
   *  - The graph contains no isolated nodes
   *  - The graph contains only proper edges (i.e. with at least two nodes)
   *  - All nodes have the same number of expression series. If some have fewer
   *    than others, the get new zero series.
   *  - All nodes have expression series' of the same length. If the input
   *    series are unequally long, shorter ones are padded with zeroes at the
   *    end.
   *  - All values but the edge weights are immutable
   *  - The edge weights conform to the specified default weight (default: `0.0`)
   *  - All sequences are sorted with respect to `Key[K]`
   *
   * These criteria imply that improper nodes, edges and category mappings are
   * dropped. Whenever a nodes or edge is dropped, `Logger` is notified.
   * Category mappings are dropped silently.
   *
   * @param no Sequence of node keys
   * @param ed Sequence of edge keys with keys of incident nodes
   * @param ex Mapping of node keys to expression sequences
   * @param cats Sequence of category keys
   * @param cat Mapping of node keys to sequences of category keys
   * @param defaultWeight Default edge weight
   * @return Graph according to the specified data and above specification
   */
  def apply[K <% Key[K],W](
        no : Seq[K],
        ed : Seq[(K, Seq[K])],
        ex : K => Option[Seq[Seq[Double]]],
        cats : Seq[K],
        cat : K => Option[Seq[K]],
        defaultWeight : => W) : WeightedHyperGraph[K,W] =
  {
    val (nodes, edges) = {
      val e2nMap = new HashMap[K,ArrayBuffer[K]]()
      val n2eMap = new HashMap[K,ArrayBuffer[K]]()

      // Initialize node map and check for identifier duplicates
      val nodeDupl = new HashMap[K, Int]()
      no.foreach { n =>
        if ( !n2eMap.contains(n) ) {
          n2eMap += ((n, new ArrayBuffer[K]))
        }
        else {
          // Drop node but record this
          if ( !nodeDupl.contains(n) ) {
            nodeDupl += ((n, 1))
          }
          nodeDupl += ((n, nodeDupl(n) + 1))
        }
      }
      nodeDupl.foreach { case (k, n) =>
        (n + " duplicates of node '" + k +
            "' found. Dropping all but one.") logAs Warning
      }

      /* Fill node to edge map, initialize and fill
       * edge to node map and clear edge duplicates.
       */
      val edgeDupl = new HashMap[K, Int]()
      ed.foreach { case (e, n) => {
        val np = n.filter(n2eMap.get(_) != None)
        if ( !e2nMap.contains(e) ) {
          // So far, no duplicate of e
          e2nMap += ((e, ArrayBuffer[K](np : _*)))
          np.foreach(n2eMap(_) += e)
        }
        else {
          if ( !edgeDupl.contains(e) ) {
            // First duplicate of e; have to fix earlier added copy
            val oN  = e ~> 0
            e2nMap += ((oN, e2nMap(e)))
            e2nMap.remove(e)
            e2nMap(oN).foreach( n => {
              n2eMap(n) += oN
              n2eMap(n).remove(n2eMap(n) indexOf e)
            })
            edgeDupl += ((e, 1))
          }

          val eI = edgeDupl(e)
          val eN = e ~> eI
          e2nMap += ((eN, ArrayBuffer[K](np : _*)))
          edgeDupl += ((e, eI + 1))
          np.foreach(n2eMap(_) += eN)
        }
      }}
      edgeDupl.foreach { case (k, n) =>
        (n + " duplicates of edge '" + k +
         "' found. Renamed to '" + k + "_(0.." +
         (n-1) + ")'.") logAs Warning
      }

      /**
       * This function removes the passed node from the node2edge map as well as
       * all "references" to it from the edge2node map.
       */
      val removeNode = (n : K) => {
        n2eMap(n).foreach { e2nMap(_) -= n }
        n2eMap -= n
      }

      /**
       * This function removes the passed edge from the edge2node map as well as
       * all "references" to it from the node2edge map.
       */
      val removeEdge = (e : K) => {
        e2nMap(e).foreach { n2eMap(_) -= e }
        e2nMap -= e
      }

      // We want to keep only those nodes with at least an expression and category, resp.
      n2eMap.foreach { case (n, _) =>
        (ex(n), cat(n)) match {
          case (Some(Seq(Seq(_,_*), _*)), Some(Seq(_, _*))) => {
            if ( cat(n).get.intersect(cats).isEmpty ) {
              removeNode(n)
              ("Node " + n + " removed (no valid categories).") logAs Warning
            }
          }
          case _ => {
            removeNode(n)
            ("Node " + n + " removed (no expressions or no categories).") logAs Warning
          }
        }
      }

      // Now, remove all edges that have at most one node left
      e2nMap.foreach { case (e, n) =>
        if ( n.size < 2 ) {
          removeEdge(e)
          ("Edge " + e + " removed (less than two nodes).") logAs Warning
        }
      }

      // Now we might have nodes without edges left; remove those
      n2eMap.foreach { case (n, e) =>
        if ( e.isEmpty ) {
          removeNode(n)
          ("Node " + n + " removed (no edges).") logAs Warning
        }
      }

      /* Everything left in the maps is valid now. Assemble the final members now!
       * Have to to some trickery because of mutually referencing immutable objects.
       */
      val tmpN = HashMap[K, Node[K,W]]()
      val tmpE = HashMap[K, Edge[K,W]]()

      // Lets find out how many time steps were measured
      val steps = if ( n2eMap.isEmpty ) { 0 } else {
        n2eMap.keys.view.map(ex(_).get.map(_.size).max).max
      }

      // We also need to know the maximum number of strains
      val strains = if ( n2eMap.isEmpty ) { 0 } else {
        n2eMap.keys.view.map(ex(_).get.size).max
      }

      // Construct node objects
      n2eMap.foreach { case (n, e) => {
        tmpN += ((n, Node(n, e.map { tmpE(_) }.sorted,
                          cat(n).get.intersect(cats).distinct.toIndexedSeq.sorted,
                          ex(n).get.padTo(strains, Seq()).map(_.padTo(steps, 0.0).toIndexedSeq).toIndexedSeq)))
      }}

      // Construct edge objects
      e2nMap.foreach { case (e, n) => {
        tmpE += ((e, Edge(e, n.map { tmpN(_) }.sorted, defaultWeight)))
      }}

      // TODO with Scala 2.9, create both as ParSeq
      (tmpN.values.toSeq.sorted.toIndexedSeq,
       tmpE.values.toSeq.sorted.toIndexedSeq)
    }

    ("Graph has " + nodes.size + " nodes (" + (no.size - nodes.size) + " dropped)").log
    ("Graph has " + edges.size + " edges (" + (ed.size - edges.size) + " dropped)").log
    if ( !nodes.isEmpty ) {
      ("Graph has " + nodes(0).expr.size + " strains").log
      ("Graph has " + nodes(0).expr(0).size + " timesteps").log
    }

    // Remove duplicate category keys, drop unused
    val newcats = cats.distinct.view.filter(c => nodes.exists(_.cats.contains(c))).sorted.force.toIndexedSeq
    ("Graph has " + newcats.size + " categories").log

    new WeightedHyperGraph[K,W](nodes, edges, newcats)
  }

  def unapply[K <% Key[K],W](g : WeightedHyperGraph[K,W]) = Some((g.nodes, g.edges, g.categories))
}
