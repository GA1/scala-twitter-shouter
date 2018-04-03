package com.twittershouter.controller

import akka.http.scaladsl.server.Directives._


trait Controller extends HealthCheckService with TwitterShouterService {


  def routes = get {
    healthCheckRoute ~
    pathPrefix("v1") {
      tweetsRoute
    }


  }

}