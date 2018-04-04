package service

import facebook.{FacebookRecipient, FacebookRequest, JsonMessage, ShopConfig}
import facebook.server.message.{Attachment, OutputMessage, Sender}
import play.Logger

/**
  * Send Message to Facebook
  *
  * @param output
  * @param sender
  * @param attachment
  * @param shopConfig
  */
class MessageSend(output: OutputMessage, sender: Sender, attachment: Option[Attachment] = None, shopConfig: ShopConfig) {

  /**
    * send based on constructor parameters
    *
    */
  def send() : Unit = {
    val fbr = new FacebookRequest(this.shopConfig)
    Logger.debug("sending message " + " -- " + output)
    fbr.sendMessage(new JsonMessage(FacebookRecipient(sender.id.toLong), output.text, attachment))
  }

}
