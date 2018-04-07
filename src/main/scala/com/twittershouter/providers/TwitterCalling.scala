package com.twittershouter.providers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.twittershouter.AppConfig
import com.twittershouter.models.{AppModelProtocol, DataErrorWrapper, Tweet, AuthenticationResponse}

import scala.concurrent.{ExecutionContext, Future}

trait TwitterCalling {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext
  implicit val actorMaterializer: ActorMaterializer

  def getTweets(): Future[DataErrorWrapper[List[Tweet]]]
}

abstract class TwitterCaller extends TwitterCalling with AppModelProtocol{

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  var bearerToken = ""

  override def getTweets(): Future[DataErrorWrapper[List[Tweet]]] =
    authenticateApp(AppConfig.twitterApiRetrieveTokenUrl)
      .flatMap(dataErrorObject => {
          if (dataErrorObject.error.isEmpty) getTweetsFromTwitterApi(dataErrorObject.data.get)
          else Future (DataErrorWrapper(None, dataErrorObject.error))
        }
      )

  def generateRetrieveTokenUrl(): String = AppConfig.twitterApiRetrieveTokenUrl

  def getTweetsFromTwitterApi(accessToken: String): Future[DataErrorWrapper[List[Tweet]]] = {
    val url = AppConfig.twitterApiRetrieveTweetsUrl
    actorSystem.log.info("calling /tweets")
    val headers: List[HttpHeader] = List(RawHeader("Authorization", "Bearer " + accessToken))
    val request = HttpRequest(method = HttpMethods.GET, uri = url, headers = headers)
    Http().singleRequest(request) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[List[Tweet]].map(tweets => DataErrorWrapper(Some(tweets), None))
      case HttpResponse(code, _, entity, _) => {
        Future(DataErrorWrapper(None, Some("There was a problem when fetching tweets from: " + url)))
      }
      case _ => {
        val message = "There was an error when authenticating against: " + url
        actorSystem.log.error(message)
        Future(DataErrorWrapper(None, Some(message)))
      }
    }
  }

  def authenticateApp(url: String): Future[DataErrorWrapper[String]] = {
    val utils = new AuthenticationUtils()
    val encodedCredentials =
      utils.encodeTwitterCredentials(AppConfig.twitterConsumerKey, AppConfig.twitterConsumerSecret)
    val headers: List[HttpHeader] = List(RawHeader("Authorization", "Basic " + encodedCredentials))
    val entity = FormData(Map("grant_type" -> "client_credentials")).toEntity
    val request = HttpRequest(method = HttpMethods.POST, uri = url, entity = entity, headers = headers)
    Http().singleRequest(request) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => {
        val r = Unmarshal(entity).to[AuthenticationResponse].map(r => DataErrorWrapper(Some(r.access_token), None))
        r
      }
      case HttpResponse(code, _, entity, _) => {
        Future{DataErrorWrapper(None, Some("There was a problem when fetching tweets from: " + url))}
      }
      case _ => {
        val message = "There was an error when authenticating against: " + url
        actorSystem.log.error(message)
        Future(DataErrorWrapper(None, Some(message)))
      }
    }
  }

}
