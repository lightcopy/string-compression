package com.github.sadikovi

class BenchmarkSuite extends UnitTestSuite {
  /** Rounds the double value */
  private def round(value: Double, precision: Int): Double = {
    if (precision <= 0) {
      value.toInt
    } else {
      val denom = math.pow(10, precision).toDouble
      (value * denom).toInt / denom
    }
  }

  /** Returns duration in milliseconds */
  def measure(func: => Unit): Double = {
    val start = System.nanoTime
    func
    val end = System.nanoTime
    round((end - start) / 1e6, 3)
  }

  /** Run the benchmark */
  def bench(name: String, iterations: Int = 10)(func: => Unit): Unit = {
    test(name) {
      if (iterations > 0) {
        val measurements = (0 until iterations).map { i =>
          measure(func)
        }

        val sortedValues = measurements.sortWith(_ < _)
        val min = sortedValues.head
        val median = sortedValues(sortedValues.size / 2)
        val max = sortedValues.last

        println(s"""Benchmark "$name":
          | Min (ms): $min
          | Med (ms): $median
          | Max (ms): $max
          """.stripMargin)
      } else {
        println(s"""Benchmark "$name":
          | No iterations were provided!
          """.stripMargin)
      }
    }
  }

  bench("sort 100000 values") {
    (0 until 100000).sortWith(_ > _)
  }
}
