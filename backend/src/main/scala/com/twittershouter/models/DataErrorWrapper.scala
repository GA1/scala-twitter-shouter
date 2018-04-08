package com.twittershouter.models

case class DataErrorWrapper[T](
  data: Option[T],
  error: Option[String]
)