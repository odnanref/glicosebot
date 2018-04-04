package facebook.server.message

/**
  * First prototype of a output object that will be used to send the code message to the
  * recipient
  *
  * Created by andref on 20-06-2017.
  */
class OutputMessage(val text: String) {

  override def toString : String = {
    text.toString()
  }
}
