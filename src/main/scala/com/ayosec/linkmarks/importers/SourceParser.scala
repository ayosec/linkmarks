package com.ayosec.linkmarks.importers

import com.ayosec.linkmarks.GraphDatabase

trait SourceParser {

  def fromSource(source: String, db: GraphDatabase)

}
