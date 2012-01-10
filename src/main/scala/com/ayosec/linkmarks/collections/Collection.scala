package com.ayosec.linkmarks.collections

import com.ayosec.linkmarks.GraphDatabase

import org.neo4j.graphdb.Node
import org.neo4j.kernel.Traversal
import org.neo4j.graphdb.Path
import org.neo4j.graphdb.traversal.Evaluator
import org.neo4j.graphdb.traversal.Evaluation._

trait Collection[Model, InstanceBuilder] {

  val backend: GraphDatabase
  val typeName: String

  def create(callback: InstanceBuilder => Unit): Model
  def create(node: Node): Model

  def getAll = {
    val traverser = Traversal.description.
      breadthFirst.
      evaluator( new Evaluator {
        def evaluate(path: Path) = {
          if(path.length != 0 && path.endNode.getProperty("type", "").asInstanceOf[String] == typeName)
            INCLUDE_AND_CONTINUE
          else
            EXCLUDE_AND_CONTINUE
        }
      })

    val nodeIterator = traverser.traverse(backend.instance.getReferenceNode).iterator
    var foundNodes = List[Model]()

    while(nodeIterator.hasNext)
      foundNodes = create(nodeIterator.next.endNode) +: foundNodes

    foundNodes
  }

  
}
