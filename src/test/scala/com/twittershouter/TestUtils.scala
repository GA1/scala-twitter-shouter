package com.twittershouter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

object TestUtils {

  implicit val testActorSystem = ActorSystem("test-actor-system")
  implicit val testActorMaterializer = ActorMaterializer()
  implicit val testActorExecutionContext = testActorSystem.dispatcher

  trait TestActorSystemProvider {
    implicit val testActorSystem: ActorSystem = testActorSystem
    implicit val testActorMaterializer: ActorMaterializer = testActorMaterializer
    implicit val testExecutionContext: ExecutionContext = testActorExecutionContext
  }

}
