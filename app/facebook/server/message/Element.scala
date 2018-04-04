package facebook.server.message

import facebook.server.message.actions.{Button, DefaultAction}
import play.api.libs.json.{Json, Writes}

/**
  * Element of a Seq of Attachment Payload
  *
  * Created by andref on 11-07-2017.
  */
case class Element(title: String, imageUrl:String, subTitle: String, defaultAction: DefaultAction, buttons: Seq[Button]) {

}

object Element {

  implicit val jsonElementWrites = new Writes[Element] {

    def writes(elem: Element) = {
      Json.obj(
        "title" -> elem.title,
        "image_url" -> elem.imageUrl,
        "subtitle" -> elem.subTitle,
        "default_action" -> elem.defaultAction,
        "buttons" -> elem.buttons
      )
    }
  }
}