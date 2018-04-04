package service.far

import facebook.ShopConfig
import facebook.server.message.{OutputMessage, Sender}
import play.api.libs.json.{JsResultException, Json}
import service.TExecutionService

import scala.io.Source
import play.Logger

/**
  * Get the current currency value For X/Y
  *
  * Created by andref on 09-07-2017.
  */
class CurrencyService extends TExecutionService {

  implicit val jsonRate = Json.format[Rate]
  /**
    * This will be a runnable method that will take the sent text message
    * and return a String
    *
    * @param text
    * @return
    */
  override def run(text: String, sender: Sender): OutputMessage = {
    if (text.toLowerCase().contains("currency") && text.contains("/") && text.split("/").size == 2) {
      val quotes: Array[String] = text.split("/")
      val firstquote = quotes(0).split(" ")
      val QUOTE1 = firstquote(firstquote.size-1)
      val secondquote = quotes(1).split(" ")
      val QUOTE2 = secondquote(0)

      val res = getCurrency(Some(QUOTE1), Some(QUOTE2))
      new OutputMessage(res)
    } else {
      val res = getCurrency(None, None)
      new OutputMessage("Invalid string provided please use format: \"currency EUR/USD\" \n" + res)
    }
  }

  /**
    * Set the facebook config object for access to page token and other references
    *
    * @param fconfig
    */
  override def setFacebookConfig(fconfig: ShopConfig): Unit = {
    this.fconfig = Some(fconfig)
  }

  /**
    * Fetch the currency value from Yahoo Finantial Services
    *
    * @deprecated Yahoo does not supply this address no more
    *
    * @param QUOTE1
    * @param QUOTE2
    * @return
    */
  def getCurrencyOld(QUOTE1:Option[String], QUOTE2: Option[String]) :String = {
    val http_url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22EURUSD%22%2C%22USDBRL%22%2C%20%22" + QUOTE1.getOrElse("EUR") + QUOTE2.getOrElse("BRL") + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    val Res = Source.fromURL(http_url)
    try {
      val json = Json.parse(Res.mkString)
      val count = (json \ "query" \ "count").as[Long]
      Logger.debug("QUOTE1 " + QUOTE1 + " QUOTE2 "+ QUOTE2 + " count:" + count)
      if (count > 0) {
        val rates = (json \ "query" \ "results" \ "rate" ).as[List[Rate]]
        val RatesResult:List[String] = rates.map{ r => r.Name + " " + r.Rate + "\n"}
        RatesResult.mkString(" ")
      } else {
        "No Result"
      }
    } catch {
      case x:JsResultException => {
        Logger.debug("Error currency parsing " + x.toString() )
        "Error currency parsing."
      }
    }
  }

  /**
    * Fetch the currency value from Yahoo Finantial Services
    * using new yahoo url
    *
    * @param QUOTE1
    * @param QUOTE2
    * @return
    */
  def getCurrency(QUOTE1: Option[String], QUOTE2: Option[String]) : String = {
    val http_url = "http://download.finance.yahoo.com/d/quotes.csv?s=" + QUOTE1.getOrElse("EUR") + QUOTE2.getOrElse("BRL") + "=X&f=l1&e=.csv"
    val Res = Source.fromURL(http_url)
    QUOTE1.getOrElse("EUR") + "/" + QUOTE2.getOrElse("BRL") + " " + Res.mkString
  }
}

/**
  * Rate of a currency X/Y
  *
  * @param Name
  * @param Rate
  */
case class Rate(Name:String, Rate:String)
