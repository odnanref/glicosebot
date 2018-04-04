package service

import facebook.ShopConfig
import facebook.server.message.actions.{Button, ButtonPayload, ButtonWebUrl, DefaultAction}
import facebook.server.message.payload.SelectPayload
import facebook.server.message.{Attachment, AttachmentPayload, Element}
import models.entities
import play.Logger
import service.Exception.NoShopConfigException

import scala.collection.mutable.ListBuffer

/**
  * Generate an Attachment for a facebook message
  *
  * @author Fernando Andre
  */
class GenerateAttachment(attachmentType:String = "template") {
  /**
    * Shopconfig Variable used to access shop specific details
    *
    */
  var shopconfig: Option[ShopConfig] = None
/*
  val attachment = new Attachment(attachmentType, new AttachmentPayload("list"))
  attachment.payload.elements = Some(new ListBuffer[Element]())

  def attach(prod: entities.Product): Unit = {
    if (this.shopconfig.isEmpty) {
      Logger.debug("No ShopConfig Variable set, please define variables to use.")
      throw new NoShopConfigException()
    }
    val actionUrl = this.shopconfig.get.botProductViewHook.get.replaceAll("<ID>", prod.id.toString)
    val fallbackUrl = shopconfig.get.fallBackUrl.get.replaceAll("<ID>", prod.id.toString)
    val defaultAction = new DefaultAction("web_url", actionUrl, true, fallbackUrl)
    val btnselect = ButtonPayload("Escolher", "facebook.server.message.payload.SelectPayload:" + SelectPayload(prod).toString)

    /*
    val button = new ButtonWebUrl(
      "Buy Now",
      this.shopconfig.get.botProductBuyHook.get.replaceAll("<ID>", prod.id.toString),
      this.shopconfig.get.fallBackUrl.get.replaceAll("<ID>", prod.id.toString)
    )
    */
    this.attachment.payload.elements.get += new Element(prod.name, prod.image, prod.code + " " + prod.price, defaultAction, Seq(btnselect))
  }
*/
  def setShopConfig(shopConfig: ShopConfig) = {
    this.shopconfig = Some(shopConfig)
  }

}
