package com.twittershouter.providers.twitter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.twittershouter.AppConfig
import com.twittershouter.models.{AppModelProtocol, DataErrorWrapper, Tweet}

import scala.concurrent.{ExecutionContext, Future}

trait TwitterTweetsRetrieving {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext
  implicit val actorMaterializer: ActorMaterializer

  def getTweetsFromTwitterApi(accessToken: String): Future[DataErrorWrapper[List[Tweet]]]
}

abstract class TwitterCaller extends TwitterTweetsRetrieving with AppModelProtocol{

  def getTweetsFromTwitterApi(accessToken: String): Future[DataErrorWrapper[List[Tweet]]] = {
    val url = AppConfig.twitterApiRetrieveTweetsUrl
    actorSystem.log.info("calling /tweets")
    val headers: List[HttpHeader] = List(RawHeader("Authorization", "Bearer " + accessToken))
    val request = HttpRequest(method = HttpMethods.GET, uri = url, headers = headers)
    Http().singleRequest(request) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[List[Tweet]].map(tweets => DataErrorWrapper(Some(tweets), None))
      case _ => {
        val message = "There was an error when fetching tweets from: " + url
        actorSystem.log.error(message)
        Future(DataErrorWrapper(None, Some(message)))
      }
    }
  }

}
