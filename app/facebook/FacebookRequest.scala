package facebook

import facebook.ShopConfig
import play.api.libs.json._
import play.Logger

import scalaj.http.{Http}

case class Error(message:String, Type:String, code:String, error_subcode: String, fbtrace_id:String)

object Error {
  implicit val jsonError = Json.format[Error]
}

/**
  * Send a facebook Bot message request
  *
  * @author Created by andref on 25-05-2017.
  */
class FacebookRequest(fconfig:ShopConfig) {
  val url:String = "https://graph.facebook.com/v2.6/me/messages"
  val  qs:String = "access_token=" + fconfig.PAGE_ACCESS_TOKEN

  /**
    * transform for facebook message to facebook message json
    */
  implicit val jsonMessageWrites = new Writes[JsonMessage] {

    /**
      * transform from facebook recipient to json recipient
      *
      */
    implicit val jsonFRecipientWrites = new Writes[FacebookRecipient] {
      def writes(fr: FacebookRecipient) = Json.obj(
        "id" -> fr.id
      )
    }

    def writes(jm: JsonMessage) = {
      val mappedByMe = if (jm.attachment.isEmpty) {
        Json.obj("text" -> jm.message)
      } else {
        Json.obj("attachment" -> jm.attachment.get)
      }

      Json.obj(
        "recipient" -> Json.toJson(jm.recipient),
         "message" -> mappedByMe
      )
    }
  }

  /**
    * handle error messages from facebook
    *
    * depending on the error the message can be thrown back into a queue
    * for resending
    *
    * @param message
    */
  def handleError(message: StringBuilder) : Unit = {
    // TODO implementing code
    val error : Error = Json.parse(message.toString()).as[Error]
    if (error.code.equals("1200")) {
      // back to queue for sending later
    } else if(error.code.equals("4") || error.code.equals("613")) {
      // send the message back to queue and place a delay on the next sending of messages
    } else if(error.code.equals("100") && error.error_subcode.equals("2018109")) {
      // Fatal error , attachment size exceeds limit placed by facebook
    }
  }

  /**
    * post the message to the URL
    *
    */
  def sendMessage(fmessage: JsonMessage) = {
    Logger.debug("sendMessage:" + Json.toJson(fmessage).toString())

    val result = Http(this.url + "?" + qs)
      .postData(Json.toJson(fmessage).toString())
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")

    val resultMessage:StringBuilder = new StringBuilder()

    result.execute(is => {
      scala.io.Source.fromInputStream(is).getLines().foreach( msg => {
        resultMessage.append(msg)
        Logger.debug("Output from facebook server after message send " + msg.toString())
      })
    })

    if (resultMessage.contains("error")) {
      handleError(resultMessage)
    }
  }

}
