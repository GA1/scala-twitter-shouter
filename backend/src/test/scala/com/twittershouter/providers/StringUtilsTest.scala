package com.twittershouter.providers

import org.scalatest.FlatSpecLike

class StringUtilsTest extends FlatSpecLike {

  private val testSubject = new StringUtils()

  it should "correctly format twitter's tweets url with placeholders" in {
    val url = "http://www.api.twitter.com/1.1/statuses/user_timeline.json?screen_name=%1$s&count=%2$s"
    assert(testSubject.formatTwitterTweetsUrl(url, "TRUMP", 139) ==
      "http://www.api.twitter.com/1.1/statuses/user_timeline.json?screen_name=TRUMP&count=139")
  }

}