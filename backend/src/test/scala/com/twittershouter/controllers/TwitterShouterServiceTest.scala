package com.twittershouter.controllers

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.twittershouter.TestUtils
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.models.{DataErrorWrapper, Tweet}
import com.twittershouter.providers.twitter.{TwitterAuthenticating, TwitterCaller, TwitterTweetsRetrieving}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class TwitterShouterServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val CORRECT_TOKEN = "correctToken"
  val AN_ERROR = "There was an error!"

  private def createAuthenticatorReturning(dew: DataErrorWrapper[String]) = {
    new TwitterAuthenticating with TestUtils.TestActorSystemProvider {
      override def authenticateApp(): Future[DataErrorWrapper[String]] = Future {dew}
    }
  }

  private def createTwitterShouterServiceWithAuthenticator(authenticator: TwitterAuthenticating) =
    new TwitterShouterService with TestUtils.TestActorSystemContext {
    override val twitterManager: TwitterManaging = new TwitterManager with TestUtils.TestActorSystemContext {
      override val twitterTweetRetriever: TwitterTweetsRetrieving = new TwitterCaller with TestUtils.TestActorSystemProvider {
        override def getTweetsFromTwitterApi(accessToken: String): Future[DataErrorWrapper[List[Tweet]]] = {
          if (CORRECT_TOKEN == accessToken) Future(DataErrorWrapper(Some(List(Tweet("a"), Tweet("b"))), None))
          else Future(DataErrorWrapper(None, Some(AN_ERROR)))
        }
      }
      override val twitterAuthenticator: TwitterAuthenticating = authenticator
    }
    override implicit val actorMaterializer: ActorMaterializer = TestUtils.testActorMaterializer
  }

  "The tweets endpoint" should {
    "should return tweets" in {
      val successfullAuthenticator = createAuthenticatorReturning(DataErrorWrapper(Some(CORRECT_TOKEN), None))
      val testSubject = createTwitterShouterServiceWithAuthenticator(successfullAuthenticator)
      Get("/tweets") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"data":{"tweets":[{"text":"A!"},{"text":"B!"}]}}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "should return an error if authenticator has failed" in {
      val failingAuthenticator = createAuthenticatorReturning(DataErrorWrapper(None, Some(AN_ERROR)))
      val testSubject = createTwitterShouterServiceWithAuthenticator(failingAuthenticator)
      Get("/tweets") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"error":"There was an error!"}""".stripMargin
      }
    }
  }

}