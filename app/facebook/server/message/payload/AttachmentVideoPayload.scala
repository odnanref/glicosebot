package facebook.server.message.payload

import facebook.server.message.{Element, TAttachmentPayload}
import facebook.server.message.actions.Button

import scala.collection.mutable.ListBuffer

case class AttachmentVideoPayload(var url: String) extends TAttachmentPayload {
  var elements : Option[ListBuffer[Element]] = None
  var buttons: Option[ListBuffer[Button]] = None
  var templateType = ""
}
