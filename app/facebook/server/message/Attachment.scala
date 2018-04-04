package facebook.server.message

import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Attachment of a message
  *
  * Created by andref on 11-07-2017.
  */
case class Attachment( Type: String = "template", var payload: TAttachmentPayload){

}

object Attachment {

  implicit val jsonAttachmentWrites = new Writes[Attachment] {

    def writes(attachment: Attachment) = {
      Json.obj(
        "type" -> attachment.Type,
        "payload" -> attachment.payload
      )
    }
  }

}
