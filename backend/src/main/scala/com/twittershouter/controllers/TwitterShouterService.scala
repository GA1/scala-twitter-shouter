package com.twittershouter.controllers

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError}
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

  def tweetsRoute = path("shouted") {
    parameters('userName.as[String].?, 'numberOfTweets.as[Int] ? 10) { (userName, numberOfTweets) =>
      if (userName.isEmpty)
        complete(BadRequest)
      else {
        onComplete(twitterManager.shoutedTweets(userName.get, numberOfTweets)) {
          case Success(resp) => complete {resp}
          case Failure(resp) => handleFailure(resp)
        }
      }
    }
  }

  private def handleFailure(resp: Throwable) =
    complete {
      InternalServerError -> resp.toString
    }
}


