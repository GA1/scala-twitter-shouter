package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.controllers.Controller
import com.twittershouter.providers.twitter.{TwitterAuthenticator, TwitterTweetsRetriever, TwitterTweetsRetrieving}

import scala.concurrent.ExecutionContext

object App {

  def main(args: Array[String]): Unit = {

    implicit val appActorSystem = ActorSystem("twitter-shouter-system")
    implicit val appActorMaterializer = ActorMaterializer()
    implicit val appExecutionContext = appActorSystem.dispatcher

    trait ActorSystemContext {
      implicit val executionContext: ExecutionContext = appExecutionContext
    }

    trait ActorSystemProvider extends ActorSystemContext {
      implicit val actorSystem: ActorSystem = appActorSystem
      implicit val actorMaterializer = appActorMaterializer
    }

    val controller = new Controller {
      override implicit val actorSystem: ActorSystem = appActorSystem
      override implicit val executionContext: ExecutionContext = appExecutionContext
      override implicit val actorMaterializer: ActorMaterializer = appActorMaterializer

      override val twitterManager: TwitterManaging = new TwitterManager with ActorSystemContext {
        override val twitterTweetRetriever: TwitterTweetsRetrieving = new TwitterTweetsRetriever with ActorSystemProvider
        override val twitterAuthenticator: TwitterAuthenticator = new TwitterAuthenticator with ActorSystemProvider
      }
    }

    Http().bindAndHandle(controller.routes, "0.0.0.0", 8080)
    appActorSystem.log.info("Server is up")

  }
}
