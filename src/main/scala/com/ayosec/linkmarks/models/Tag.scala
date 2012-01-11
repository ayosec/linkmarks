package com.ayosec.linkmarks.models

import com.ayosec.linkmarks.GraphDatabase
import com.ayosec.linkmarks.Relations

import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Path
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluator
import org.neo4j.graphdb.traversal.Evaluation._
import org.neo4j.kernel.Traversal

object Tag {

  // Find a tag with the specified name. If the tag does not
  // exist it will be created.
  def get(backend: GraphDatabase, name: String) = {
    val index = backend.getIndex("tags")
    var node = index.get("name", name).getSingle

    if(node == null) {
      // Create a new tag
      backend.transaction { tx =>
        node = backend.instance.createNode
        node.createRelationshipTo(backend.root, Relations.Tag)
        node.setProperty("type", "tag")
        node.setProperty("name", name)

        // Add to the index
        index.add(node, "name", name)
      }
    }

    new Tag(backend, node)
  }
}

class Tag private (val backend: GraphDatabase) extends Model {

  def this(backend: GraphDatabase, node: Node) {
    this(backend)
    this.node = node
  }

  def name = node.getProperty("name")

  def add(link: Link) = node.createRelationshipTo(link.rawNode, Relations.TaggedLink)

  // Find all the nodes with this tag
  def links = {
    val traverser = Traversal.description.
      breadthFirst.
      relationships(Relations.TaggedLink, Direction.OUTGOING).
      evaluator(new Evaluator {
        def evaluate(path: Path) = {
          if(path.endNode.getProperty("type", null) == "link")
            INCLUDE_AND_CONTINUE
          else
            EXCLUDE_AND_CONTINUE
        }
      })

    val nodeIterator = traverser.traverse(node).iterator
    var foundNodes = List[Link]()

    while(nodeIterator.hasNext)
      foundNodes = new Link(backend, nodeIterator.next.endNode) +: foundNodes

    foundNodes
  }
}
