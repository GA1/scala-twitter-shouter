package com.twittershouter

import org.scalatest.FlatSpecLike

class UtilsTest extends FlatSpecLike {

  private val task1 = new Utils()

  it should "Should correctly return 13 when calling solve()" in {
    assert(task1.solve() == 13)
  }

}