package provider

import java.net.URI
import redis.clients.jedis.Jedis

import com.google.inject.{Inject, Singleton}
import javax.inject.Provider
import service.client.RedisClient

@Singleton
class RedisClientProvider @Inject()(configuration: play.api.Configuration) extends Provider[RedisClient] {

  override def get(): RedisClient = {
    val redisuri = configuration.getString("redis.uri")
      .getOrElse(
        throw new Exception("No redis.uri string configuration found in application.conf")
      )

    val uri = URI.create(redisuri)
    val pass = redisuri.split(":")(2).split("@")(0)


    val jedis = new Jedis(uri.getHost, uri.getPort)
    jedis.connect()
    jedis.auth(pass)
    new RedisClient(jedis)
  }
}
