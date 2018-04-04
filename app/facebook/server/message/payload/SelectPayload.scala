package facebook.server.message.payload

import models.entities.{Glicose}
import play.api.libs.json._

/**
  * private payload object to deal with user selections
  *
  * @param glicose
  */
case class SelectPayload(glicose: Glicose) {

  implicit val convertToJson = Json.format[SelectPayload]

  override def toString: String = Json.toJson(this).toString()

}

object SelectPayload {

  implicit val convertJsonSelectPayload = new Reads[SelectPayload] {
    override def reads(json: JsValue): JsResult[SelectPayload] = {
      val product = (json \ "glicose").as[Glicose]

      JsSuccess(SelectPayload(product))
    }
  }
}