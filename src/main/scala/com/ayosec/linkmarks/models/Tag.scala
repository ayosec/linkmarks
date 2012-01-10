package com.ayosec.linkmarks.models

import com.ayosec.linkmarks.GraphDatabase
import com.ayosec.linkmarks.Relations
import org.neo4j.graphdb.Node

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

}
