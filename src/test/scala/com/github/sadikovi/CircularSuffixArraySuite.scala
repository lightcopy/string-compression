package com.github.sadikovi

class CircularSuffixArraySuite extends UnitTestSuite {
  test("small test") {
    // new CircularSuffixArray("ABCDAASDFWESDF")
    new CircularSuffixArray("ABRACADABRA!")
  }

  // Mergesort:
  // Benchmark "benchmark":
  // Min (ms): 337.371
  // Med (ms): 342.588
  // Max (ms): 391.029

  // 3-way quicksort:
  // Benchmark "benchmark":
  // Min (ms): 173.279
  // Med (ms): 174.528
  // Max (ms): 192.642

  // 5000 characters
  private val input = (
    "19/02/20 09:33:25 WARN scheduler.TaskSetManager: Lost task 200.0 in stage 0.0 (TID xxx, t.y.z.com, e" +
    "xecutor 93): ExecutorLostFailure (executor 93 exited caused by one of the running tasks) Reason: Con" +
    "tainer killed by YARN for exceeding memory limits. 8.1 GB of 8 GB physical memory used. Consider boo" +
    "sting spark.yarn.executor.memoryOverhead. Caused by: org.apache.hadoop.fs.FileAlreadyExistsException" +
    ": /user/hive/warehouse/tmp_supply_feb1/.spark-staging-blah-blah-blah/dt=2019-02-17/part-00200-blah-b"
  ) * 10

  // test("benchmark") {
  //   bench(100) {
  //     new CircularSuffixArray(input)
  //   }
  // }
}
