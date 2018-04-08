package com.twittershouter.business

import com.twittershouter.models.{AppModelProtocol, DataErrorWrapper, Tweet, TweetResponse}
import com.twittershouter.providers.twitter.{TwitterAuthenticating, TwitterTweetsRetrieving}

import scala.concurrent.{ExecutionContext, Future}

trait TwitterManaging extends AppModelProtocol {

  implicit val executionContext: ExecutionContext

  val twitterTweetRetriever: TwitterTweetsRetrieving
  val twitterAuthenticator: TwitterAuthenticating

  def shoutedTweets(): Future[DataErrorWrapper[TweetResponse]]

}

abstract class TwitterManager extends TwitterManaging {

  val twitterTweetRetriever: TwitterTweetsRetrieving
  val twitterAuthenticator: TwitterAuthenticating

  private val tweetShoutConverter = new TweetShoutConverter()

  def getTweets(): Future[DataErrorWrapper[List[Tweet]]] =
    twitterAuthenticator.authenticateApp()
      .flatMap(dataErrorObject => {
        if (dataErrorObject.error.isEmpty) twitterTweetRetriever.getTweetsFromTwitterApi(dataErrorObject.data.get)
        else Future (DataErrorWrapper(None, dataErrorObject.error))
      }
    )

  def shoutedTweets(): Future[DataErrorWrapper[TweetResponse]] =
    getTweets().map(wrappedTweets => {
      if (wrappedTweets.error.isEmpty) {
        val tweets = wrappedTweets.data.get
        val shoutedTweets: List[Tweet] = tweets.map(tweetShoutConverter.toShoutedTweet(_))
        DataErrorWrapper(Some(TweetResponse(shoutedTweets)), None)
      } else {
        DataErrorWrapper(None, wrappedTweets.error)
      }
    })


}
