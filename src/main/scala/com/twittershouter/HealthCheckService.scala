package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._

trait HealthCheckService {

  implicit val actorSystem: ActorSystem

  def healthCheckRoute = path("health-check") {
    complete {"""{"message":"Up and healthy."}"""}
  }

}


