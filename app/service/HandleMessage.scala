package service

import javax.inject.Inject
import facebook._
import facebook.server.message._
import facebook.server.message.payload.SelectPayload
import models.daos.{CommandsDAO, GlicoseDAO, UserProfileDAO}
import models.entities.Execution
import play.Logger
import play.api.libs.json.Json
import provider.RedisClientProvider
import service.client.RedisClient

import scala.concurrent.ExecutionContext

/**
  * Handles message received with payload
  *
  * Created by andref on 30-05-2017.
  */
class HandleMessage @Inject()(configuration: play.api.Configuration,
                              commandsDAO: CommandsDAO,
                              userProfileDao: UserProfileDAO,
                              glicoseDao: GlicoseDAO,
                              fconfig: ShopConfig)
                             (implicit ex : ExecutionContext) extends MessageHandler {

  val mensagemAjuda =
    """<valor numérico> - Digite o número ex.(76) da glicose para gravar.
      |relatorio ou relatório - para baixar os valores gravados.
      |"apagar tudo"- apagar todos os dados relacionados ao usuário (utilizador).
      |apagar - Apaga o último registo introduzido.
      |ajuda - para ver esta mensagem.""".stripMargin

  private var redis : Option[RedisClient] = None

  def setRedisClient(redis: RedisClient):Unit = {
    this.redis = Some(redis)
  }

  def apagartudo(sender: Sender): Unit = {
    val gs = new GlicoseService(glicoseDao, fconfig)
    gs.removeUser(sender.id.toLong)
  }

  def apagarUltimo(sender: Sender) : Unit = {
    val gs = new GlicoseService(glicoseDao, fconfig)
    gs.deleteLast(sender)
  }

  def getUser(sender: Sender) :UserProfile = {
    val rcp = new RedisClientProvider(configuration)
    val uis = new UserInteractionService(userProfileDao, rcp.get(), this.fconfig)
    Logger.debug("fetching user " + sender.id)
    val user :UserProfile = uis.getUserInfo(sender.id.toString)
    Logger.debug("Got user " + user)
    user
  }

  def sendRelatorio(sender: Sender): Unit = {
    Logger.debug("Sending report to " + sender.id)

    val user = getUser(sender)
    val gs = new GlicoseService(glicoseDao, fconfig)
    gs.sendReport(user)
  }

  def saveMeal(x: String, sender: Sender): Unit = {
    val comi = "comi"
    val lastmeal = x.substring(x.indexOf(comi)+comi.length).trim
    Logger.debug("Saving the meal for " + sender + " meal was " + lastmeal)
    val gs = new GlicoseService(glicoseDao, fconfig)
    gs.saveLastMeal(lastmeal, sender.id.toLong)
  }

  def saveGlicose(x: String, sender: Sender): Unit = {
    Logger.debug("Save glicose value of " + x + " from sender " + sender.id)

    val user = this.getUser(sender)
    val gs = new GlicoseService(glicoseDao, fconfig)
    gs.saveGlicose(x, user)
  }

  /**
    * Get information about the user connecting
    *
    * @param sender_id
    */
  def handleUserProfile(sender_id: String) : Unit = {

    val rcp = new RedisClientProvider(configuration)
    val uis = new UserInteractionService(userProfileDao, rcp.get(), this.fconfig)
    uis.handleUserProfile(sender_id)
  }

  /**
    * Call's remote API for product search related to the text
    *
    * @param text
    * @param sender
    */
  def handleRemoteSearch(text: String, sender: Sender): Unit = {
    Logger.debug("Not Doing remote search... " + text)
  }

  /**
    * Handle Text Message
    *
    * @param text
    * @param sender
    */
  override def handleText(text: String, sender: Sender) : Unit = {
    val commandDiscovery = new CommandDiscoveryService(commandsDAO, text)
    val execution = commandDiscovery.getExecution().map{
      execution => {
        if (execution.isEmpty) {
          val digit = "(\\d+)".r // compare to only numbers in the text
          text match {
            case digit(x) => this.saveGlicose(x, sender);
            case x if (x.toLowerCase.contains("comi")) => this.saveMeal(text, sender);
            case x if (x.toLowerCase.contains("relatório") || x.toLowerCase.contains("relatorio")) => this.sendRelatorio(sender)
            case x if (x.toLowerCase().contains("ajuda")) => {
              val fbr = new FacebookRequest(this.fconfig)
              fbr.sendMessage(new JsonMessage(FacebookRecipient(sender.id.toLong), mensagemAjuda))
            }
            case x if (x.toLowerCase().contains("apagar tudo")) => {
              this.apagartudo(sender)
            }
            case x if (x.toLowerCase().equals("apagar")) => {
              this.apagarUltimo(sender)
            }
            case _ => {
              Logger.debug("No option found for " + text)
              val fbr = new FacebookRequest(this.fconfig)
              fbr.sendMessage(new JsonMessage(FacebookRecipient(sender.id.toLong),
                "Olá, não compreendi o que pretende.\nEscreva ajuda para ver em como o posso servir.")
              )
            }
          }
          //handleRemoteSearch(text, sender) // Remote API Server handles text parsing and such
        } else {
          handleExecution(execution.get, text, sender)
        }
      }
    }
  }

  /**
    * Handle Execution returned from database
    *
    * @param ex
    * @param text
    * @param sender
    */
  def handleExecution(ex: Execution, text: String, sender: Sender): Unit = {
    Logger.debug("Going into execution " + ex)
    if (ex.execution.split(":").size == 3) {
      val cname = ex.execution.split(":")(1)
      val fname = ex.execution.split(":")(2)

      try {
        val theclass = Class.forName(cname).newInstance().asInstanceOf[TExecutionService]

        if (theclass.isInstanceOf[TExecutionService]) {
          theclass.setFacebookConfig(this.fconfig) // passing configuration to send requests
          val output = theclass.run(text, sender)
          Logger.debug("Output from " + cname + ".run " + text + " -- " + output)
          handleOutput(output, sender)
        }
      } catch {
        case exception: Throwable => {
          Logger.debug("Error in HandleExecution " + ex + " with exception " + exception.getMessage)
          throw exception
        }
      }
    }
  }

  /**
    * Handle Output returned from Execution
    *
    * @param output
    * @param sender gets converted to recipient
    */
  def handleOutput(output: OutputMessage, sender: Sender, attachment: Option[Attachment] = None) : Unit = {
    Logger.debug("handling output message " + " -- " + output)
    val msend = new MessageSend(output, sender, attachment, this.fconfig)
    msend.send()
  }

  override def handlePostback(postback: Postback, sender: Sender) = {
    if (postback.payload.equals("VIEW_MORE")) {
      // rapi.handleViewMore()

    } else if (postback.payload.equals("REPORT")) {
      this.sendRelatorio(sender)
    } else if (postback.payload.equals("AJUDA")) {
      val fbr = new FacebookRequest(this.fconfig)
      fbr.sendMessage(new JsonMessage(FacebookRecipient(sender.id.toLong), mensagemAjuda))
    } else if (postback.payload.equals("GET_STARTED")) {

      val fbr = new FacebookRequest(this.fconfig)
      fbr.sendMessage(new JsonMessage(FacebookRecipient(sender.id.toLong), mensagemAjuda))

    } else if (postback.payload.startsWith("facebook.server.message.payload.SelectPayload:")) {
      val jsonstr = postback.payload.replace("facebook.server.message.payload.SelectPayload:", "")
      val selected = Json.parse(jsonstr).as[SelectPayload]
      if (this.redis.isEmpty) {
        Logger.debug("Redis not configured. handlePostback SelectPayload present.")
      } else {
      }

    }
  }

  override def handleAttachment(attachment: Attachment, sender: Sender) = ???

}