package com.github.sadikovi

import java.io._

class BurrowsWheelerRleEncoderSuite extends BenchmarkSuite {
  test("small test") {
    val bw = new BurrowsWheelerRleEncoder()
    val out = new ByteArrayOutputStream()

    // val s = "ABRACADABRA!"
    // val s = "ABCDAASDFWESDF"
    val s = input2

    bw.encode(s.getBytes, out)

    // println("ORG: " + java.util.Arrays.toString(pad(s.getBytes)))
    // println("CMP: " + java.util.Arrays.toString(out.toByteArray))
    println("ORG: " + pad(s.getBytes).length)
    println("CMP: " + out.toByteArray.length)
    println("BST: " + rle(s.getBytes).length)

    val in = new ByteArrayInputStream(out.toByteArray)
    val res = bw.decode(in)
    // println("ORG: " + java.util.Arrays.toString(s.getBytes))
    // println("RES: " + java.util.Arrays.toString(res));

    assert(s.getBytes.length === res.length)
    assert(s.getBytes === res)

    // new CircularSuffixArray("ABCDAASDFWESDF")
    // new CircularSuffixArray("ABRACADABRA!")
    // new CircularSuffixArray(input)
  }

  // bench("encode", 100) {
  //   val bw = new BurrowsWheelerRleEncoder()
  //   val out = new ByteArrayOutputStream()
  //   bw.encode(input2.getBytes, out)
  //   out.close()
  // }
  //
  // bench("decode", 100) {
  //   val bw = new BurrowsWheelerRleEncoder()
  //   val out = new ByteArrayOutputStream()
  //   bw.encode(input2.getBytes, out)
  //   out.close()
  //   val in = new ByteArrayInputStream(out.toByteArray)
  //   bw.decode(in)
  //   in.close()
  // }

  // [0, 0, 0, 0, 65, 66, 82, 65, 67, 65, 68, 65, 66, 82, 65,   33]
  // [3, 0, 0, 0, 12, 0,  0,  0,  65, 82, 68, 33, 82, 67, -124, 65, 66, 66]


  def pad(arr: Array[Byte], bytes: Int = 4): Array[Byte] = {
    val res = new Array[Byte](bytes + arr.length)
    for (i <- 0 until arr.length) {
      res(i + bytes) = arr(i)
    }
    res
  }

  def rle(a: Array[Byte]): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val value = a.sortWith(_ < _)
    var curr: Byte = -1
    var cnt = 0
    for (i <- 0 until a.length) {
      val v = value(i);
      if (v < 0) throw new IllegalArgumentException("Cannot process byte value " + v)
      if (cnt < 127 && curr == v) {
        cnt += 1
      } else {
        if (cnt == 0) {
          // do nothing
        } else if (cnt == 1) {
          out.write(curr)
        } else if (cnt == 2) {
          out.write(curr)
          out.write(curr)
        } else if (cnt == 3) {
          out.write(curr)
          out.write(curr)
          out.write(curr)
        } else {
          out.write(0x80 | cnt)
          out.write(curr)
        }

        curr = v
        cnt = 1
      }
    }

    if (cnt == 0) {
      // do nothing
    } else if (cnt == 1) {
      out.write(curr)
    } else if (cnt == 2) {
      out.write(curr)
      out.write(curr)
    } else if (cnt == 3) {
      out.write(curr)
      out.write(curr)
      out.write(curr)
    } else {
      out.write(0x80 | cnt)
      out.write(curr)
    }

