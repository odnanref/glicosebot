package controllers

import java.io.File

import akka.actor.ActorSystem
import facebook.{FBodyParser, ShopConfig}
import facebook.ShopConfig
import javax.inject._
import models.daos.{CommandsDAO, GlicoseDAO, UserProfileDAO}
import play.api.{Configuration, Logger}
import play.api.mvc.{Action, _}
import provider.RedisClientProvider

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}


@Singleton
class BotController @Inject()(configuration: Configuration,
                              commandsDAO : CommandsDAO,
                              userProfileDao: UserProfileDAO,
                              glicoseDao: GlicoseDAO,
                              cc: ControllerComponents,
                              actorSystem: ActorSystem)
                             (implicit exec: ExecutionContext)
  extends AbstractController(cc) {

  def BotHook = Action.async(parse.json) {
    request => {
      Logger.debug("before message parsing:")

      Future {
        val fconfig = new ShopConfig(
          1, // storeId
          System.getenv("PAGE_ACCESS_TOKEN")
        )
        val shm = new service.HandleMessage(configuration,
          commandsDAO, userProfileDao, glicoseDao, fconfig)
        shm.setRedisClient(new RedisClientProvider(configuration).get())
        val fbp = new FBodyParser(request.body, Some(shm))
      }
      Logger.debug("after message parsing")
    }
      Future(Ok("net.crash"))
  }

  def getBotHook = Action { request =>

    val verify_token = request.getQueryString("hub.verify_token").getOrElse("FAIL")
    val hub_challenge = request.getQueryString("hub.challenge").getOrElse("FAILED CHALLENGE")

    if (!verify_token.contentEquals(System.getenv("TOKEN_STRING"))) {
      Ok("Invalid Verify token")
    } else {
      Ok(hub_challenge)
    }
  }

  def getAttachment(file:String) = Action {
    val property = "java.io.tmpdir"
    val tempDir = System.getProperty(property) + "/glicosereports"

    val source = new File(tempDir + file)
    if (source.exists()) {
      Ok.sendFile(source)
    } else {
      NotFound
    }
  }

  def getPrivacyPolicy = Action {
    Ok(views.html.privacypolicy())
  }

  def getTermsOfService = Action {
    Ok(views.html.termsofservice())
  }

}
