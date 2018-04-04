package models.daos

import models.entities.{Command, Execution}
import models.persistence.SlickTables.{CommandsTable}
import slick.lifted.CanBeQueryCondition
import slick.driver.H2Driver.api._
import slick.jdbc.GetResult

import scala.concurrent.Future

/**
  * words related commands that execute actions
  *
  * Created by andref on 25-05-2017.
  */
class CommandsDAO extends BaseDAO[CommandsTable, Command]  {

  override def insert(row: Command): Future[Long] = {
    super.insert(row)
  }

  override def deleteByFilter[C: CanBeQueryCondition](f: (CommandsTable) => C): Future[Int] = {
    super.deleteByFilter[C](f)
  }

  override def update(row: Command): Future[Int] = {
    super.update(row)
  }

  override def update(rows: Seq[Command]): Future[Unit] = {
    super.update(rows)
  }

  override def insert(rows: Seq[Command]): Future[Seq[Long]] = {
    super.insert(rows)
  }

  override def findById(id: Long): Future[Option[Command]] = {
    super.findById(id)
    //db.run(tableQ.filter(_.id === id).result.headOption)
  }

  override def findByFilter[C: CanBeQueryCondition](f: (CommandsTable) => C): Future[Seq[Command]] = {
    super.findByFilter[C](f)
  }

  override def deleteById(id: Long): Future[Int] = {
    super.deleteById(id)
  }

  override def deleteById(ids: Seq[Long]): Future[Int] = {
    super.deleteById(ids)
  }

  def findByName(name:String) :Future[Seq[Command]] = {
    this.findByFilter(_.name == name)
  }

  implicit val getExecutionResult = GetResult(r => Execution(r.<<, r.<<, r.<<))

  def findExecutionByName(name:String) : Future[Seq[Execution]] = {
    db.run(
    sql"""SELECT ex.id, ex.name, ex.execution FROM executions ex
          INNER JOIN commands cmds ON (cmds.execution_id = ex.id )
          WHERE MATCH(cmds.name) AGAINST($name)"""
        .as[Execution]
    )
  }

  def findExecutionByNames(names: List[String]) : Future[Seq[Execution]] = {
    val tmp1 = names.map(  _ + "|" ).mkString
    val tmp = tmp1.substring(0, tmp1.length-1)
    db.run(
      sql"""SELECT ex.id, ex.name, ex.execution FROM executions ex
          INNER JOIN commands cmds ON (cmds.execution_id = ex.id )
          WHERE MATCH(ex.name) AGAINST($tmp) """
        .as[Execution]
    )
  }

  override val tableQ: dbConfig.profile.api.TableQuery[CommandsTable] = TableQuery[CommandsTable]
}
