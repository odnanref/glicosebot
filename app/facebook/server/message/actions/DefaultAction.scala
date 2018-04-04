package facebook.server.message.actions

import play.api.libs.json.{Json, Writes}

/**
  * Default Action for Message Attachment Payload
  *
  * Created by andref on 11-07-2017.
  */
case class DefaultAction(Type: String,
                         url: String,
                         messengerExtensions: Boolean,
                         fallbackUrl: String) {

}

object DefaultAction {

  implicit val jsonDefaultActionWrites = new Writes[DefaultAction] {

    def writes(da: DefaultAction) = {
      Json.obj(
        "type" -> da.Type,
        "url" -> da.url,
        "messenger_extensions" -> da.messengerExtensions,
        "fallback_url" -> da.fallbackUrl
      )
    }
  }
}
