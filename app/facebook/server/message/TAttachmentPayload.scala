package facebook.server.message

import facebook.server.message.actions.Button
import play.api.libs.json.{Json, Writes}

import scala.collection.mutable.ListBuffer

abstract class TAttachmentPayload {

  var elements : Option[ListBuffer[Element]]
  var buttons: Option[ListBuffer[Button]]
  var templateType: String
  var url : String

}

object TAttachmentPayload {

  implicit val jsonTAttachmentPayloadWrites = new Writes[TAttachmentPayload] {

    def writes(payload: TAttachmentPayload) = {
      val templateType = payload.templateType.toLowerCase()
      val mappedByMe = if (templateType.equals("generic")) {
        Json.obj(
          "template_type" -> payload.templateType,
          "elements" -> payload.elements
        )
      } else if (templateType.equals("")) {
        Json.obj(
          "url" -> payload.url
        )
      } else {
        Json.obj(
          "template_type" -> payload.templateType,
          "top_element_style" -> "compact",
          "elements" -> payload.elements,
          "buttons" -> payload.buttons.get
        )
      }

      mappedByMe
    }

  }
}