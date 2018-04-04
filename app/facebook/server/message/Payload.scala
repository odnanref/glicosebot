package facebook.server.message

import scala.collection.mutable.ListBuffer

/**
  * Facebook message entries payload
  *
  * Created by andref on 25-05-2017.
  */
class Payload (facebookObject: FacebookObject, entries: Seq[Entry]) {

  def getMessages() : Seq[Message] = {
    for ( ent <- entries;
          msg <- ent.messaging;
          event <- msg.get.events if !msg.isEmpty && !event.message.isEmpty)
      yield event.message.get
  }

  private var attachments: ListBuffer[Attachment] = new ListBuffer[Attachment]

  def getAttachments() : Seq[Attachment] = {
    for ( ent <- entries;
          msg <- ent.messaging;
          event <- msg.get.events if (!msg.isEmpty && !event.message.isEmpty ))
      yield event.message.get.Attachment.get
  }

  private var postbacks : ListBuffer[Postback] = new ListBuffer[Postback]

  def getPostbacks(): Seq[Postback] = {
    for ( ent <- entries;
          msg <- ent.messaging;
          event <- msg.get.events if !msg.isEmpty && !event.postback.isEmpty)
      yield event.postback.get
  }
}
