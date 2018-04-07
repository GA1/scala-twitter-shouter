package com.twittershouter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

object TestUtils {

  implicit val testActorSystem = ActorSystem("test-actor-system")
  implicit val testActorMaterializer = ActorMaterializer()
  implicit val testActorExecutionContext = testActorSystem.dispatcher


  trait TestActorSystemContext {
    implicit val executionContext: ExecutionContext = testActorExecutionContext
  }

  trait TestActorSystemProvider extends TestActorSystemContext {
    implicit val actorSystem: ActorSystem = testActorSystem
    implicit val actorMaterializer: ActorMaterializer = testActorMaterializer
  }

}
