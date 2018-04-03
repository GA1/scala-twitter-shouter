package com.twittershouter.business

import akka.actor.ActorSystem
import com.twittershouter.model.{Tweet, TweetResponse}
import com.twittershouter.providers.TwitterCalling

import scala.concurrent.{ExecutionContext, Future}

trait TwitterManaging {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val twitterCaller: TwitterCalling

  def shoutedTweets(): Future[TweetResponse]

}

abstract class TwitterManager extends TwitterManaging {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val twitterCaller: TwitterCalling

  private val tweetShoutConverter = new TweetShoutConverter()

  override def shoutedTweets(): Future[TweetResponse] =
    twitterCaller.getTweets().map(tweets => TweetResponse(tweets.map(tweetShoutConverter.toShoutedTweet(_))))


}
