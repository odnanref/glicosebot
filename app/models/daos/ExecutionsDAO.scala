package models.daos

import models.entities.{Command, Execution}
import models.persistence.SlickTables.{CommandsTable, ExecutionsTable}
import slick.lifted.CanBeQueryCondition

import scala.concurrent.Future

/**
  * executions that are provided to commands
  *
  * Created by andref on 25-05-2017.
  */
class ExecutionsDAO extends AbstractBaseDAO[ExecutionsTable, Execution] {
  override def insert(row: Execution): Future[Long] = {
    insert(row)
  }

  override def deleteByFilter[C: CanBeQueryCondition](f: (ExecutionsTable) => C): Future[Int] = {
    deleteByFilter[C](f)
  }

  override def update(row: Execution): Future[Int] = {
    update(row)
  }

  override def update(rows: Seq[Execution]): Future[Unit] = {
    update(rows)
  }

  override def insert(rows: Seq[Execution]): Future[Seq[Long]] = {
    insert(rows)
  }

  override def findById(id: Long): Future[Option[Execution]] = {
    findById(id)
  }

  override def findByFilter[C: CanBeQueryCondition](f: (ExecutionsTable) => C): Future[Seq[Execution]] = {
    findByFilter[C](f)
  }

  override def deleteById(id: Long): Future[Int] = {
    deleteById(id)
  }

  override def deleteById(ids: Seq[Long]): Future[Int] = {
    deleteById(ids)
  }
}
