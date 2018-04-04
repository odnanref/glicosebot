package facebook.server.message.payload

import facebook.server.message.actions.Button
import facebook.server.message.{Element, TAttachmentPayload}
import play.api.libs.json.{Json, Writes}

import scala.collection.mutable.ListBuffer

case class AttachmentImagePayload(var url: String) extends TAttachmentPayload {
  var elements : Option[ListBuffer[Element]] = None
  var buttons: Option[ListBuffer[Button]] = None
  var templateType = ""
}

object AttachmentImagePayload {
  implicit val jsonAttachmentPayloadWrites = new Writes[AttachmentImagePayload] {

    def writes(payload: AttachmentImagePayload) = {
      Json.obj(
      "url" -> payload.url
      )
    }
  }
}