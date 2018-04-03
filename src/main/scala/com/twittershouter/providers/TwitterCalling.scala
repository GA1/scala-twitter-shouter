package com.twittershouter.providers

import akka.actor.ActorSystem
import com.twittershouter.model.Tweet

import scala.concurrent.{ExecutionContext, Future}

trait TwitterCalling {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  def getTweets(): Future[List[Tweet]]

}


abstract class TwitterCaller extends TwitterCalling {

  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  override def getTweets(): Future[List[Tweet]] =
    Future (List(Tweet("first dummy tweet"), Tweet("second dummy tweet")))

}
