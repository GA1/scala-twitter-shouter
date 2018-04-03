package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.controller.TwitterShouterService
import com.twittershouter.model.Tweet
import com.twittershouter.providers.{TwitterCaller, TwitterCalling}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.{ExecutionContext, Future}

class TwitterShouterServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val twitterCaller = new TwitterCaller {
    override implicit val actorSystem: ActorSystem = TestUtils.testActorSystem
    override implicit val executionContext: ExecutionContext = TestUtils.testActorExecutionContext
    override def getTweets(): Future[List[Tweet]] =
      Future (List(Tweet("a"), Tweet("b")))
  }

  val testSubject = new TwitterShouterService {
    override implicit val actorSystem = TestUtils.testActorSystem
    override implicit val executionContext = TestUtils.testActorExecutionContext
    override val twitterManager: TwitterManaging = new TwitterManager {

      override implicit val actorSystem = TestUtils.testActorSystem
      override implicit val executionContext: ExecutionContext = TestUtils.testActorExecutionContext

      override val twitterCaller: TwitterCalling = new TwitterCaller {
        override implicit val actorSystem: ActorSystem = TestUtils.testActorSystem
        override implicit val executionContext: ExecutionContext = TestUtils.testActorExecutionContext
        override def getTweets(): Future[List[Tweet]] =
          Future (List(Tweet("a"), Tweet("b")))
      }
    }
  }

  "The tweets endpoint" should {
    "should return tweets" in {
      Get("/tweets") ~> testSubject.tweetsRoute ~> check {
        responseAs[String] shouldEqual """{"tweets":[{"text":"A!"},{"text":"B!"}]}""".stripMargin
      }
    }
  }
}