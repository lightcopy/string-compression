package com.github.sadikovi

class CircularSuffixArraySuite extends BenchmarkSuite {
  test("test") {
    // new CircularSuffixArray("ABCDAASDFWESDF")
    new CircularSuffixArray("ABRACADABRA!")
    // new CircularSuffixArray("19/02/20 09:33:25 WARN scheduler.TaskSetManager: Lost task 200.0 in stage 0.0 (TID xxx, t.y.z.com, executor 93): ExecutorLostFailure (executor 93 exited caused by one of the running tasks) Reason: Container killed by YARN for exceeding memory limits. 8.1 GB of 8 GB physical memory used. Consider boosting spark.yarn.executor.memoryOverhead.")
  }

  // Mergesort:
  // Benchmark "benchmark":
  // Min (ms): 158.667
  // Med (ms): 160.322
  // Max (ms): 209.8

  // 3-way quicksort:
  // Benchmark "benchmark":
  // Min (ms): 81.366
  // Med (ms): 82.195
  // Max (ms): 99.966
  private val input = "19/02/20 09:33:25 WARN scheduler.TaskSetManager: Lost task 200.0 in stage 0.0 (TID xxx, t.y.z.com, executor 93): ExecutorLostFailure (executor 93 exited caused by one of the running tasks) Reason: Container killed by YARN for exceeding memory limits. 8.1 GB of 8 GB physical memory used. Consider boosting spark.yarn.executor.memoryOverhead." * 10

  bench("benchmark", 100) {
    new CircularSuffixArray(input)
  }
}
