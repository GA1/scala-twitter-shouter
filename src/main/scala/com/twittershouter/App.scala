package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object App {

  def main(args: Array[String]): Unit = {

    implicit val appActorSystem = ActorSystem("twitter-shouter-system")
    implicit val appActorMaterializer = ActorMaterializer()

    val controller = new Controller {
      override implicit val actorSystem: ActorSystem = appActorSystem
    }

    val bindingFuture = Http().bindAndHandle(controller.routes, "0.0.0.0", 8080)
    appActorSystem.log.info("Server is up")
  }


}
