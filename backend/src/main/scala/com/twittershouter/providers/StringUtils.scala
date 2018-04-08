package com.twittershouter.providers

class StringUtils {

  def formatTwitterTweetsUrl(url: String, userName: String, numberOfTweets: Int): String =
    url format (userName, numberOfTweets)

}
