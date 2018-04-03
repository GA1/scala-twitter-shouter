package com.twittershouter.providers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.twittershouter.model.Tweet

import scala.concurrent.{ExecutionContext, Future}

trait TwitterCaller {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  def getTweets(): Future[List[Tweet]] = Future (List(Tweet("first dummy tweet"), Tweet("second dummy tweet")))


}
