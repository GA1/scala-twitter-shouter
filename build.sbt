scalaVersion := "2.12.4"

val wmVersion = "0.4.27"

libraryDependencies ++={
  val akkaV = "2.5.11"
  val akkaHttpV = "10.1.0"
  Seq(
    "com.typesafe.akka"              %%  "akka-actor"                              % akkaV,
    "com.typesafe.akka"              %%  "akka-http-spray-json"                    % akkaHttpV,
    "com.typesafe.akka"              %%  "akka-http-xml"                           % akkaHttpV,
    "com.typesafe.akka"              %%  "akka-http-testkit"                       % akkaHttpV,
    "org.scalatest"                  %%  "scalatest"                               % "3.0.0" % "test"
  )
}

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
