package models.entities

/**
  * Command Execution
  *
  * A command has one or more executions, a execution issues a service or other action/event
  *
  * Created by andref on 02-06-2017.
  */
case class Execution(id:Long, name:String, execution:String) extends BaseEntity {

}
