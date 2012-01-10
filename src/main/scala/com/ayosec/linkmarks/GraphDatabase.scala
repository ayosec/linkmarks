package com.ayosec.linkmarks

import java.util.Iterator
import java.util.HashMap

import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.index.Index
import org.neo4j.kernel.EmbeddedGraphDatabase

class GraphDatabase(val rootPath: String) {

  val instance = {
    // Creates a new EmbeddedGraphDatabase instance and
    // shutdown it when the VM process is exiting
    val db = new EmbeddedGraphDatabase(rootPath)
    sys.addShutdownHook { db.shutdown() }
    db
  }

  lazy val links = new collections.Links(this)

  def transaction[T](callback: Transaction => T): T = {
    val tx = instance.beginTx
    try {
      val result = callback(tx)
      tx.success

      result
    } finally {
      tx.finish
    }
  }

  def root = instance.getReferenceNode

  // Indexes

  private val mapIndexes = new HashMap[String, Index[Node]]
  private lazy val indexManager = instance.index

  def getIndex(indexName: String) = mapIndexes.synchronized {
    var index = mapIndexes.get(indexName)
    if(index == null) {
      index = indexManager.forNodes(indexName)
      mapIndexes.put(indexName, index)
    }

    index
  }

}
