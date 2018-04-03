package com.twittershouter.business

import com.twittershouter.model.Tweet

class TweetShoutConverter {

  def toShoutedTweet(tweet: Tweet) = Tweet(tweet.text.toUpperCase() + (if (tweet.text.endsWith("!")) "" else "!"))
}
