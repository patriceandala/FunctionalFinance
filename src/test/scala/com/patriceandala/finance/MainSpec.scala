package com.patriceandala.finance

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyFlatSpec with Matchers {
  "Sample test" should "verify a simple assertion" in {
    1 + 1 shouldEqual 2
  }
}