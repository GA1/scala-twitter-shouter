package com.twittershouter.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twittershouter.TestComponentFactory
import com.twittershouter.models.DataErrorWrapper
import com.twittershouter.providers.twitter.TwitterAuthenticating
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class TwitterShouterServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val tcf = TestComponentFactory

  "The tweets endpoint" should {
    "return bad request if userName is lacking" in {
      val testSubject = tcf.createTwitterShouterServiceWithAuthenticator(tcf.successfulAuthenticator)
      Get("/shouted?numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        status shouldEqual StatusCodes.BadRequest
      }
    }
  }

  "The tweets endpoint" should {
    "return normally if numberOfTweets is lacking, since it defaults to 10" in {
      val testSubject = tcf.createTwitterShouterServiceWithAuthenticator(tcf.successfulAuthenticator)
      Get("/shouted?userName=Trump") ~> testSubject.tweetsRoute ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }

  "The tweets endpoint" should {
    "return tweets" in {
      val testSubject = tcf.createTwitterShouterServiceWithAuthenticator(tcf.successfulAuthenticator)
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"data":{"tweets":[{"text":"A!"},{"text":"B!"}]}}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "return an error if authenticator has failed" in {
      val failingAuthenticator = tcf.createAuthenticatorReturning(DataErrorWrapper(None, Some(tcf.AUTHENTICATION_ERROR)))
      val testSubject = tcf.createTwitterShouterServiceWithAuthenticator(failingAuthenticator)
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"error":"""" + tcf.AUTHENTICATION_ERROR + """"}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "return an error if tweets retriever has failed" in {
      val failingAuthenticator = tcf.createAuthenticatorReturning(DataErrorWrapper(None, Some(tcf.AUTHENTICATION_ERROR)))
      val testSubject = tcf.createTwitterShouterServiceWithfailingRetriever()
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"error":"""" + tcf.TWEETS_RETRIEVAL_ERROR + """"}""".stripMargin
      }
    }
  }

  "The tweets endpoint" should {
    "return tweets without trying to authenticate the app if the app was already authenticated" in {
      val authenticatorThatWorksOnlyOnce = {
        new TwitterAuthenticating with TestComponentFactory.TestActorSystemProvider {
          var count = 0
          override def authenticateApp(): Future[DataErrorWrapper[String]] = {
            if (count == 0) {
              count = count + 1
              Future {DataErrorWrapper(Some(tcf.CORRECT_TOKEN), None)}
            } else {
              Future {DataErrorWrapper(None, Some(tcf.AUTHENTICATION_ERROR))}
            }
          }
        }
      }
      tcf.createAuthenticatorReturning(DataErrorWrapper(None, Some(tcf.AUTHENTICATION_ERROR)))
      val testSubject = tcf.createTwitterShouterServiceWithAuthenticator(authenticatorThatWorksOnlyOnce)
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute
      Get("/shouted?userName=trump&numberOfTweets=2") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"data":{"tweets":[{"text":"A!"},{"text":"B!"}]}}""".stripMargin.stripMargin
      }
    }
  }


}