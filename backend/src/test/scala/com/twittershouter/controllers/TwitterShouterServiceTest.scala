package com.twittershouter.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.twittershouter.TestUtils
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.models.{DataErrorWrapper, Tweet}
import com.twittershouter.providers.twitter.{TwitterAuthenticating, TwitterTweetsRetriever, TwitterTweetsRetrieving}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class TwitterShouterServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val CORRECT_TOKEN = "correctToken"
  val AUTHENTICATION_ERROR = "There was an authentication error!"
  val TWEETS_RETRIEVAL_ERROR = "There was an error while retrieving the tweets!"

  private def createAuthenticatorReturning(dew: DataErrorWrapper[String]) = {
    new TwitterAuthenticating with TestUtils.TestActorSystemProvider {
      override def authenticateApp(): Future[DataErrorWrapper[String]] = Future {dew}
    }
  }

  private def createTwitterShouterServiceWithAuthenticator(authenticator: TwitterAuthenticating) = {
    val retriever = new TwitterTweetsRetrieving with TestUtils.TestActorSystemProvider {
      override def getTweetsFromTwitterApi(accessToken: String, userName: String, numberOfTweets: Int): Future[DataErrorWrapper[List[Tweet]]] = {
        if (CORRECT_TOKEN == accessToken) Future(DataErrorWrapper(Some(List(Tweet("a"), Tweet("b"))), None))
        else Future(DataErrorWrapper(None, Some(TWEETS_RETRIEVAL_ERROR)))
      }
    }
    createTwitterShouterServiceWithAuthenticatorAndRetriever(authenticator, retriever)
  }

  private def createTwitterShouterServiceWithfailingRetriever() = {
    val retriever = new TwitterTweetsRetrieving with TestUtils.TestActorSystemProvider {
      override def getTweetsFromTwitterApi(accessToken: String, userName: String, numberOfTweets: Int) =
        Future(DataErrorWrapper(None, Some(TWEETS_RETRIEVAL_ERROR)))
    }
    createTwitterShouterServiceWithAuthenticatorAndRetriever(successfulAuthenticator, retriever)
  }

  private def createTwitterShouterServiceWithAuthenticatorAndRetriever(authenticator: TwitterAuthenticating, retriever: TwitterTweetsRetrieving) =
    new TwitterShouterService with TestUtils.TestActorSystemContext {
      override val twitterManager: TwitterManaging = new TwitterManager with TestUtils.TestActorSystemContext {
        override val twitterTweetRetriever: TwitterTweetsRetrieving = retriever
        override val twitterAuthenticator: TwitterAuthenticating = authenticator
      }
      override implicit val actorMaterializer: ActorMaterializer = TestUtils.testActorMaterializer
    }

  private val successfulAuthenticator = createAuthenticatorReturning(DataErrorWrapper(Some(CORRECT_TOKEN), None))

  "The tweets endpoint" should {
    "should return bad request if userName is lacking" in {
      val testSubject = createTwitterShouterServiceWithAuthenticator(successfulAuthenticator)
      Get("/shouted?numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        status shouldEqual StatusCodes.BadRequest
      }
    }
  }

  "The tweets endpoint" should {
    "should return normally if numberOfTweets is lacking, since it defaults to 10" in {
      val testSubject = createTwitterShouterServiceWithAuthenticator(successfulAuthenticator)
      Get("/shouted?userName=Trump") ~> testSubject.tweetsRoute ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }

  "The tweets endpoint" should {
    "return tweets" in {
      val testSubject = createTwitterShouterServiceWithAuthenticator(successfulAuthenticator)
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"data":{"tweets":[{"text":"A!"},{"text":"B!"}]}}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "return an error if authenticator has failed" in {
      val failingAuthenticator = createAuthenticatorReturning(DataErrorWrapper(None, Some(AUTHENTICATION_ERROR)))
      val testSubject = createTwitterShouterServiceWithAuthenticator(failingAuthenticator)
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"error":"""" + AUTHENTICATION_ERROR + """"}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "return an error if tweets retriever has failed" in {
      val failingAuthenticator = createAuthenticatorReturning(DataErrorWrapper(None, Some(AUTHENTICATION_ERROR)))
      val testSubject = createTwitterShouterServiceWithfailingRetriever()
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"error":"""" + TWEETS_RETRIEVAL_ERROR + """"}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "return tweets without trying to authenticate the app if the app was already authenticated" in {
      val authenticatorThatWorksOnlyOnce = {
        new TwitterAuthenticating with TestUtils.TestActorSystemProvider {
          var count = 0
          override def authenticateApp(): Future[DataErrorWrapper[String]] = {
            if (count == 0) {
              count = count + 1
              Future {DataErrorWrapper(Some(CORRECT_TOKEN), None)}
            } else {
              Future {DataErrorWrapper(None, Some(AUTHENTICATION_ERROR))}
            }
          }
        }
      }
      createAuthenticatorReturning(DataErrorWrapper(None, Some(AUTHENTICATION_ERROR)))
      val testSubject = createTwitterShouterServiceWithAuthenticator(authenticatorThatWorksOnlyOnce)
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"data":{"tweets":[{"text":"A!"},{"text":"B!"}]}}""".stripMargin.stripMargin
      }
    }
  }


}