package facebook

import facebook.server.message._
import facebook.server.message.payload.{AttachmentAudioPayload, AttachmentFilePayload, AttachmentImagePayload, AttachmentVideoPayload}
import play.Logger
import play.api.libs.json.{JsValue}
import play.api.mvc._

import scala.collection.mutable.ListBuffer

/**
  * Intended to parse an incoming Json Message from Facebook Webhooks request
  *
  * This shouldn't have this many vars :S Only now I started understanding how functional goes
  * 2018-03-20
  *
  * Created by andref on 27-05-2017.
  */
class FBodyParser(request: JsValue, handler: Option[MessageHandler] = None ) {

  Logger.debug("String received:" + request.toString())

  val FobjectType = new FacebookObject((request \ "object").asOpt[String].getOrElse("FAILED"))

  val entriesjs = (request \ "entry").as[List[JsValue]]

  val entries : List[Entry] = entriesjs.map {
    f =>
      val entry_id:String = (f \ "id").asOpt[String].getOrElse("FAIL")
      val entry_time:Long = (f \ "time").asOpt[Long].getOrElse(909)
      val messages = (f \ "messaging").asOpt[List[JsValue]]

      val tmpPolicyEnforcement = (f \ "policy-enforcement").asOpt[JsValue]

      tmpPolicyEnforcement match {
        case Some(policy) => PolicyEnforcement((policy \ "action").as[String],
          (policy \ "reason").asOpt[String])
        case _ => None
      }

      val messaging = new ListBuffer[Option[Messaging]]

      if (!messages.isEmpty) {
        messages.get.foldLeft(messaging)( (messaging, z) => messaging += parseMessage(z))
      }


      Logger.debug("This is a entry , id " + entry_id + " time " + entry_time + " messaging total " + messaging.length)
      Entry(entry_id, entry_time, messaging.toList)
  }

  /**
    * Payload of message sent
    *
    */
  val payload = new Payload(FobjectType, entries.toSeq)

  /**
    * parses a message
    *
    * echoes are disgarded
    *
    * @param z JsValue
    * @return
    */
  def parseMessage(z: JsValue) : Option[Messaging] = {
    Logger.debug("MAIS UM " + z)

    val sender_id: String = (z \ "sender" \ "id").as[String]
    val recipient_id : String = (z \ "recipient" \ "id").as[String]
    val timestamp : Long = (z \ "timestamp" ).as[Long]

    Logger.debug("sender _ id " + sender_id + " and recipient _id " + recipient_id + " timestamp " + timestamp)

    val message_text :Option[String] = (z \ "message" \ "text").asOpt[String]
    val is_echo :Boolean = (z \ "message" \ "is_echo").asOpt[Boolean].getOrElse(false)

    if (message_text != None && is_echo.equals(false)) {

      val event = new Event( Message(message_text.get) )

      if (handler.isDefined) {
        Logger.debug("Called handler " + handler.get.getClass.getName + " text for " + sender_id)
        handler.get.handleText(message_text.get, Sender(sender_id))
      }
      Some(Messaging(sender_id, recipient_id, Seq(event)))

    } else if (is_echo.equals(false)) {

      val attachment = (z\"message"\"attachments").asOpt[List[JsValue]]
      val postback = (z \ "postback").asOpt[JsValue]

      val events : Option[Seq[Event]] = if (!attachment.isEmpty) {
        val objAttachment = parseAttachments(attachment.get)
        Some(objAttachment.map {
          attach => {
              if (handler.isDefined) {
                Logger.debug("Called handler " + handler.get.getClass.getName + " attachment for " + sender_id)
                handler.get.handleAttachment(attach, Sender(sender_id))
              }
              new Event(Message("", Some(attach)) )
            }
          }
        )
      } else if (!postback.isEmpty) {
        val postbackobj = Postback((postback.get \ "title").as[String], (postback.get \ "payload").as[String])

        if (handler.isDefined) {
          Logger.debug("Called handler " + handler.get.getClass.getName + " postback for " + sender_id)
          handler.get.handlePostback(postbackobj, Sender(sender_id))
        }

        Some(
          Seq(
            new Event(
              postbackobj
            )
          )
        )
      } else {
        None
      }

      if (!events.isEmpty) {
        Some(Messaging(sender_id, recipient_id, events.get ))
      } else {
        None
      }

    } else {
      None
    }

  }

  def getPayload(): Payload = {
    payload
  }

  def getMessages() : Seq[Message] = {
    payload.getMessages()
  }

  def getAttachments() : Seq[Attachment] = {
    payload.getAttachments()
  }

  def getPostbacks(): Seq[Postback] = {
    payload.getPostbacks()
  }

  /**
    * From a list of attachments received parse them into a object
    *
    * @param attachments
    * @return
    */
  def parseAttachments(attachments: List[JsValue]) : Seq[Attachment] = {
    attachments.map {
      attachment => {
        val a_type = (attachment \ "type").as[String]
        val payload = if (a_type.toLowerCase().equals("image")) {
          val image_url = (attachment \ "payload" \ "url").as[String];
          new AttachmentImagePayload(image_url)
        } else if (a_type.toLowerCase().equals("audio") ) {
          val url = (attachment \ "payload" \ "url").as[String];
          new AttachmentAudioPayload(url)
        } else if (a_type.toLowerCase().equals("video") ) {
          val url = (attachment \ "payload" \ "url").as[String];
          new AttachmentVideoPayload(url)
        } else {
          val url = (attachment \ "payload" \ "url").as[String];
          new AttachmentFilePayload(url)
        }

        new Attachment(a_type, payload)
      }
    }
  }
}

/**
  * https://developers.facebook.com/docs/messenger-platform/reference/webhook-events/messaging_policy_enforcement
  *
  * An app will receive this callback when a policy enforcement action is taken on the page it manages. You can subscribe to this callback by selecting the messaging_policy_enforcement field when setting up your webhook.
  *
  * A policy enforcement will be taken on a page if it does not conform to Messenger Platform policy, fails to meet Facebook community standards or violates Facebook Pages guidelines. Common issues include spams, sending inappropriate messages (porn, suicide, etc), abusing tags, etc.
  *
  * @param action
  * @param reason
  */
case class PolicyEnforcement(action: String, reason: Option[String])

