package facebook.server.message

/**
  * Is placed inside a Message Event
  *
  * Created by andref on 27-05-2017.
  */
case class Message(val text:String, Attachment: Option[Attachment] = None) {

}
