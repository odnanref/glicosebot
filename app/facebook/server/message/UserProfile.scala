package facebook.server.message

import models.entities.UserProfileEnt

/**
  * User profile returned from Facebook
  * and passed to this object
  *
  * Created by andref on 26-06-2017.
  */
case class UserProfile(id: String,
                       first_name: String,
                       last_name: String,
                       profile_pic: String, // profile picture url
                       locale: String,
                       timezone: Int,
                       gender: String,
                       gid : Option[Long] = None
                      ) {

}

object UserProfile {
  implicit def userProfileToMap(u : UserProfile) : Map[String,String] = {
    Map("first_name" -> u.first_name, "last_name" -> u.last_name, "profile_pic" -> u.profile_pic, "locale" -> u.locale,
      "timezone" -> u.timezone.toString, "gender" -> u.gender)
  }

  implicit def mapToUserProfile(x: Map[String, String]) : UserProfile = {
    UserProfile(x("id"), x("first_name"),x("last_name"), x("profile_pic"), x("locale"), x("timezone").toInt, x("gender"))
  }

  implicit def mapToEntity(user: UserProfile ) : UserProfileEnt = {
    UserProfileEnt(user.id.toLong, user.first_name, user.last_name, user.profile_pic, user.locale, user.timezone, user.gender, 0)
  }

  implicit def entityToUserProfile(user: UserProfileEnt) : UserProfile = {
    UserProfile(user.id.toString(), user.first_name, user.last_name, user.profile_pic, user.locale, user.timezone, user.gender, Some(user.gid))
  }
}
