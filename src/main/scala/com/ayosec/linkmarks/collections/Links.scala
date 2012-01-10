package com.ayosec.linkmarks.collections

import com.ayosec.linkmarks.models._
import com.ayosec.linkmarks.GraphDatabase

import org.neo4j.graphdb.Node

class Links(val backend: GraphDatabase) extends Collection[Link, LinkBuilder] {

  val typeName = "link"

  def create(callback: LinkBuilder => Unit) = new Link(backend, callback)
  def create(node: Node) = new Link(backend, node)

}
