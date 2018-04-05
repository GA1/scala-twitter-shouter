package com.twittershouter.controllers

import akka.http.scaladsl.server.Directives._
import com.twittershouter.models.AppModelProtocol

trait V1Api extends TwitterShouterService {

  def v1ApiRoutes = pathPrefix("v1") {
    tweetsRoute
  }
}


