package models.daos

import java.sql.Timestamp

import models.entities.Glicose
import models.persistence.SlickTables.GlicoseTable
import org.joda.time.DateTime
import play.Logger
import slick.lifted
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Glicose data
  *
  * Created by andref on 25-05-2017.
  */
class GlicoseDAO extends BaseDAO[GlicoseTable, Glicose] {

  import dbConfig.profile.api._
  import models.persistence.SlickTables.JodaDateTimeMapper
  import scala.concurrent.ExecutionContext.Implicits.global

  def deleteLastByUserId(userId: Long): Unit = {
    getLastGlicoseByUserProfileId(userId).onComplete{
      case Success(x) => this.deleteById(x.get.id)
      case Failure(x) => Logger.debug("Error found deleteLastByUserId " + x.getMessage)
    }
  }

  def getReportByUserProfileId(userProfileId: Long): Future[Seq[Glicose]] = {
    this.db.run(tableQ.filter(_.userProfileId === userProfileId).result)
  }

  def updateLastMeal(lastmeal: String, userProfileId: Long): Unit = {
    this.getLastGlicoseByUserProfileId(userProfileId).map {
     x => {
       x.map {
         f => this.db.run(tableQ.filter(_.id === f.id).map{_.food}.update(lastmeal))
       }
     }
    }
  }

  def getLastGlicoseByUserProfileId(userProfileId: Long) : Future[Option[Glicose]] = {
    this.db.run(tableQ.filter(_.userProfileId === userProfileId)
      .sortBy(_.datein.desc)
      .take(1)
      .result
      .headOption
    )
  }

  def deleteByUserId(id : Long): Future[Int] = {
    db.run(tableQ.filter(_.userProfileId === id).delete)
  }


  override val tableQ: dbConfig.profile.api.TableQuery[GlicoseTable] = lifted.TableQuery[GlicoseTable]
}
