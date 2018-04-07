package com.twittershouter.controllers

import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.twittershouter.business.TwitterManaging
import com.twittershouter.models.AppModelProtocol

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait TwitterShouterService extends AppModelProtocol {

  val twitterManager: TwitterManaging
  implicit val executionContext: ExecutionContext
  implicit val actorMaterializer: ActorMaterializer

  private val servicePath = "tweets"

  def tweetsRoute = path(servicePath) {
    onComplete(twitterManager.shoutedTweets()) {
      case Success(resp) => complete { resp }
      case Failure(resp) => handleFailure(resp)
    }
  }

  private def handleFailure(resp: Throwable) =
    complete {
      InternalServerError -> resp.toString
    }
}


