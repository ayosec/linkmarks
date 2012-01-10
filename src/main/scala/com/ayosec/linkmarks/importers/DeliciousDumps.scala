package com.ayosec.linkmarks.importers

import scala.collection.JavaConversions._

import java.io.File
import org.jsoup.Jsoup
import org.joda.time.DateTime

import com.ayosec.linkmarks.GraphDatabase

object DeliciousDumps extends SourceParser {

  /* The source of this importer is a directory with files downloaded with
   * $ wget -c https://www.delicious.com/USERNAME?\&page={1..200}
   * curl or any other wget-like command can be used
   *
   * It will be better to use the export functionality, available at
   * http://export.delicious.com/settings/bookmarks/export,
   * but it is not working
   */

  val deliciousUrl = "http://delicious.com"

  def fromSource(source: String, db: GraphDatabase) {
    for(fileName <- new File(source).listFiles) {
      val links = Jsoup.
                    parse(fileName, "UTF-8", deliciousUrl).
                    select("div.link")

      for(link <- links) {
        val date = new DateTime(link.attr("date").toInt * 1000L)
        val href = link.select(".share").first.attr("href")
        val tags = link.select(".tag .name") map { _.text }
        val note = link.select(".note") map { _.text } mkString "\n"

        //println("%s - %s - %s - %s".format(date, href, tags, note))
        val newLink = db.links.create { l =>
          l.date = date
          l.tags = tags.toList
          l.notes = note
          l.title = href
          l.link = href
          l.fromRoot = true
        }

      }
    }
  }

}
