package service.far

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Calendar

import facebook.RequestUserProfile
import facebook.ShopConfig
import facebook.server.message.{OutputMessage, Sender}
import service.TExecutionService

/**
  * Created by andref on 20-06-2017.
  */
class TimeService extends TExecutionService {

  /**
    * This will be a runnable method that will take the sent text message
    * and return a String
    *
    * @param text
    * @return
    */
  override def run(text: String, sender:Sender): OutputMessage = {

    if (this.fconfig.isEmpty) {
      val localtime = getStandardTime()
      new OutputMessage("The local time is " + localtime )
    } else {
      val request = new RequestUserProfile(sender, fconfig.get)
      val uprofile = request.getUserProfile()
      val localtime = this.getTimezoneTime(uprofile.timezone)
      // set the answer
      new OutputMessage("The local time for timezone GMT " + uprofile.timezone + " is " + localtime )
    }
  }

  def getStandardTime() :String = {
    val format = new SimpleDateFormat("d-M-y H:m:s")
    format.format(Calendar.getInstance().getTime())
  }

  def getTimezoneTime(timezone:Int) :String = {
    val offset_time = ZoneOffset.ofHours(timezone)
    val ltime = ZonedDateTime.now(offset_time).toLocalDateTime
    ltime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
  }

  /**
    * Set the facebook config object for access to page token and other references
    *
    * @param fconfig
    */
  override def setFacebookConfig(fconfig: ShopConfig): Unit = {
    this.fconfig = Some(fconfig)
  }
}
