import scalaj.http.Http
import org.xml.sax.InputSource
import scala.xml._
import parsing._
import nu.validator.htmlparser.{sax,common}
import sax.HtmlParser
import common.XmlViolationPolicy
import java.io._

class HTML5Parser extends NoBindingFactoryAdapter {

  override def loadXML(source: InputSource, _p: SAXParser): scala.xml.Node = {
    loadXML(source)
  }

  def loadXML(source: InputSource): scala.xml.Node = {

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }
}

class ElementExporter(val host: String, val user: String, val pass: String) {

  val url = "http://"+host+"/cs/CatalogManager"
  
  val parser = new HTML5Parser

  var cookie = ""

  def login() = {
    val (c, m) = Http(url)
      .params("ftcmd" -> "login", "username" -> user , "password" -> pass)
      .asCodeHeaders
    println(m)
    cookie = m("Set-Cookie")
    c
  }

  def select() = {
    val out = Http(url).header("Cookie", cookie)
      .params("ftcmd" -> "selectrow(s)",
        "tablename" -> "elementcatalog",
        "selwhat" -> "elementname,url").asBytes
    val is = new InputSource(new ByteArrayInputStream(out))
    parser.loadXML(is)

  }
  
  def retrieve(key: String) = {
    Http(url).header("Cookie", cookie)
      .params("ftcmd" -> "retrievebinary",
        "retrievestatus" -> "false",
        "tablename" -> "ElementCatalog",
        "columnname" -> "url",
        "tablekey" -> "elementname",
        "tablekeyvalue" -> key).asString    
  }

}

object ElementExporter {

  def main(args: Array[String]) = {
    
    val me = new ElementExporter(args(0)+":"+args(1), args(2), args(3))
    
    println(me.login)

    val table = me.select
    
    val dir = new java.io.File(args(0).replace(":", "/"))
    dir.mkdirs()

    //println(table)

    val rows = table \\ "tr"
    for (row <- rows) {
      val ls = row \\ "td" map (_.text)

      if (ls.size > 0) {
 
        val body = me.retrieve(ls(0))
         
        println("%s (%s)".format(ls(0), body.size))
       
        val file = new File(dir, "./"+ls(0))
               
        val fw = new java.io.FileWriter(file)        
        fw.write(body)
        fw.close
                        
      }
      // println(vv+vv.size)
    }

    //println(rows)
  }
}
