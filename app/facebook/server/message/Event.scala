package facebook.server.message

/**
  * Created by andref on 27-05-2017.
  */
class Event(var message: Option[Message] = None, var postback: Option[Postback] = None) {

  def this(message: Message) = this(Some(message), None)

  def this(postback: Postback) = this(None, Some(postback))

}
