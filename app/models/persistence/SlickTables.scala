package models.persistence

import java.sql.Timestamp

import models.entities.{Command, Execution, Glicose, UserProfileEnt}
import org.joda.time.DateTime
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.profile.api._

  abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  }

  class CommandsTable(tag: Tag) extends BaseTable[Command](tag, "commands") {
    def name = column[String]("name")
    def desc = column[String]("description")
    def execution = column[Long]("execution_id")
    def * = (id, name, desc, execution) <> (Command.tupled, Command.unapply)
  }

  val commandsTableQ : TableQuery[CommandsTable] = TableQuery[CommandsTable]

  class ExecutionsTable(tag: Tag) extends BaseTable[Execution](tag, "executions") {
    def name = column[String]("name")
    def execution = column[String]("execution")
    def * = (id, name, execution) <> (Execution.tupled, Execution.unapply)
  }

  val executionsTableQ : TableQuery[ExecutionsTable] = TableQuery[ExecutionsTable]

  class GlicoseTable(tag: Tag) extends BaseTable[Glicose](tag, "glicose") {
    def inputed = column[String]("inputed")
    def datein = column[DateTime]("datein")
    def userProfileId = column[Long]("user_profile_id")
    def food = column[String]("food")

    def * = (id, inputed, datein, userProfileId, food) <> ((Glicose.apply _).tupled, Glicose.unapply)
  }

  val GlicoseTableQ : TableQuery[GlicoseTable] = TableQuery[GlicoseTable]

  class UserProfileTable(tag: Tag) extends Table[UserProfileEnt](tag, "user_profile") {
    def id = column[Long]("id")
    def first_name = column[String]("first_name")
    def last_name = column[String]("last_name")
    def profile_pic = column[String]("profile_pic")
    def locale = column[String]("locale")
    def timezone = column[Int]("timezone")
    def gender = column[String]("gender")
    def gid = column[Long]("gid", O.PrimaryKey, O.AutoInc)

    def * = (id, first_name, last_name, profile_pic, locale, timezone, gender, gid) <> (UserProfileEnt.tupled, UserProfileEnt.unapply)
  }

  val userProfileTableQ : TableQuery[UserProfileTable] = TableQuery[UserProfileTable]

  implicit val JodaDateTimeMapper = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime())
  )
}
