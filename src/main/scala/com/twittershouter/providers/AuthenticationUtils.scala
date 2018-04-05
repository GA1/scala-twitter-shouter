package com.twittershouter.providers

class AuthenticationUtils {

  def encode64(toEncode: String): String = {
    val byteArray = java.util.Base64.getEncoder.encode(toEncode.getBytes())
    new String(byteArray)
  }

  def encodeTwitterCredentials(twitterConsumerKey: String, twitterConsumerSecret: String) =
    encode64(twitterConsumerKey + ":" + twitterConsumerSecret)
}
