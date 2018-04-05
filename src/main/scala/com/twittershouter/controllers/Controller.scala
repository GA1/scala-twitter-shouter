package com.twittershouter.controllers

import akka.http.scaladsl.server.Directives._


trait Controller extends HealthCheckService with V1Api {

  def routes = get {
    healthCheckRoute ~
    v1ApiRoutes
  }
}