    out.toByteArray
  }

  // 5000 characters
  private val input1 = (
    "19/02/20 09:33:25 WARN scheduler.TaskSetManager: Lost task 200.0 in stage 0.0 (TID xxx, t.y.z.com, e" +
    "xecutor 93): ExecutorLostFailure (executor 93 exited caused by one of the running tasks) Reason: Con" +
    "tainer killed by YARN for exceeding memory limits. 8.1 GB of 8 GB physical memory used. Consider boo" +
    "sting spark.yarn.executor.memoryOverhead. Caused by: org.apache.hadoop.fs.FileAlreadyExistsException" +
    ": /user/hive/warehouse/tmp_supply_feb1/.spark-staging-blah-blah-blah/dt=2019-02-17/part-00200-blah-b"
  ) * 10

  // 5898 characters
  private val input2 = """[{"_id":"5f22b86237f04e9cb6ba9677","index":0,"guid":"7718c95e-0cec-4a6a-b22f-147335d31ec6","isActive":true,"balance":"$2,988.96","picture":"http://placehold.it/32x32","age":34,"eyeColor":"blue","name":"Barry Horton","gender":"male","company":"ANDERSHUN","email":"barryhorton@andershun.com","phone":"+1 (995) 513-3757","address":"782 Central Avenue, Maybell, Vermont, 4584","about":"Exercitation occaecat id et et. Nisi dolor sunt fugiat pariatur anim sit officia. Mollit sint pariatur velit tempor pariatur magna. Velit minim amet do ipsum duis incididunt Lorem quis aliquip dolor eiusmod aliqua voluptate do. Et culpa esse anim nostrud commodo irure cillum ut sunt exercitation culpa eu non ad.\r\n","registered":"2017-05-23T04:34:25 -02:00","latitude":34.731435,"longitude":108.642168,"tags":["laboris","aute","mollit","proident","consequat","est","in"],"friends":[{"id":0,"name":"Navarro Wilder"},{"id":1,"name":"Beck David"},{"id":2,"name":"Leona Barton"}],"greeting":"Hello, Barry Horton! You have 2 unread messages.","favoriteFruit":"strawberry"},{"_id":"5f22b862f75294f423966883","index":1,"guid":"b963abca-d720-4936-b262-9558d2af7710","isActive":false,"balance":"$2,125.04","picture":"http://placehold.it/32x32","age":24,"eyeColor":"blue","name":"Gardner Talley","gender":"male","company":"RETRACK","email":"gardnertalley@retrack.com","phone":"+1 (890) 453-2730","address":"220 Laurel Avenue, Bayview, Louisiana, 6178","about":"Voluptate sunt culpa aliquip labore labore exercitation irure ad voluptate cillum. Ut sint labore anim cillum adipisicing laboris qui duis sint in non veniam laborum. Ex exercitation enim sunt sint cillum incididunt tempor quis dolor ad ad culpa adipisicing. Aliquip occaecat consectetur aute ullamco magna consequat irure. Minim cillum nisi tempor tempor. Labore laboris consequat tempor sint exercitation pariatur laborum reprehenderit elit proident. Minim occaecat et quis laborum ipsum aute adipisicing sit.\r\n","registered":"2020-05-07T11:51:50 -02:00","latitude":-47.165119,"longitude":52.340988,"tags":["dolor","ex","duis","ad","reprehenderit","cupidatat","ex"],"friends":[{"id":0,"name":"Catalina Oneil"},{"id":1,"name":"Dionne Murray"},{"id":2,"name":"Mosley Emerson"}],"greeting":"Hello, Gardner Talley! You have 7 unread messages.","favoriteFruit":"strawberry"},{"_id":"5f22b862a9461a5778632641","index":2,"guid":"57d8de99-dc92-4735-a7f6-9284216bb8ce","isActive":true,"balance":"$3,647.50","picture":"http://placehold.it/32x32","age":34,"eyeColor":"blue","name":"Diane Maxwell","gender":"female","company":"PROGENEX","email":"dianemaxwell@progenex.com","phone":"+1 (895) 520-3701","address":"626 Montgomery Street, Neahkahnie, Puerto Rico, 9182","about":"Fugiat dolore dolore exercitation quis veniam pariatur commodo magna proident aute enim cillum. Veniam sit duis nisi adipisicing consequat qui magna reprehenderit minim. Ex non veniam ut culpa ullamco consectetur in nulla magna exercitation voluptate est quis ullamco. Ea cillum dolore consequat velit duis sint amet. Proident aliquip eiusmod aliqua laboris sit incididunt Lorem officia adipisicing mollit dolore sint.\r\n","registered":"2015-09-22T10:59:42 -02:00","latitude":39.081999,"longitude":-139.862528,"tags":["ex","elit","nostrud","cupidatat","mollit","est","qui"],"friends":[{"id":0,"name":"Sara Vaughn"},{"id":1,"name":"Hahn Ballard"},{"id":2,"name":"Salinas Acosta"}],"greeting":"Hello, Diane Maxwell! You have 9 unread messages.","favoriteFruit":"strawberry"},{"_id":"5f22b8629c3347232d314eca","index":3,"guid":"a13bd2b5-4c6a-40e5-baf5-c49ad80392cc","isActive":false,"balance":"$2,672.10","picture":"http://placehold.it/32x32","age":27,"eyeColor":"green","name":"Hays Weeks","gender":"male","company":"DEMINIMUM","email":"haysweeks@deminimum.com","phone":"+1 (916) 588-3906","address":"421 Commercial Street, Westphalia, South Carolina, 4867","about":"Anim Lorem est qui amet et ex cupidatat tempor quis magna. Adipisicing et do proident ex elit consectetur irure duis anim eu amet mollit ut. Minim excepteur laborum et laborum sunt laborum nulla. Minim veniam occaecat magna ad commodo officia. Occaecat velit duis do ullamco.\r\n","registered":"2015-01-29T10:58:49 -01:00","latitude":-21.417018,"longitude":175.239389,"tags":["irure","voluptate","ea","occaecat","est","veniam","voluptate"],"friends":[{"id":0,"name":"Eva Irwin"},{"id":1,"name":"Jennifer Pate"},{"id":2,"name":"Reynolds Kidd"}],"greeting":"Hello, Hays Weeks! You have 5 unread messages.","favoriteFruit":"apple"},{"_id":"5f22b862aa33fbfa61542656","index":4,"guid":"61bb93bc-a972-4a11-ba47-27989e3175d0","isActive":true,"balance":"$2,985.79","picture":"http://placehold.it/32x32","age":20,"eyeColor":"green","name":"Vincent Zimmerman","gender":"male","company":"SPORTAN","email":"vincentzimmerman@sportan.com","phone":"+1 (887) 562-3116","address":"705 Tabor Court, Reinerton, Connecticut, 5112","about":"Lorem veniam officia tempor reprehenderit adipisicing deserunt duis tempor et nostrud magna elit. Labore non aliquip enim ullamco culpa aliqua id dolore esse excepteur sint excepteur consectetur. Nisi nisi adipisicing dolor labore aliquip cupidatat elit occaecat Lorem consectetur commodo aliquip irure adipisicing. Magna aliqua tempor exercitation proident eu. Aliqua in anim proident irure pariatur magna commodo do aliquip nostrud voluptate occaecat ad. Lorem qui dolore laboris elit laborum laborum proident Lorem nulla fugiat excepteur. Aute voluptate reprehenderit veniam esse ea sint laborum labore deserunt et ullamco id minim.\r\n","registered":"2019-02-26T01:49:37 -01:00","latitude":-84.022186,"longitude":55.917252,"tags":["deserunt","veniam","deserunt","amet","minim","veniam","consequat"],"friends":[{"id":0,"name":"Marquez Santiago"},{"id":1,"name":"Leslie Whitney"},{"id":2,"name":"Bobbi Hatfield"}],"greeting":"Hello, Vincent Zimmerman! You have 9 unread messages.","favoriteFruit":"banana"}]"""
}
