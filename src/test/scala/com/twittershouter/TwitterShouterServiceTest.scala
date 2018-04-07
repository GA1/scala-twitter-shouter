package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.controllers.TwitterShouterService
import com.twittershouter.models.{DataErrorWrapper, Tweet}
import com.twittershouter.providers.{TwitterCaller, TwitterCalling}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.{ExecutionContext, Future}

class TwitterShouterServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val testSubject = new TwitterShouterService {
    override implicit val actorSystem = TestUtils.testActorSystem
    override implicit val executionContext = TestUtils.testActorExecutionContext
    override val twitterManager: TwitterManaging = new TwitterManager {

      override implicit val actorSystem = TestUtils.testActorSystem
      override implicit val executionContext: ExecutionContext = TestUtils.testActorExecutionContext
      override implicit val actorMaterializer: ActorMaterializer = TestUtils.testActorMaterializer

      override val twitterCaller: TwitterCalling = new TwitterCaller {
        override implicit val actorSystem: ActorSystem = TestUtils.testActorSystem
        override implicit val executionContext: ExecutionContext = TestUtils.testActorExecutionContext
        override implicit val actorMaterializer: ActorMaterializer = TestUtils.testActorMaterializer
        override def getTweets(): Future[DataErrorWrapper[List[Tweet]]] =
          Future (DataErrorWrapper(Some(List(Tweet("a"), Tweet("b"))), None))
      }
    }
    override implicit val actorMaterializer: ActorMaterializer = TestUtils.testActorMaterializer
  }

  "The tweets endpoint" should {
    "should return tweets" in {
      Get("/tweets") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"data":{"tweets":[{"text":"A!"},{"text":"B!"}]}}""".stripMargin
      }
    }
  }
}