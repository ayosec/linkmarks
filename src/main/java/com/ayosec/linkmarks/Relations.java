package com.ayosec.linkmarks;

import org.neo4j.graphdb.RelationshipType;

public enum Relations implements RelationshipType {
  WithContent,
  Tag,
  TaggedLink,
  HasLink;
}
