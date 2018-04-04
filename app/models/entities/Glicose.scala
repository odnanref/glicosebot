package models.entities

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by andref on 25-05-2017.
  */
case class Glicose (id: Long, inputed: String, datein: DateTime, userProfileId: Long, food: String = "")
  extends BaseEntity

object Glicose {

  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")

  implicit val dateTimeJsReader = JodaReads.jodaDateReads("yyyyMMddHHmmss")

  implicit val jsonGlicose = Json.format[Glicose]

}
