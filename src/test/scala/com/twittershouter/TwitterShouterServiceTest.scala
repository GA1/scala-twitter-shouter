package com.twittershouter

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.controllers.TwitterShouterService
import com.twittershouter.models.{DataErrorWrapper, Tweet}
import com.twittershouter.providers.{TwitterCaller, TwitterCalling}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class TwitterShouterServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val testSubject = new TwitterShouterService with TestUtils.TestActorSystemContext{
    override val twitterManager: TwitterManaging = new TwitterManager with TestUtils.TestActorSystemContext{
      override val twitterCaller: TwitterCalling = new TwitterCaller with TestUtils.TestActorSystemProvider {
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