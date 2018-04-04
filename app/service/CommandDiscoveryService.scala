package service

import models.daos.CommandsDAO
import models.entities.Execution
import play.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * To handle text passed to the bot and see if the text triggers any
  * database commands that lead to executions.
  *
  * Created by andref on 20-07-2017.
  */
class CommandDiscoveryService (commandsDAO: CommandsDAO, text: String) {

  val words = text.split("\\s+").toList

  Logger.debug("Object CommandDiscoveryService created...")
  // Temporarily handle with concurrent searchs on the database
  // TODO Load all commands to memory and check the list ?? memcache or some other cache service

  //  Cannot find an implicit ExecutionContext fix
  import play.api.libs.concurrent.Execution.Implicits._

  private def findExecution() : Future[Seq[Execution]] = {
    Logger.debug("SEARCHING FOR EXECUTIONS")
    //val executions = words.map(commandsDAO.findExecutionByName(_)) // List[Future[Seq[Execution]]]
    val executions = commandsDAO.findExecutionByNames(words)
    //executions.foreach(_.onFailure{case x => Logger.debug("FailURE1: " + x ); throw x })
    //val filtered = executions.map(_.filter(_.nonEmpty)) // List[Future[Seq[Execution]]
    //val flattened = Future.sequence(filtered.toList).map(_.flatten) // Future[List[Execution]]
    //flattened.onFailure{case x => Logger.debug("FailURE3: " + x); throw x}
    //flattened
    executions.onFailure{case x => Logger.debug("FAILED SQL SEARCH " + x ); println("FAILED SQL : " + x )}
    executions
  }

  def getExecution(): Future[Option[Execution]] = {
    val executions = findExecution()
    executions.map { e =>
      if (e.size > 1) {
        Logger.debug("ERROR_TOMANYEXECS: Found more than one execution " + e.head)
        e.foreach{
          execs => Logger.debug("ERROR_TOMANYEXECS: " + execs)
        }
        None
      } else {
        e.headOption
      }
    }
  }

}
