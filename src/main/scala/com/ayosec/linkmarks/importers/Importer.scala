package com.ayosec.linkmarks.importers

import com.ayosec.bindize._
import com.ayosec.linkmarks.GraphDatabase

object Importer {

  def main(args: Array[String]) {
    import System._

    val validImporters = List("DeliciousDumps")

    var importerName = ""
    var dbPath = ""
    var sources = List[String]()

    val parser = new Parser with NonOptionArguments {

      // Importer to use
      the option "-i" withParam { importerName = _ }

      // Database path
      the option "-r" withParam { dbPath = _ }

      // List of available importers
      the option "-l" is {
        validImporters foreach { println(_) }
        exit(0)
      }

    }

    parser.parse(args)

    if(importerName == "") {
      err.println("Need an importer with -i. Use -l to list them")
      exit(1)
    }

    if(dbPath == "") {
      err.println("Need a path to store the database. Use the -r option")
      exit(1)
    }

    // Static-dynamism
    // This is based on the Scala 2.9.1 conventions. Of course,
    // it is not the-right-way to do this
    val importer = Class.
      forName("com.ayosec.linkmarks.importers." + importerName + "$").
      getField("MODULE$").
      get().asInstanceOf[SourceParser]

    val db = new GraphDatabase(dbPath)
    for(source <- parser.arguments) {
      println("Loading " + source + " with " + importerName)
      importer.fromSource(source, db)
    }

  }
  
}
