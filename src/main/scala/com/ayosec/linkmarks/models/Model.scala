package com.ayosec.linkmarks.models

import org.neo4j.graphdb.Node

trait Model {

  protected var node: Node = null

  def rawNode = node

}
