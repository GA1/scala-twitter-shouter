package com.twittershouter

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twittershouter.controllers.HealthCheckService
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext

class HealthCheckServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val testSubject = new HealthCheckService {
    override implicit val actorSystem: ActorSystem = TestUtils.testActorSystem
    override implicit val executionContext: ExecutionContext = TestUtils.testActorExecutionContext
  }

  "The health check endpoint" should {
    "return a healthy response" in {
      Get("/health-check") ~> testSubject.healthCheckRoute ~> check {
        responseAs[String] shouldEqual """{"message":"Up and healthy."}""".stripMargin
      }
    }
  }
}