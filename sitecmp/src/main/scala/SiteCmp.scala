import scalaj.http.Http
import org.xml.sax.InputSource
import scala.xml._
import parsing._
import nu.validator.htmlparser.{ sax, common }
import sax.HtmlParser
import common.XmlViolationPolicy
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class SiteCmp(val host: String, val user: String, val pass: String) {

  val url = "http://" + host + "/cs/CatalogManager"

  val render = "http://" + host + "/cs/Satellite"

  val parser = new HTML5Parser

  var cookie = ""

  def login() = {
    val req = Http(url)
      .params("ftcmd" -> "login",
        "username" -> user,
        "password" -> pass)

    //println("***" + req.getUrl)

    val (c, m) = req.asCodeHeaders
    println(m)
    cookie = m("Set-Cookie")
    c
  }

  def select() = {
    val out = Http(url).header("Cookie", cookie)
      .params("ftcmd" -> "selectrow(s)",
        "tablename" -> "Page",
        "selwhat" -> "id,status").asBytes
    val is = new InputSource(new ByteArrayInputStream(out))
    parser.loadXML(is)

  }

  def selectSitePlan() = {
    val out = Http(url).header("Cookie", cookie)
      .params("ftcmd" -> "selectrow(s)",
        "tablename" -> "SitePlanTree",
        "selwhat" -> "ncode,otype,oid").asBytes
    val is = new InputSource(new ByteArrayInputStream(out))
    parser.loadXML(is)

  }

  def retrieve(id: String): Option[String] = {
    val req = Http(render.format(id))
      .params(
        "cid" -> id,
        "c" -> "Page",
        "childpagename" -> "TelmoreSite/TLayout",
        "pagename" -> "Telmore/TWrapper")

    try {

      val url = req.getUrl
      println(url)

      val lines = scala.io.Source.fromURL(url).getLines

      val filtered = for (line <- lines) yield {
        var nline = line
          .replaceAll("""[\t\r ]+""", " ")
          .replaceFirst("""[ ]*\n$""", "")
          .replaceAll("""\d{6,}""", "999999999")
          .trim

        /*

        if (!"/cs/Satellite/img/\\d+/".r.findFirstIn(nline).isEmpty)
          nline = nline.replaceAll("/cs/Satellite/img/\\d+/", "/cs/Satellite/img/999999999/")

        if (!"http://www.telmore.dk/\\d+/".r.findFirstIn(nline).isEmpty)
          nline = nline.replaceAll("http://www.telmore.dk/\\d+/", "http://www.telmore.dk/999999999/")
          */

        if (nline.contains("JSESSIONID"))
          nline = nline.replaceFirst("value=[A-Z0-9]+", "value=999999999")

        if (!"""\w+\.prod\.dk\.telmore\.net""".r.findFirstIn(nline).isEmpty)
          nline = nline.replaceFirst("""\w+\.prod\.dk\.telmore\.net""", """xxx.prod.dk.telmore.net""")

        if (nline.contains("javascript:openCustomWindow("))
          nline = nline.replaceAll("""javascript:openCustomWindow\(.*\)""", """javascript:openCustomWindow('XXX')""")

        nline
      }

      val res = filtered filter { _.size > 0 } mkString "\n"

      Some(res)

    } catch {
      case e =>
        e.printStackTrace()
        throw new Exception(req.getUrl.toString, e)

    } finally {
      None
    }

    /*try {
      req.asString
    } catch {
      // try again....
      case _ =>
        println("(trying again for " + id + ")")
        try {
          scala.io.Source.fromURL(req.getUrl).mkString
        } catch {
          case e =>
            e.printStackTrace()
            throw new Exception(req.getUrl.toString, e)
        }
    }*/
  }

}

object SiteCmp {

  var dir: File = null
  var me: SiteCmp = null

  var max = -1
  var found = List[String]()

  def tidy(file: File) {
    if (file.exists())
      try {
        org.w3c.tidy.Tidy.main(Array("-c",
          "-quiet", "-f", "tidy.log", "-i", "-wrap", "2048", "-utf8", "-m",
          file.getAbsolutePath()))
      } catch {
        case e => println("???" + file + "\n" + e)
      }
  }

  def dumpById(id: String): Option[String] = {
    val file = new File(dir, "Page_" + id + ".html")
    import org.jsoup.Jsoup
    try {
      me.retrieve(id) match {
        case Some(body) =>
          val dom = Jsoup.parse(body)
          dom.select("div#NavigationMenu").remove()
          //dom.select("ul.submenu").remove()
          //dom.select("li.NavigationMenu").remove()
          /*
          import scala.collection.JavaConversions._
          val it = dom.select("tr.trUltraLight").iterator().toList
          for (el <- it) {
            el.
            el.nextSibling().remove()
            el.remove()
          }*/
          dom.select("tr.trUltraLight + tr ").remove()
          dom.select("tr.trUltraLight").remove()
          

          val out = dom.toString
          val fw = new java.io.FileWriter(file)
          fw.write(out)
          fw.close
          println("%s (%d) ".format(id, out.size))
          Some(id)
        //tidy(file)          
        case None =>
          println("%s !!! skipped".format(id))
          None
      }
    } catch {
      case e =>
        println("%s !!! %s".format(id, e.getMessage))
        val file = new File(dir, "Err_" + id + ".err")
        val fw = new FileWriter(file)
        val pw = new PrintWriter(fw)
        //pw.println(me.retrieve_url(id))
        e.printStackTrace(pw)
        fw.close
        None
    }

  }

  def main(args: Array[String]) {

    me = new SiteCmp(args(0), args(1), args(2))

    me.login

    dir = new File(args(0).replace(":", "-"))
    dir.mkdirs()

    if (args.length > 3) {
      val ls = args.tail.tail.tail

      for (id <- ls) {
        dumpById(id)
      }

    } else {

      val table = me.selectSitePlan

      val rows = table \\ "tr"

      val ls = for (row <- rows) {

        //println(row)

        val ls = row \\ "td" map (_.text)
        if (ls.size > 0) {

          //val id = ls(0)

          val (ncode, otype, oid) = (ls(6), ls(3), ls(4))
          println("%s %s %s".format(ncode, otype, oid))

          if (otype == "Page" && ncode == "Placed") {

            //println("%s %s".format(otype, oid))
            dumpById(oid) match {

              case Some(id) =>
                found = id :: found
                if (found.length == max) {
                  println(found mkString " ")
                  System.exit(0)
                }
              case None => None
            }

          }

        }
        // println(vv+vv.size)

      }
      //println(table)

    }
    //println(rows)
  }

}