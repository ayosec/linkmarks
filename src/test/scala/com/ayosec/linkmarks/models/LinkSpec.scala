package com.ayosec.linkmarks.models

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.joda.time.DateTime

import com.ayosec.linkmarks.Relations
import com.ayosec.linkmarks.TestHelpers

class LinkSpec extends FlatSpec with ShouldMatchers {

  it should "create a simple link" in {
    val db = TestHelpers.createDatabase

    // Create some useless nodes
    db.transaction { tx =>
      val rootNode = db.instance.getReferenceNode
      (1 to 10) foreach { i =>
        db.instance.createNode.createRelationshipTo(rootNode, Relations.WithContent)
      }
    }

    val newLink = db.links.create { l =>
      l.date = new DateTime(1100000000000L) // 2004-11-09T11:33:20.000Z
      l.tags = List("foo", "bar")
      l.notes = "Lorem ipsum..."
      l.title = "Some title"
      l.link = "http://foo.bar/"
      l.fromRoot = true
    }

    // Find it
    val links = db.links.getAll
    links.length should be (1)
    links(0).notes should be ("Lorem ipsum...")
    links(0).title should be ("Some title")
    links(0).link should be ("http://foo.bar/")
    links(0).date.toString("y-MM-dd hh-mm-ss") should be ("2004-11-09 11-33-20")
    links(0).tags should be (Set("foo", "bar"))


    // Access to the raw Neo4j node
    val node = db.instance.getNodeById(newLink.rawNode.getId)
    node.getProperty("type") should be ("link")
  }

  it should "find links with tags" in {
    val db = TestHelpers.createDatabase

    // Some links with tags
    db.links.create { l =>
      l.tags = List("foo", "bar")
      l.link = "a"
    }

    db.links.create { l =>
      l.tags = List("bar")
      l.link = "b"
    }

    db.links.create { l =>
      l.tags = List("foo")
      l.link = "c"
    }

    // Find nodes with the tag "foo"
    val nodes = db.tag("foo").links
    nodes.map(_.link).toSet should be (Set("a", "c"))

  }

}
