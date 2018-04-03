package com.twittershouter

import akka.http.scaladsl.server.Directives._


trait Controller extends HealthCheckService {


  def routes = get {
    healthCheckRoute
  }

}