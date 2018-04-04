package facebook

import facebook.server.message.{Attachment, Postback, Sender}

trait MessageHandler {

  /**
    * Handle Text Message
    *
    * @param text
    * @param sender
    */
  def handleText(text: String, sender: Sender) : Unit = {
  }

  /**
    * Handle Postback
    *
    * @param postback
    * @param sender
    */
  def handlePostback(postback: Postback, sender: Sender) : Unit = {
  }

  /**
    * Handle receiving Attachment
    * @param attachment
    * @param sender
    * @return
    */
  def handleAttachment(attachment: Attachment, sender: Sender) : Unit = ???
}
