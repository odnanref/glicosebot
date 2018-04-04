package facebook.server.message

import facebook.server.message.actions.Button
import play.api.libs.json.{Json, Writes}

import scala.collection.mutable.ListBuffer

/**
  * Attachment Payload of the Attachment
  *
  * Created by andref on 11-07-2017.
  */
case class AttachmentPayload(var templateType: String,
                             var elements: Option[ListBuffer[Element]] = None,
                             var buttons: Option[ListBuffer[Button]] = None) extends TAttachmentPayload  {
  var url = "" // Not implemented, not needed

}

object AttachmentPayload {

  implicit val jsonAttachmentPayloadWrites = new Writes[AttachmentPayload] {

    def writes(payload: AttachmentPayload) = {
      val templateType = payload.templateType.toLowerCase()
      val mappedByMe = if (templateType.equals("generic")) {
        Json.obj(
          "template_type" -> payload.templateType,
          "elements" -> payload.elements
        )
      } else {
        Json.obj(
          "template_type" -> payload.templateType,
          "top_element_style" -> "compact",
          "elements" -> payload.elements,
          "buttons" -> payload.buttons
        )
      }

      mappedByMe
    }
  }

}