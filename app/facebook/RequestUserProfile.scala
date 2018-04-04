package facebook

import facebook.ShopConfig
import facebook.server.message.{Sender, UserProfile}
import play.Logger
import play.api.libs.json.{JsResultException, Json}

import scalaj.http.Http

/**
  * Get Sender ID Profile information
  *
  * Created by andref on 26-06-2017.
  */
class RequestUserProfile(sender: Sender, fconfig: ShopConfig) {

  val url:String = "https://graph.facebook.com/v2.6/" + sender.id +
    "?fields=first_name,last_name,profile_pic,locale,timezone,gender&access_token=" +
    fconfig.PAGE_ACCESS_TOKEN

  implicit val userProfileRead = Json.reads[UserProfile]

  def getUserProfile() :UserProfile = {
    try {
      Json.parse(request()).as[UserProfile]
    } catch {
      case error: JsResultException => {
        Logger.debug("Error JsResultException getUserProfile " + error)
        throw error
      }
    }
  }

  def request() :String = {
    if (fconfig.PAGE_ACCESS_TOKEN == null) {
      Logger.warn("No access token provided.")
      ""
    } else {
      val answer = new StringBuffer()
      val result = Http(this.url) // call facebook user profile messenger page
        .header("Content-Type", "application/json")
        .header("Charset", "UTF-8")
        .execute(is => {
          scala.io.Source.fromInputStream(is).getLines().foreach( msg =>
            {
              answer.append(msg.toString)
              Logger.debug("debug from input string " + msg.toString() )
            }
          )
        })
      Logger.debug("UserProfile answer " + answer)
      answer.toString // return the answer
    }
  }
}
