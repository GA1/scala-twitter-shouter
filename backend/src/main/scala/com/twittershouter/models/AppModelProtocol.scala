package com.twittershouter.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait AppModelProtocol extends SprayJsonSupport with DefaultJsonProtocol{

  implicit val tweetFormat = jsonFormat1(Tweet)
  implicit val tweetResponseFormat = jsonFormat1(TweetResponse)
  implicit val authenticationResponseFormat = jsonFormat2(AuthenticationResponse)
  implicit val dateErrorWrapperTweetsFormat = jsonFormat2(DataErrorWrapper[TweetResponse])
}
