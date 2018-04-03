package com.twittershouter.controller

import akka.http.scaladsl.server.Directives._
import com.twittershouter.model.AppModelProtocol

trait V1Api extends TwitterShouterService {

  def v1ApiRoutes = pathPrefix("v1") {
    tweetsRoute
  }
}


