package service.client

import javax.inject.{Inject, Singleton}
import redis.clients.jedis.Jedis

@Singleton
class RedisClient @Inject()(jedis: Jedis) {

  /**
    * get a map string -> string
    *
    * @param key
    * @return
    */
  def get(key:String) : Map[String,String] = {
    val tmp = jedis.hgetAll(key: String)

    import collection.JavaConverters._
    tmp.asScala.toMap
  }

  /**
    * set a map string -> string
    *
    * @param key
    * @param x
    */
  def set(key: String, x: Map[String, String]) = {
    x.foreach { f =>
      jedis.hset(key, f._1, f._2)
    }
  }

  /**
    * set the hashmap value of string
    *
    * @param key
    * @param x
    * @return
    */
  def set(key:String, x: String) = {
    jedis.hset(key, "value", x)
  }

  /**
    * Get from set hashmap field value
    * @param key
    * @return
    */
  def getString(key:String) : String = {
    val r = jedis.hget(key, "value")
    if (r == null) {
      ""
    } else {
      r
    }
  }

}
