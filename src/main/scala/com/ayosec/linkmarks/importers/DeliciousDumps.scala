package com.ayosec.linkmarks.importers

import scala.collection.JavaConversions._

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Callable

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
    val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)

    val futures =
      for(fileName <- new File(source).listFiles)
        yield threadPool.submit(new ParseLinks(fileName, db))

    threadPool.shutdown

    var acc = 0
    for(future <- futures) {
      acc += future.get
      print("\r" + acc + " links")
    }
  }

  final class ParseLinks(file: File, db: GraphDatabase) extends Callable[Int] {
    def call = {
      var count = 0

      val links = Jsoup.
                    parse(file, "UTF-8", deliciousUrl).
                    select("div.link")

      for(link <- links) {
        val date = new DateTime(link.attr("date").toInt * 1000L)
        val tags = link.select(".tag .name") map { _.text }
        val note = link.select(".note") map { _.text } mkString "\n"

        val linkTag = link.select(".share").first
        val title = linkTag.attr("title")
        val href = linkTag.attr("href")

        //println("%s - %s - %s - %s".format(date, href, tags, note))
        db.synchronized {
          val newLink = db.links.create { l =>
            l.date = date
            l.tags = tags.toList
            l.notes = note
            l.title = title
            l.link = href
            l.fromRoot = true
          }
        }

        count += 1
      }

      // Return the number of found links
      count
    }
  }
}

