package com.twittershouter.controllers

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twittershouter.TestUtils.TestActorSystemProvider
import org.scalatest.{Matchers, WordSpec}

class HealthCheckServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  val testSubject = new HealthCheckService with TestActorSystemProvider

  "The health check endpoint" should {
    "return a healthy response" in {
      Get("/health-check") ~> testSubject.healthCheckRoute ~> check {
        responseAs[String] shouldEqual """{"message":"Up and healthy."}""".stripMargin
      }
    }
  }
}