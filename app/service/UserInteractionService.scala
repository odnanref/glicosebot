package service

import javax.inject.Inject
import facebook._
import facebook.server.message.{Sender, UserProfile}
import models.daos.UserProfileDAO
import models.entities.UserProfileEnt
import play.Logger
import service.client.RedisClient

import scala.util.{Failure, Success}

class UserInteractionService @Inject() (val userProfileDAO: UserProfileDAO, redis: RedisClient, shopConfig: ShopConfig) {

  import scala.concurrent.ExecutionContext.Implicits.global

  val USER_INFORMATION = "user_information_"

  def handleUserProfile(sender_id: String) : Unit = { // TODO do this
    Logger.debug("Getting information about user: " + sender_id)
    val res = redis.get(USER_INFORMATION + sender_id)

    if (res.isEmpty) {
      val futureUser = this.userProfileDAO.findById(sender_id.toLong).map {
        user_from_db =>
          if (user_from_db.isEmpty) {
            Logger.debug("None found " + user_from_db.getOrElse("NONE FOUND IN DB"))
            val ruq = new RequestUserProfile(Sender(sender_id), this.shopConfig);
            val userProfile = ruq.getUserProfile()

            Logger.debug("User Profile fetched " + userProfile)

            val u: UserProfileEnt = UserProfile.mapToEntity(userProfile) // safe transform

            this.userProfileDAO.insert(u).onComplete {
              case Success(x) => Logger.debug("Success inserting user: " + x)
              case Failure(x) => Logger.debug("Failure inserting user: " + x.printStackTrace())
            } // inserting new user

            redis.set(USER_INFORMATION + sender_id, u) // setting to redis memory
            this.handleNewUser(u) // handling onboarding event

          } else {
            val x = user_from_db.get
            Logger.debug("User already existed in database " + x)
            redis.set(USER_INFORMATION + sender_id, x) // existing user setting to redis memory
          }
      }

      futureUser.onComplete{
        case Success(x) => Logger.debug("Success " + x )
        case Failure(x) => Logger.debug("Failure " + x.printStackTrace() )
      }

    } else {
      Logger.debug("User exists in Memory database " + res )
      Some(res)
    }
  }

  /**
    * This will handle the boarding of the new user according to the shop specifics
    *
    * @param ent
    * @return
    */
  def handleNewUser(ent: UserProfileEnt) = {
    val fr = new FacebookRequest(this.shopConfig)// FIXME This is a test, will be dynamic soon
    fr.sendMessage(new JsonMessage(new FacebookRecipient(ent.id), "Bem vindo " + ent.first_name))
  }

  def getUserInfo(sender_id: String) = {
    Logger.debug("Getting " + USER_INFORMATION + sender_id)
    val r = redis.get(USER_INFORMATION + sender_id) // information when discovered is always set to redis.
    if (r.isEmpty) {
      throw new Exception("No sender located in redis by that string")
    } else {
      r
    }
  }
}
