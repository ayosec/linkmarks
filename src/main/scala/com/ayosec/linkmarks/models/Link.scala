package com.ayosec.linkmarks.models

import org.joda.time.DateTime
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Direction

import collection.JavaConversions._

import com.ayosec.linkmarks.Relations
import com.ayosec.linkmarks.GraphDatabase

class LinkBuilder {
  var date: DateTime = null
  var notes: String  = null
  var title: String  = null
  var link: String   = null
  var tags           = List[String]()

  var fromRoot: Boolean = false
}

class Link private (val backend: GraphDatabase) extends Model {

  def this(backend: GraphDatabase, callback: LinkBuilder => Unit) {
    this(backend)

    val builder = new LinkBuilder
    callback(builder)

    backend.transaction { tx =>
      node = backend.instance.createNode
      node.setProperty("type", "link")

      if(builder.date  != null) node.setProperty("date",  builder.date.getMillis)
      if(builder.notes != null) node.setProperty("notes", builder.notes)
      if(builder.title != null) node.setProperty("title", builder.title)
      if(builder.link  != null) node.setProperty("link",  builder.link)

      for(tagName <- builder.tags) {
        Tag.get(backend, tagName).rawNode.createRelationshipTo(node, Relations.TaggedLink)
      }

      // Attach to the root node?
      if(builder.fromRoot)
        backend.instance.getReferenceNode.createRelationshipTo(node, Relations.HasLink)
    }
  }

  def this(backend: GraphDatabase, rawNode: Node) = {
    this(backend)
    node = rawNode
  }

  def notes = node.getProperty("notes", null).asInstanceOf[String]
  def title = node.getProperty("title", null).asInstanceOf[String]
  def link = node.getProperty("link", null).asInstanceOf[String]

  def date = {
    val date = node.getProperty("date", null)
    if(date != null) new DateTime(date.asInstanceOf[Long]) else null
  }

  def tags = {
    (node.
      getRelationships(Relations.TaggedLink, Direction.INCOMING) map { node =>
        node.getStartNode.getProperty("name").asInstanceOf[String]
      }
    ).toSet
  }

}
