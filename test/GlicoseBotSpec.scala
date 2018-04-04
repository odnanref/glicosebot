
import java.io.File

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import facebook._
import facebook.server.message.UserProfile
import models.daos.{CommandsDAO, GlicoseDAO, UserProfileDAO}
import models.entities.Glicose
import org.joda.time.{DateTime, LocalTime}
import play.api.libs.json.{JsValue, Json}
import provider.RedisClientProvider
import service.{GlicoseService, HandleMessage}

import scala.util.{Failure, Success}

class GlicoseBotSpec
    extends PlaySpec with GuiceOneAppPerSuite  {

  import scala.concurrent.ExecutionContext.Implicits.global

  val commandsDAO = new CommandsDAO()
  val userProfileDao = new UserProfileDAO()
  val glicoseDao = new GlicoseDAO()

  val shopconfig = new ShopConfig(
    1, // storeId
    System.getenv("PAGE_ACCESS_TOKEN")
  )
  // Facebook user id
  val userId = 1664928253589100L//1159527677474031L;

  "Glicose bot" should {

    "on receiving a number" in {
      val msg = s"""{"object":"page","entry":[{"id":"779844352055452","time":1504198411030,"messaging":[{"sender":{"id":"$userId"},"recipient":{"id":"779844352077452"},"timestamp":1504198410786,"message":{"mid":"mid.$$cAAKfsLZjhfFkanuKIleOTZBn6D-d","seq":119234,"text":"76"}}]}]}"""
      val jsonMsg = Json.parse(msg)
      val config = play.api.Configuration.load(play.api.Environment.simple())

      val handler = new HandleMessage(
        config,
        commandsDAO,
        userProfileDao,
        glicoseDao,
        shopconfig
      )
      handler.setRedisClient(new RedisClientProvider(config).get())

      val fparser = new FBodyParser(jsonMsg, Some(handler))

      Thread.sleep(2000)

      glicoseDao.getLastGlicoseByUserProfileId(userId).onComplete{
        case Success(x) => assert(x.size > 0)
        case Failure(x) => {
          assert(false)
        }
      }
    }

    "on receiving >>comi<<" in {
      val msg = s"""{"object":"page","entry":[{"id":"779844352055452","time":1504198411030,"messaging":[{"sender":{"id":"$userId"},"recipient":{"id":"779844352077452"},"timestamp":1504198410786,"message":{"mid":"mid.$$cAAKfsLZjhfFkanuKIleOTZBn6D-d","seq":119234,"text":"comi salada com alface , bife de frango , arroz e feijão"}}]}]}"""
      val jsonMsg = Json.parse(msg)
      val config = play.api.Configuration.load(play.api.Environment.simple())

      val handler = new HandleMessage(
        config,
        commandsDAO,
        userProfileDao,
        glicoseDao,
        shopconfig
      )
      handler.setRedisClient(new RedisClientProvider(config).get())
      val fparser = new FBodyParser(jsonMsg, Some(handler))

      Thread.sleep(2000)

      glicoseDao.getLastGlicoseByUserProfileId(userId).onComplete{
        case Success(x) => assert(!x.get.food.isEmpty, "Food was found")
        case Failure(x) => assert(false, "Failed finding food " + x.getMessage )
      }
    }

    "on receiving relatorio" in {
      val msg = s"""{"object":"page","entry":[{"id":"779844352055452","time":1504198411030,"messaging":[{"sender":{"id":"$userId"},"recipient":{"id":"779844352077452"},"timestamp":1504198410786,"message":{"mid":"mid.$$cAAKfsLZjhfFkanuKIleOTZBn6D-d","seq":119234,"text":"relatorio"}}]}]}"""
      val jsonMsg = Json.parse(msg)

      val config = play.api.Configuration.load(play.api.Environment.simple())

      val handler = new HandleMessage(
        config,
        commandsDAO,
        userProfileDao,
        glicoseDao,
        shopconfig
      )
      handler.setRedisClient(new RedisClientProvider(config).get())
      val fparser = new FBodyParser(jsonMsg, Some(handler))

    }

    "write report" in {
      val gs = new GlicoseService(glicoseDao, shopconfig)
      val userProfile = UserProfile(userId.toString, "Fernando", "André", "image.jpeg","pt_PT", -3, "male")

      glicoseDao.getReportByUserProfileId(userId).map {
        f => {
          //Build the report
          gs.buildReport(f, userProfile)
        }
      }.onComplete{
        case Success(x) => {
          println(x)
          val property = "java.io.tmpdir"
          val tempDir = System.getProperty(property) + "/glicosereports"

          val filepath = tempDir + File.separator + "glicose_" + userProfile.id + ".html"
          val fp = new File(filepath)

          println("Writing to " + filepath)

          assert(fp.exists())
          assert(new File(filepath.replace(".html", ".pdf")).exists())
          fp.delete()
          //new File(filepath.replace(".html", ".pdf")).delete()
        }
        case Failure(x) => {
          println(">>>> FAILURE " + x.getMessage)
          assert(false)
        }
      }
      Thread.sleep(2000)
    }

    "deleting the last inserted" in {

      val config = play.api.Configuration.load(play.api.Environment.simple())

      glicoseDao.insert(Glicose(0, "1226", new DateTime(), userId))

      val msg2 = s"""{"object":"page","entry":[{"id":"779844352055452","time":1504198411030,"messaging":[{"sender":{"id":"$userId"},"recipient":{"id":"779844352077452"},"timestamp":1504198410786,"message":{"mid":"mid.$$cAAKfsLZjhfFkanuKIleOTZBn6D-d","seq":119234,"text":"apagar"}}]}]}"""
      val jsonMsg2 = Json.parse(msg2)

      val handler2 = new HandleMessage(
        config,
        commandsDAO,
        userProfileDao,
        glicoseDao,
        shopconfig
      )
      handler2.setRedisClient(new RedisClientProvider(config).get())
      val fparser2 = new FBodyParser(jsonMsg2, Some(handler2))
      Thread.sleep(2000)

      glicoseDao.getLastGlicoseByUserProfileId(userId).onComplete{
        case Success(x) => assert(x.get.inputed.equals("76"), "last value really was " + x.get.inputed)
        case Failure(x) => println("ERROR FAILED DELETING LAST " + x.getMessage)
      }
    }

  }

  "Specific Unit test of functions" should {
    "test the groupBy of a report" in {
      val gs = new GlicoseService(glicoseDao, shopconfig)
      glicoseDao.getReportByUserProfileId(userId).map {
        f => {
          val r = gs.getReportByDay(f)
          println(r)// If it doesn't throw exceptions I'm happy :\
        }
      }
    }
  }
}
