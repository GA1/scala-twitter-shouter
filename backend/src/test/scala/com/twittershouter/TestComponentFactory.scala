package com.twittershouter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.controllers.TwitterShouterService
import com.twittershouter.models.{DataErrorWrapper, Tweet}
import com.twittershouter.providers.twitter.{TwitterAuthenticating, TwitterTweetsRetrieving}

import scala.concurrent.{ExecutionContext, Future}

object TestComponentFactory {

  implicit val testActorSystem = ActorSystem("test-actor-system")
  implicit val testActorMaterializer = ActorMaterializer()
  implicit val testActorExecutionContext = testActorSystem.dispatcher


  trait TestActorSystemContext {
    implicit val executionContext: ExecutionContext = testActorExecutionContext
  }

  trait TestActorSystemProvider extends TestActorSystemContext {
    implicit val actorSystem: ActorSystem = testActorSystem
    implicit val actorMaterializer: ActorMaterializer = testActorMaterializer
  }


  val CORRECT_TOKEN = "correctToken"
  val AUTHENTICATION_ERROR = "There was an authentication error!"
  val TWEETS_RETRIEVAL_ERROR = "There was an error while retrieving the tweets!"

  def createAuthenticatorReturning(dew: DataErrorWrapper[String]) = {
    new TwitterAuthenticating with TestComponentFactory.TestActorSystemProvider {
      override def authenticateApp(): Future[DataErrorWrapper[String]] = Future {dew}
    }
  }

  def createCorrectTwitterShouterService() = {
    createTwitterShouterServiceWithAuthenticatorAndRetriever(successfulAuthenticator, successfulRetriever)
  }

  def createTwitterShouterServiceWithAuthenticator(authenticator: TwitterAuthenticating) = {
    createTwitterShouterServiceWithAuthenticatorAndRetriever(authenticator, successfulRetriever)
  }

  val successfulAuthenticator = createAuthenticatorReturning(DataErrorWrapper(Some(CORRECT_TOKEN), None))

  val successfulRetriever = new TwitterTweetsRetrieving with TestComponentFactory.TestActorSystemProvider {
    override def getTweetsFromTwitterApi(accessToken: String, userName: String, numberOfTweets: Int) = {
      if (CORRECT_TOKEN == accessToken) Future(DataErrorWrapper(Some(List(Tweet("a"), Tweet("b"))), None))
      else Future(DataErrorWrapper(None, Some(TWEETS_RETRIEVAL_ERROR)))
    }
  }

  def createTwitterShouterServiceWithfailingRetriever() = {
    val retriever = new TwitterTweetsRetrieving with TestComponentFactory.TestActorSystemProvider {
      override def getTweetsFromTwitterApi(accessToken: String, userName: String, numberOfTweets: Int) =
        Future(DataErrorWrapper(None, Some(TWEETS_RETRIEVAL_ERROR)))
    }
    createTwitterShouterServiceWithAuthenticatorAndRetriever(successfulAuthenticator, retriever)
  }

  def createTwitterShouterServiceWithAuthenticatorAndRetriever(authenticator: TwitterAuthenticating, retriever: TwitterTweetsRetrieving) =
    new TwitterShouterService with TestComponentFactory.TestActorSystemContext {
      override val twitterManager: TwitterManaging = new TwitterManager with TestComponentFactory.TestActorSystemContext {
        override val twitterTweetRetriever: TwitterTweetsRetrieving = retriever
        override val twitterAuthenticator: TwitterAuthenticating = authenticator
      }
      override implicit val actorMaterializer: ActorMaterializer = TestComponentFactory.testActorMaterializer
    }

}
