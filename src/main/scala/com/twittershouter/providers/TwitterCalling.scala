package com.twittershouter.providers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.ByteString
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
    actorSystem.log.info("calling /tweets")
    authenticateApp(AppConfig.twitterApiRetrieveTokenUrl )
    Future (List(Tweet("first dummy tweet"), Tweet("second dummy tweet")))
  }

  def generateRetrieveTokenUrl() = AppConfig.twitterApiRetrieveTokenUrl

  def authenticateApp(generatedUrl: String): Future[TwitterAppAuthenticationResponse] = {
    val utils = new AuthenticationUtils()
    val encodedCredentials =
      utils.encodeTwitterCredentials(AppConfig.twitterConsumerKey, AppConfig.twitterConsumerSecret)
    val headers: List[HttpHeader] = List(RawHeader("Authorization", "Basic " + encodedCredentials))
    val entity = FormData(Map("grant_type" -> "client_credentials")).toEntity
    val request = HttpRequest(method = HttpMethods.POST, uri = generatedUrl, entity = entity, headers = headers)
    Http().singleRequest(request) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => {
        val r = Unmarshal(entity).to[TwitterAppAuthenticationResponse]
        r
      }
      case HttpResponse(code, _, _, _) => {
        entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
        throw new Error("Got an error response with code " + code + " when calling " + generatedUrl)
      }
      case _ => {
        throw new Error("An unknown error ocurred when calling " + generatedUrl)
      }
    }
  }

}
