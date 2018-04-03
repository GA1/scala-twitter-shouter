package com.twittershouter.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives._
import com.twittershouter.business.TwitterManager
import com.twittershouter.model.AppProtocol

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait TwitterShouterService extends AppProtocol {

  val twitterManager: TwitterManager
  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  def tweetsRoute = path("tweets") {
    onComplete(twitterManager.shoutedTweets()) {
      case Success(resp) => complete { resp }
      case Failure(resp) => handleFailure(resp)
    }
  }


  private def handleFailure(resp: Throwable) =
    complete {
      actorSystem.log.info("There was a problem")
      InternalServerError -> resp.toString
    }
}


