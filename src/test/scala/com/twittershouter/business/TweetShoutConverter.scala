package com.twittershouter

import com.twittershouter.business.TweetShoutConverter
import com.twittershouter.models.Tweet
import org.scalatest.FlatSpecLike

class TweetShoutConverterTest extends FlatSpecLike {

  private val testSubject = new TweetShoutConverter()

  it should "Should correctly convert a not shouted tweet to a shouted tweet" in {
    val tweet = new Tweet("Some text")
    val result = testSubject.toShoutedTweet(tweet)
    val expected = new Tweet("SOME TEXT!")
    assert(result == expected)
  }

  it should "not add exclamation mark if there is one already at the end of the tweet's text" in {
    val tweet = new Tweet("A tweet!")
    val result = testSubject.toShoutedTweet(tweet)
    val expected = new Tweet("A TWEET!")
    assert(result == expected)
  }

  it should "return the same tweet if it is already shouted" in {
    val tweet = new Tweet("A SHOUTED TWEET!")
    val result = testSubject.toShoutedTweet(tweet)
    val expected = new Tweet("A SHOUTED TWEET!")
    assert(result == expected)
  }

}