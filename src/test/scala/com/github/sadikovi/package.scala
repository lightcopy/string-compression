package com.github.sadikovi

import org.scalatest._

abstract class UnitTestSuite extends FunSuite
  with Matchers
  with BeforeAndAfterAll
  with BeforeAndAfter
