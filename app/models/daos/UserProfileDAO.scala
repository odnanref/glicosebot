package models.daos

import com.google.inject.Inject
import models.entities.UserProfileEnt
import models.persistence.SlickTables.UserProfileTable
import play.Logger
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.GetResult
import slick.lifted.CanBeQueryCondition

import scala.concurrent.{ExecutionContext, Future}

/**
  * words related commands that execute actions
  *
  * Created by andref on 25-05-2017.
  */
class UserProfileDAO @Inject()(implicit ec: ExecutionContext) extends AbstractBaseDAO[UserProfileTable,UserProfileEnt] with HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import dbConfig.profile.api._

  def insert(row: UserProfileEnt): Future[Long] = {
    this.insert(Seq(row))
    Future{
      0
    }
  }

  override def deleteByFilter[C: CanBeQueryCondition](f: (UserProfileTable) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

  override def update(row: UserProfileEnt): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future{0}
  }

  override def update(rows: Seq[UserProfileEnt]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  def insert(rows: Seq[UserProfileEnt]): Future[Seq[Long]] = {
    db.run(tableQ ++= rows.filter(_.isValid))
    Future{Seq(0)}
  }

  override def findById(id: Long): Future[Option[UserProfileEnt]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  override def findByFilter[C: CanBeQueryCondition](f: (UserProfileTable) => C): Future[Seq[UserProfileEnt]] = {
    db.run(tableQ.withFilter(f).result)
  }

  override def deleteById(id: Long): Future[Int] = {
    deleteById(Seq(id))
  }

  override def deleteById(ids: Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  val tableQ: dbConfig.profile.api.TableQuery[UserProfileTable] = TableQuery[UserProfileTable]
}
