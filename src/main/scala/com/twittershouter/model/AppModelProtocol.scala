package com.twittershouter.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait AppModelProtocol extends SprayJsonSupport with DefaultJsonProtocol{

  implicit val tweetFormat = jsonFormat1(Tweet)
  implicit val tweetResponseFormat = jsonFormat1(TweetResponse)

}
