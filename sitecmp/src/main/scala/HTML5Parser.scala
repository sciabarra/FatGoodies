import scalaj.http.Http
import org.xml.sax.InputSource
import scala.xml._
import parsing._
import nu.validator.htmlparser.{sax,common}
import sax.HtmlParser
import common.XmlViolationPolicy
import java.io.ByteArrayInputStream
import java.io.File

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
