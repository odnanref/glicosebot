package service.Exception

/**
  * No shop configuration variable found exception
  *
  * @param msg
  */
case class NoShopConfigException(msg: String = "No shop config variable found.") extends Exception(msg) {

  def this(msg: String, cause: Throwable) = {
    this(msg)
    initCause(cause)
  }

  def this(cause: Throwable) = {
    this(Option(cause).map(_.toString).orNull)
    initCause(cause)
  }

  def this() = {
    this(null: String)
  }

}

