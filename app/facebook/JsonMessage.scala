package facebook

import facebook.server.message.Attachment

/**
  * Message to be used has a reply to facebook
  *
  * Created by andref on 25-05-2017.
  */
class JsonMessage(val recipient: FacebookRecipient , val message: String, val attachment: Option[Attachment] = None) {

}
