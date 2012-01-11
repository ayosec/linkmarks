package com.ayosec.linkmarks.models

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.ayosec.linkmarks.TestHelpers

class TagSpec extends FlatSpec with ShouldMatchers {

  it should "group tags with any level" in {
    val db = TestHelpers.createDatabase

    // Some links with tags
    db.links.create { l =>
      l.tags = List("foo", "bar")
      l.link = "a"
    }

    db.links.create { l =>
      l.tags = List("three")
      l.link = "b"
    }

    db.links.create { l =>
      l.tags = List("out")
      l.link = "c"
    }

    // Create grouped tags
    db.transaction { tx =>
      db.tag("foo").add(db.tag("bar"))
      db.tag("bar").add(db.tag("three"))
      db.tag("root").add(db.tag("foo"))
    }

    // Get the link
    val nodes = db.tag("root").links
    nodes.length should be (2)
    nodes.map(_.link).toSet should be (Set("a", "b"))
  }
}
