package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.twittershouter.business.{TwitterManager, TwitterManaging}
import com.twittershouter.controller.Controller
import com.twittershouter.providers.{TwitterCaller, TwitterCalling}

import scala.concurrent.ExecutionContext

object App {

  def main(args: Array[String]): Unit = {

    implicit val appActorSystem = ActorSystem("twitter-shouter-system")
    implicit val appActorMaterializer = ActorMaterializer()
    implicit val appExecutionContext = appActorSystem.dispatcher

    val controller = new Controller {

      override implicit val actorSystem: ActorSystem = appActorSystem
      override implicit val executionContext: ExecutionContext = appExecutionContext

      override val twitterManager: TwitterManaging = new TwitterManager {

        override implicit val actorSystem: ActorSystem = appActorSystem
        override implicit val executionContext: ExecutionContext = appExecutionContext

        override val twitterCaller: TwitterCalling = new TwitterCaller {
          override implicit val actorSystem: ActorSystem = appActorSystem
          override implicit val executionContext: ExecutionContext = appExecutionContext
        }
      }
    }

    Http().bindAndHandle(controller.routes, "0.0.0.0", 8080)
    appActorSystem.log.info("Server is up")

  }
}
