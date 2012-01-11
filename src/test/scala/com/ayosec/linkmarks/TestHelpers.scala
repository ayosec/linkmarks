package com.ayosec.linkmarks

import sys.process.Process
import java.io.File

object TestHelpers {

  def createDatabase = {
    val tempName = File.createTempFile("neo4j", "volatile")
    tempName.delete
    tempName.mkdirs

    // Clean everything at the exit
    sys.addShutdownHook { Process(List("rm", "-rf", tempName.getAbsolutePath)) !  }

    new GraphDatabase(tempName.getAbsolutePath)
  }

}
