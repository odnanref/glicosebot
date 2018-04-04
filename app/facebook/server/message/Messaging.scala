package facebook.server.message

/**
  * Message related information
  *
  * Created by andref on 25-05-2017.
  */
case class Messaging(val sender_id: String, val recipient_id: String, val events: Seq[Event]) {

}
