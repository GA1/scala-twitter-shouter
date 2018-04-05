package com.twittershouter

object AppConfig {

  import com.typesafe.config.ConfigFactory

  lazy val config = ConfigFactory.load()

  lazy val twitterApiHost = config.getString("app.twitterApiHost")
  lazy val twitterApiRetrieveTokenUrl = config.getString("app.twitterApiRetrieveTokenUrl")
  lazy val twitterConsumerKey = config.getString("app.twitterConsumerKey")
  lazy val twitterConsumerSecret = config.getString("app.twitterConsumerSecret")
}