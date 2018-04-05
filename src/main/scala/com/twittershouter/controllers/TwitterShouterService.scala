package com.twittershouter.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.twittershouter.business.TwitterManaging
import com.twittershouter.models.AppModelProtocol

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait TwitterShouterService extends AppModelProtocol {

  val twitterManager: TwitterManaging
  implicit val actorSystem: ActorSystem
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
      actorSystem.log.info("There was a problem while handling a /" + servicePath + " request")
      InternalServerError -> resp.toString
    }
}


