package service

import facebook.ShopConfig
import facebook.server.message.{OutputMessage, Sender}

/**
  * Defines what an execution service should have
  *
  * Created by andref on 20-06-2017.
  */
trait TExecutionService {

  var fconfig : Option[ShopConfig] = None

  /**
    * This will be a runnable method that will take the sent text message
    * and return a String
    *
    * @param text
    * @return
    */
  def run(text:String, sender: Sender) : OutputMessage

  /**
    * Set the facebook config object for access to page token and other references
    *
    * @param fconfig
    */
  def setFacebookConfig(fconfig: ShopConfig): Unit
}
