package com.twittershouter.providers.twitter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.twittershouter.AppConfig
import com.twittershouter.models.{AppModelProtocol, AuthenticationResponse, DataErrorWrapper}
import com.twittershouter.providers.AuthenticationUtils

import scala.concurrent.{ExecutionContext, Future}

trait TwitterAuthenticating {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext
  implicit val actorMaterializer: ActorMaterializer

  def authenticateApp(): Future[DataErrorWrapper[String]]
}

abstract class TwitterAuthenticator extends TwitterAuthenticating with AppModelProtocol{

  private val utils = new AuthenticationUtils()

  def authenticateApp(): Future[DataErrorWrapper[String]] = {
    val url = AppConfig.twitterApiRetrieveTokenUrl

    def createAuthenticationRequest() = {
      val encodedCredentials =
        utils.encodeTwitterCredentials(AppConfig.twitterConsumerKey, AppConfig.twitterConsumerSecret)
      val headers: List[HttpHeader] = List(RawHeader("Authorization", "Basic " + encodedCredentials))
      val entity = FormData(Map("grant_type" -> "client_credentials")).toEntity
      val request = HttpRequest(method = HttpMethods.POST, uri = url, entity = entity, headers = headers)
      request
    }

    Http().singleRequest(createAuthenticationRequest()) flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => {
        Unmarshal(entity).to[AuthenticationResponse].map(r => {
          val accessToken = r.access_token
          DataErrorWrapper(Some(accessToken), None)
        })
      }
      case HttpResponse(StatusCodes.Forbidden, _, _, _) =>
        Future {
          DataErrorWrapper(None, Some("Could not authenticate with the provided credentials against: " + url))
        }
      case _ => {
        val message = "There was an error when authenticating against: " + url
        actorSystem.log.error(message)
        Future(DataErrorWrapper(None, Some(message)))
      }
    }
  }

}
