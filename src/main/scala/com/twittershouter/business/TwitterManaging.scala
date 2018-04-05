package com.twittershouter.business

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.twittershouter.models.{AppModelProtocol, Tweet, TweetResponse}
import com.twittershouter.providers.TwitterCalling

import scala.concurrent.{ExecutionContext, Future}

trait TwitterManaging extends AppModelProtocol {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext
  implicit val actorMaterializer: ActorMaterializer

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
