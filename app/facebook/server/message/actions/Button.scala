package facebook.server.message.actions

import play.api.libs.json.{Json, Writes}

/**
  * Button for a message Attachment Payload
  *
  * Created by andref on 11-07-2017.
  */
sealed trait Button {

  var title: String = ""

  var Type: String = ""

  var url: String = ""

  var messengerExtensions : Boolean = true;

  var fallbackUrl : String = ""

  var payload: String = ""
}

object Button {

  implicit val jsonButtonWebUrlWrites = new Writes[ButtonWebUrl] {

    def writes(button: ButtonWebUrl) = {
      Json.obj(
        "title" -> button.title,
        "type" -> button.Type,
        "url" -> button.url,
        "messenger_extensions" -> button.messengerExtensions,
        "fallback_url" -> button.fallbackUrl
      )
    }
  }

  implicit val jsonButtonPayloadWrites = new Writes[ButtonPayload] {

    def writes(button: ButtonPayload) = {
      Json.obj(
        "title" -> button.title,
        "type" -> button.Type,
        "payload" -> button.payload
      )
    }
  }

  implicit val jsonButtonWrites = new Writes[Button] {

    def writes(button: Button) = {
      if (button.Type.toLowerCase.equals("postback")) {
        Json.obj(
          "title" -> button.title,
          "type" -> button.Type,
          "payload" -> button.payload
        )
      } else {
        Json.obj(
          "title" -> button.title,
          "type" -> button.Type,
          "url" -> button.url,
          "messenger_extensions" -> button.messengerExtensions,
          "fallback_url" -> button.fallbackUrl
        )
      }
    }
  }
}

case class ButtonWebUrl(btn_title: String, btn_url: String, btn_fallbackUrl:String) extends Button {
  title = btn_title
  url = btn_url
  fallbackUrl = btn_fallbackUrl

  Type = "web_url"
}

case class ButtonPayload( btn_title: String, btn_payload: String) extends Button {
  title = btn_title
  payload = btn_payload
  Type = "postback"
}

