package com.twittershouter.providers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.twittershouter.AppConfig
import com.twittershouter.models.{AppModelProtocol, Tweet, TwitterAppAuthenticationResponse}

import scala.concurrent.{ExecutionContext, Future}

trait TwitterCalling {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext
  implicit val actorMaterializer: ActorMaterializer

  def getTweets(): Future[List[Tweet]]
}

abstract class TwitterCaller extends TwitterCalling with AppModelProtocol{

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  var bearerToken = ""

  override def getTweets(): Future[List[Tweet]] = {
    return authenticateApp(AppConfig.twitterApiRetrieveTokenUrl)
      .flatMap(token => getTweetsFromTwitterapi(AppConfig.twitterApiRetrieveTweetsUrl, token))
  }

  def generateRetrieveTokenUrl() = AppConfig.twitterApiRetrieveTokenUrl

  def getTweetsFromTwitterapi(url: String, accessToken: String): Future[List[Tweet]] = {
    actorSystem.log.info("calling /tweets")
    val headers: List[HttpHeader] = List(RawHeader("Authorization", "Bearer " + accessToken))
    val request = HttpRequest(method = HttpMethods.GET, uri = url, headers = headers)
    Http().singleRequest(request) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => {
        val r = Unmarshal(entity).to[List[Tweet]]
        r
      }
      case HttpResponse(code, a, b, c) => {
        throw new Error("Got an error response with code " + code + " when calling " + url)
      }
      case _ => {
        throw new Error("An unknown error ocurred when calling " + url)
      }
    }
  }

  def authenticateApp(url: String): Future[String] = {
    actorSystem.log.info("calling /oauth2/token")
    val utils = new AuthenticationUtils()
    val encodedCredentials =
      utils.encodeTwitterCredentials(AppConfig.twitterConsumerKey, AppConfig.twitterConsumerSecret)
    val headers: List[HttpHeader] = List(RawHeader("Authorization", "Basic " + encodedCredentials))
    val entity = FormData(Map("grant_type" -> "client_credentials")).toEntity
    val request = HttpRequest(method = HttpMethods.POST, uri = url, entity = entity, headers = headers)
    Http().singleRequest(request) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => {
        val r = Unmarshal(entity).to[TwitterAppAuthenticationResponse].map(_.access_token)
        r
      }
      case HttpResponse(code, _, _, _) => {
        throw new Error("Got an error response with code " + code + " when calling " + url)
      }
      case _ => {
        throw new Error("An unknown error ocurred when calling " + url)
      }
    }
  }

}
