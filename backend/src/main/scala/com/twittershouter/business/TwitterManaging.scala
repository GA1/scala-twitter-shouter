package com.twittershouter.business

import com.twittershouter.models.{AppModelProtocol, DataErrorWrapper, Tweet, TweetResponse}
import com.twittershouter.providers.twitter.{TwitterAuthenticating, TwitterTweetsRetrieving}

import scala.concurrent.{ExecutionContext, Future}

trait TwitterManaging extends AppModelProtocol {

  implicit val executionContext: ExecutionContext

  val twitterTweetRetriever: TwitterTweetsRetrieving
  val twitterAuthenticator: TwitterAuthenticating

  def shoutedTweets(userName: String, numberOfTweets: Int): Future[DataErrorWrapper[TweetResponse]]

}

abstract class TwitterManager extends TwitterManaging {

  private val tweetShoutConverter = new TweetShoutConverter()
  private var cachedAppAccessToken: Option[String] = None

  private def getTweets(userName: String, numberOfTweets: Int): Future[DataErrorWrapper[List[Tweet]]] = {
    if (cachedAppAccessToken.isEmpty) {
      twitterAuthenticator.authenticateApp()
        .flatMap(dataErrorObject => {
          if (dataErrorObject.error.isEmpty) {
            val accessToken = dataErrorObject.data.get
            cachedAppAccessToken = Some(accessToken)
            twitterTweetRetriever.getTweetsFromTwitterApi(accessToken, userName, numberOfTweets)
          }
          else Future (DataErrorWrapper(None, dataErrorObject.error))
        }
      )
    } else {
      twitterTweetRetriever.getTweetsFromTwitterApi(cachedAppAccessToken.get, userName, numberOfTweets)
    }
  }

  def shoutedTweets(userName: String, numberOfTweets: Int): Future[DataErrorWrapper[TweetResponse]] =
    getTweets(userName, numberOfTweets).map(wrappedTweets => {
      if (wrappedTweets.error.isEmpty) {
        val tweets = wrappedTweets.data.get
        val shoutedTweets: List[Tweet] = tweets.map(tweetShoutConverter.toShoutedTweet(_))
        DataErrorWrapper(Some(TweetResponse(shoutedTweets)), None)
      } else {
        DataErrorWrapper(None, wrappedTweets.error)
      }
    })


}
