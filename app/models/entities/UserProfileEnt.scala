package models.entities

case class UserProfileEnt(id: Long, first_name: String, last_name: String,
                          profile_pic: String, // profile picture url
                          locale: String,
                          timezone: Int,
                          gender: String,
                          gid: Long) extends BaseEntity

object UserProfileEnt {
  implicit def userProfileToMap(u : UserProfileEnt) : Map[String,String] = {
    Map("id" -> u.id.toString, "first_name" -> u.first_name, "last_name" -> u.last_name,
      "profile_pic" -> u.profile_pic, "locale" -> u.locale,
      "timezone" -> u.timezone.toString, "gender" -> u.gender,
      "gid" -> u.gid.toString
    )
  }

  implicit def mapToUserProfile(x: Map[String, String]) : UserProfileEnt = {
    UserProfileEnt(x("id").toLong,
      x("first_name"),x("last_name"),
      x("profile_pic"), x("locale"), x("timezone").toInt, x("gender"),
      x("gid").toLong
    )
  }

  def tupled = (this.apply _).tupled

}
