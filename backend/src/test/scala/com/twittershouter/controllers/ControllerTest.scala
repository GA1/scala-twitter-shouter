package com.twittershouter.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.twittershouter.TestComponentFactory
import com.twittershouter.TestComponentFactory.TestActorSystemProvider
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.providers.twitter.{TwitterAuthenticating, TwitterTweetsRetrieving}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext

class ControllerTest extends WordSpec with Matchers with ScalatestRouteTest {

  private val tcf = TestComponentFactory

  "The app cotroller" should {
    "build the full path for shouted tweets correctly" in {
      val testSubject = new Controller() {
        override val twitterManager: TwitterManaging = new TwitterManager with TestComponentFactory.TestActorSystemContext {
          override val twitterTweetRetriever: TwitterTweetsRetrieving = tcf.successfulRetriever
          override val twitterAuthenticator: TwitterAuthenticating = tcf.successfulAuthenticator
        }
        override implicit val actorSystem: ActorSystem = TestComponentFactory.testActorSystem
        override implicit val executionContext: ExecutionContext = TestComponentFactory.testActorExecutionContext
        override implicit val actorMaterializer: ActorMaterializer = TestComponentFactory.testActorMaterializer
      }
      Get("/api/v1/shouted?userName=trump&numberOfTweets=2") ~> testSubject.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }
}