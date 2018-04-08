package com.twittershouter.providers

import org.scalatest.FlatSpecLike

class AuthenticationUtilsTest extends FlatSpecLike {

  private val testSubject = new AuthenticationUtils()

  it should "correctly encode64 'user:pass' into 'dXNlcjpwYXNz'" in {
    assert(testSubject.encode64("user:pass") == "dXNlcjpwYXNz")
  }

  it should "correctly encodeTwitterCredentials 'a' and 'b'" in {
    assert(testSubject.encodeTwitterCredentials("a", "b") == "YTpi")
  }


}