package com.twittershouter.business

import akka.actor.ActorSystem
import com.twittershouter.models.{AppModelProtocol, DataErrorWrapper, Tweet, TweetResponse}
import com.twittershouter.providers.TwitterCalling

import scala.concurrent.{ExecutionContext, Future}

trait TwitterManaging extends AppModelProtocol {

//  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val twitterCaller: TwitterCalling

  def shoutedTweets(): Future[DataErrorWrapper[TweetResponse]]

}

abstract class TwitterManager extends TwitterManaging {

  val twitterCaller: TwitterCalling

  private val tweetShoutConverter = new TweetShoutConverter()

  override def shoutedTweets(): Future[DataErrorWrapper[TweetResponse]] =
    twitterCaller.getTweets().map(wrappedTweets => {
      if (wrappedTweets.error.isEmpty) {
        val tweets = wrappedTweets.data.get
        val shoutedTweets: List[Tweet] = tweets.map(tweetShoutConverter.toShoutedTweet(_))
        DataErrorWrapper(Some(TweetResponse(shoutedTweets)), None)
      } else {
        DataErrorWrapper(None, wrappedTweets.error)
      }
    })


}
