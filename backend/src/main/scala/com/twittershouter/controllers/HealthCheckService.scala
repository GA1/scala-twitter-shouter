package com.twittershouter.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

trait HealthCheckService {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  def healthCheckRoute = path("health-check") {
    complete {"""{"message":"Up and healthy."}"""}
  }

}


