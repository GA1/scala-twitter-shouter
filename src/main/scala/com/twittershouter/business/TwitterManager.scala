package com.twittershouter.business

import akka.actor.ActorSystem
import com.twittershouter.model.{Tweet, TweetResponse}
import com.twittershouter.providers.TwitterCaller

import scala.concurrent.{ExecutionContext, Future}

trait TwitterManager {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val twitterCaller: TwitterCaller

  def shoutedTweets(): Future[TweetResponse] =
    twitterCaller.getTweets().map(tweets => TweetResponse(tweets))


}
