package com.github.sadikovi

import java.io._

class BurrowsWheelerRleEncoderSuite extends BenchmarkSuite {
  test("small test") {
    val bw = new BurrowsWheelerRleEncoder()
    val out = new ByteArrayOutputStream()

    // val s = "ABRACADABRA!"
    // val s = "ABCDAASDFWESDF"
    val s = scala.io.Source.fromFile("plan.json").getLines.mkString

    bw.encode(s.getBytes, out)

    // println("ORG: " + java.util.Arrays.toString(pad(s.getBytes)))
    // println("CMP: " + java.util.Arrays.toString(out.toByteArray))
    println("UNQ: " + unique(s.getBytes))
    println("ORG: " + pad(s.getBytes).length)
    println("CMP: " + out.toByteArray.length)
    println("BST: " + rle(s.getBytes).length)

    // var i = 0
    // val b = out.toByteArray
    // println("ENC:")
    // while (i < b.length) {
    //   if (b(i) < 0) {
    //     val cnt = b(i) & 0x7f
    //     i += 1
    //     print(b(i).toChar)
    //     print(cnt)
    //   } else {
    //     print(b(i).toChar)
    //   }
    //   i += 1
    // }
    // println()

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

  def unique(arr: Array[Byte]): Int = {
    val r = new Array[Boolean](256)
    for (a <- arr) {
      r(a) = true;
    }

    var cnt = 0
    for (e <- r) {
      if (e) cnt += 1;
    }

    cnt
  }

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

  private val input3 = """[{"_id":"5f22c9a18223ffe7ee590e4b","index":0,"guid":"24971bcb-39d2-4360-9b2b-3f529d842678","isActive":false,"balance":"$3,165.83","picture":"http://placehold.it/32x32","age":29,"eyeColor":"green","name":"Hurst Austin","gender":"male","company":"ZENTURY","email":"hurstaustin@zentury.com","phone":"+1 (879) 443-2366","address":"517 Stuart Street, Lund, Alaska, 5342","about":"Cillum nostrud adipisicing duis elit eu irure tempor sint enim ea cupidatat. Nostrud occaecat est Lorem mollit irure sint non sint voluptate dolore eu reprehenderit. Occaecat qui laboris duis est nisi et laboris.\r\n","registered":"2020-05-13T09:10:43 -02:00","latitude":-26.779965,"longitude":-44.645958,"tags":["sit","incididunt","cillum","magna","nisi","ex","fugiat"],"friends":[{"id":0,"name":"Boyd Leach"},{"id":1,"name":"Poole Ochoa"},{"id":2,"name":"Cash Swanson"}],"greeting":"Hello, Hurst Austin! You have 6 unread messages.","favoriteFruit":"strawberry"},{"_id":"5f22c9a190c3ff9025d04eac","index":1,"guid":"344da6a1-cdbb-4795-8f6c-3dfb87a27e83","isActive":true,"balance":"$3,694.15","picture":"http://placehold.it/32x32","age":37,"eyeColor":"brown","name":"Irwin Sawyer","gender":"male","company":"GEEKUS","email":"irwinsawyer@geekus.com","phone":"+1 (847) 534-3048","address":"359 Coffey Street, Soudan, Utah, 6670","about":"Aute elit cupidatat voluptate nostrud cupidatat id non id aliquip irure cupidatat. Officia cillum exercitation aute cillum enim laborum ipsum nisi non quis consectetur dolore. Officia exercitation ullamco nisi nisi mollit tempor. Mollit non labore qui est anim ut sunt occaecat sunt tempor velit et mollit veniam. Excepteur qui reprehenderit incididunt sit culpa ea in in incididunt nulla.\r\n","registered":"2015-02-23T07:48:05 -01:00","latitude":-15.946441,"longitude":40.871022,"tags":["cillum","sint","aute","culpa","cupidatat","amet","laborum"],"friends":[{"id":0,"name":"Neva Bruce"},{"id":1,"name":"Concepcion Dominguez"},{"id":2,"name":"Irene Moody"}],"greeting":"Hello, Irwin Sawyer! You have 3 unread messages.","favoriteFruit":"strawberry"},{"_id":"5f22c9a1b2a2e7c6446f6f31","index":2,"guid":"0feb9a52-efb7-41ae-a2d0-51ec67ea1907","isActive":true,"balance":"$1,845.19","picture":"http://placehold.it/32x32","age":26,"eyeColor":"blue","name":"Atkins Patrick","gender":"male","company":"INCUBUS","email":"atkinspatrick@incubus.com","phone":"+1 (829) 454-2295","address":"416 Stockholm Street, Kenwood, Iowa, 6524","about":"Duis labore elit irure Lorem. Minim enim qui cupidatat eiusmod ut commodo aute sunt Lorem veniam culpa et sit. Proident nisi anim commodo labore.\r\n","registered":"2016-09-25T04:51:37 -02:00","latitude":-3.392533,"longitude":149.809227,"tags":["non","sint","elit","adipisicing","est","adipisicing","reprehenderit"],"friends":[{"id":0,"name":"Sheree Paul"},{"id":1,"name":"Leanna Rogers"},{"id":2,"name":"Alissa Clayton"}],"greeting":"Hello, Atkins Patrick! You have 3 unread messages.","favoriteFruit":"banana"}]"""

  private val input4 = """[{"_id":"5f22c9ef7d7733623e2ea7f8","index":0,"guid":"2de54b22-451c-4182-aa6f-624b388dcc82","isActive":false,"balance":"$3,002.12","picture":"http://placehold.it/32x32","age":34,"eyeColor":"brown","name":"Alvarado Dillard","gender":"male","company":"TELEPARK","email":"alvaradodillard@telepark.com","phone":"+1 (951) 510-2631","address":"413 Regent Place, Brewster, Alabama, 1388","about":"Labore et proident nulla id ut eiusmod. Irure dolore pariatur ut do anim. Minim cupidatat aute voluptate voluptate irure commodo sint labore pariatur. Ea officia irure ad sint aute laboris id. Deserunt fugiat quis ex ullamco esse commodo esse. Ullamco nulla culpa fugiat aute magna.\r\n","registered":"2017-03-11T09:46:32 -01:00","latitude":-19.663667,"longitude":-120.144675,"tags":["eu","irure","et","quis","et","laboris","duis"],"friends":[{"id":0,"name":"Bentley Donaldson"},{"id":1,"name":"Watts Lloyd"},{"id":2,"name":"Terry Bowen"}],"greeting":"Hello, Alvarado Dillard! You have 9 unread messages.","favoriteFruit":"apple"}]"""

  private val input5 = """[{"_id":"5f22ddc5d68d5b6c55107982","index":0,"guid":"d5a5eeaf-7b4d-4228-9277-bde327424efb","isActive":false,"balance":"$1,757.97","picture":"http://placehold.it/32x32","age":22,"eyeColor":"green","name":"Flora Lane","gender":"female","company":"ZAGGLE","email":"floralane@zaggle.com","phone":"+1 (942) 489-2377","address":"621 Poplar Street, Sheatown, North Dakota, 1471","about":"Tempor proident dolore ex in et eiusmod est reprehenderit. Cupidatat veniam pariatur dolor proident. Commodo proident velit proident tempor dolore esse id ullamco officia cupidatat. Nisi qui laborum anim irure ut dolore est qui aliqua excepteur. Incididunt qui nisi amet irure veniam ex veniam Lorem aute dolor. Exercitation Lorem amet sunt commodo consequat excepteur dolor deserunt proident ad veniam labore.\r\nDolor nisi adipisicing consequat voluptate reprehenderit aliquip Lorem. Sunt in qui aliquip tempor anim deserunt reprehenderit sint est. Anim nisi ea anim non sint et ea adipisicing dolor ex.\r\n","registered":"2018-09-27T07:34:49 -02:00","latitude":16.648949,"longitude":-98.253004,"tags":["sit","nisi","sit","exercitation","consequat","deserunt","aliqua"],"friends":[{"id":0,"name":"Antonia Payne"},{"id":1,"name":"Casey Christensen"},{"id":2,"name":"Felecia Hayden"}],"greeting":"Hello, Flora Lane! You have 8 unread messages.","favoriteFruit":"apple"},{"_id":"5f22ddc56a9ff69c3abe1bee","index":1,"guid":"a984bf41-8762-4841-8408-1229325ddbb9","isActive":false,"balance":"$2,462.67","picture":"http://placehold.it/32x32","age":33,"eyeColor":"green","name":"Young Kelley","gender":"female","company":"FISHLAND","email":"youngkelley@fishland.com","phone":"+1 (924) 538-3942","address":"362 Battery Avenue, Welch, North Carolina, 5682","about":"Anim laborum magna culpa ex sit. Adipisicing nostrud excepteur labore Lorem fugiat anim quis proident ut sunt ipsum officia elit. Qui proident velit proident ut veniam anim do sit exercitation nulla amet cillum proident.\r\nMinim sunt quis aliqua excepteur cupidatat Lorem voluptate. Elit exercitation sint do consectetur. Aliquip ut dolor pariatur aliqua deserunt ut irure velit ipsum sunt fugiat laborum commodo.\r\n","registered":"2016-02-11T12:03:34 -01:00","latitude":40.812402,"longitude":-34.292255,"tags":["aliquip","dolore","ut","tempor","sit","officia","magna"],"friends":[{"id":0,"name":"Kinney Shaw"},{"id":1,"name":"Fay Holt"},{"id":2,"name":"Christi Patton"}],"greeting":"Hello, Young Kelley! You have 4 unread messages.","favoriteFruit":"apple"},{"_id":"5f22ddc55a3a5947f2ab8248","index":2,"guid":"52b9b50a-304f-4084-9617-a044ee2c444c","isActive":false,"balance":"$3,520.89","picture":"http://placehold.it/32x32","age":22,"eyeColor":"blue","name":"Young Joyner","gender":"male","company":"NIMON","email":"youngjoyner@nimon.com","phone":"+1 (969) 479-2268","address":"193 Jay Street, Clinton, California, 2657","about":"Ad deserunt minim occaecat mollit pariatur eiusmod laborum nulla enim. Et magna aliqua velit Lorem ad duis nisi enim. Magna eiusmod velit consequat dolore voluptate cillum ea sit veniam voluptate ea occaecat. Sit occaecat anim anim ea consequat nisi ullamco tempor est fugiat elit. Magna enim magna dolore non consectetur magna consectetur laboris id consectetur. Aute aliqua elit sit eu velit eiusmod sint nostrud fugiat incididunt voluptate non culpa. Adipisicing dolore nulla adipisicing quis commodo.\r\nIpsum in magna sunt ea aliqua et ullamco nostrud cupidatat quis non occaecat. Occaecat velit veniam est ad. Anim aliqua excepteur reprehenderit anim est aute.\r\n","registered":"2018-12-25T12:09:48 -01:00","latitude":23.079861,"longitude":-9.158946,"tags":["enim","eiusmod","qui","nostrud","dolore","cupidatat","officia"],"friends":[{"id":0,"name":"Grant Cline"},{"id":1,"name":"Geneva Bolton"},{"id":2,"name":"Gutierrez Church"}],"greeting":"Hello, Young Joyner! You have 1 unread messages.","favoriteFruit":"strawberry"},{"_id":"5f22ddc589225030fba08d85","index":3,"guid":"05d9e857-952c-40b1-ae39-27f4a21f0e2d","isActive":false,"balance":"$3,100.57","picture":"http://placehold.it/32x32","age":27,"eyeColor":"blue","name":"Deanna Russo","gender":"female","company":"GEEKKO","email":"deannarusso@geekko.com","phone":"+1 (968) 418-3332","address":"949 Delevan Street, Denio, South Dakota, 5719","about":"Nisi irure id irure qui tempor id pariatur. Laboris in magna pariatur occaecat incididunt ad est nulla ea. Magna aute quis Lorem Lorem ea consequat deserunt exercitation irure consequat nisi excepteur. Incididunt fugiat commodo reprehenderit ad ut anim id nisi. Eu dolore officia irure sunt dolore nisi eu tempor. Elit consectetur cillum eu minim ut laboris. Laborum sint incididunt consequat mollit commodo cupidatat elit incididunt eu officia.\r\nMollit irure exercitation veniam proident. Enim in minim proident veniam. Esse sit ullamco adipisicing aliquip nostrud culpa excepteur qui voluptate consectetur nostrud excepteur adipisicing labore.\r\n","registered":"2019-06-27T09:16:36 -02:00","latitude":48.991352,"longitude":-26.243571,"tags":["aute","tempor","enim","irure","voluptate","enim","cupidatat"],"friends":[{"id":0,"name":"Brenda Horne"},{"id":1,"name":"Vega Randolph"},{"id":2,"name":"Meyers Leblanc"}],"greeting":"Hello, Deanna Russo! You have 9 unread messages.","favoriteFruit":"apple"},{"_id":"5f22ddc5af7f850288359043","index":4,"guid":"b8131a4d-2823-4eb3-8d2e-66b3712e3cb9","isActive":false,"balance":"$2,862.51","picture":"http://placehold.it/32x32","age":36,"eyeColor":"blue","name":"Boyer Solomon","gender":"male","company":"XYLAR","email":"boyersolomon@xylar.com","phone":"+1 (839) 572-2973","address":"733 Cumberland Walk, Caron, New Mexico, 8922","about":"Nostrud qui amet laborum aute dolor cillum velit. Adipisicing consequat veniam consequat qui reprehenderit occaecat officia occaecat consequat elit. Commodo amet tempor culpa cupidatat eu sunt exercitation voluptate proident mollit. Officia tempor adipisicing ut culpa Lorem velit cupidatat deserunt. Velit cillum quis commodo reprehenderit laborum consectetur voluptate dolor deserunt adipisicing ut aute. Incididunt eiusmod sit irure eiusmod ex mollit proident nostrud culpa aute.\r\nAd ullamco et velit occaecat ea officia. Amet ea do labore esse tempor et nulla ut consequat incididunt. Pariatur veniam exercitation nostrud minim commodo ad.\r\n","registered":"2020-07-02T04:41:03 -02:00","latitude":36.363315,"longitude":54.644189,"tags":["ipsum","amet","incididunt","deserunt","aliqua","nostrud","nulla"],"friends":[{"id":0,"name":"Aurelia Mclaughlin"},{"id":1,"name":"Lilian Harding"},{"id":2,"name":"Davidson Atkinson"}],"greeting":"Hello, Boyer Solomon! You have 8 unread messages.","favoriteFruit":"apple"},{"_id":"5f22ddc52ee8cd348be43c26","index":5,"guid":"d16b93b0-3373-4eb6-a5d8-ad76f7a54f4a","isActive":false,"balance":"$3,914.34","picture":"http://placehold.it/32x32","age":32,"eyeColor":"brown","name":"Roberson Rosales","gender":"male","company":"BOLAX","email":"robersonrosales@bolax.com","phone":"+1 (891) 451-2383","address":"218 Tabor Court, Chase, Georgia, 137","about":"Anim Lorem voluptate fugiat do laboris commodo id consectetur adipisicing occaecat exercitation. Anim ad laborum est aliquip anim magna tempor magna incididunt ut tempor do laboris incididunt. Enim exercitation velit reprehenderit excepteur culpa veniam.\r\nOfficia ullamco do tempor ullamco. Laborum culpa et voluptate ea. Sit ut cillum excepteur non sunt. Et pariatur labore fugiat nulla velit. Qui velit ex pariatur adipisicing ipsum exercitation reprehenderit ut minim sunt mollit. Nulla ea id dolore non. Pariatur et ex eu velit commodo eu reprehenderit magna amet mollit eu.\r\n","registered":"2015-03-28T08:52:37 -01:00","latitude":-44.00756,"longitude":-101.553401,"tags":["esse","sit","commodo","laborum","in","culpa","in"],"friends":[{"id":0,"name":"Ida Moody"},{"id":1,"name":"Abby Crane"},{"id":2,"name":"Glass Jennings"}],"greeting":"Hello, Roberson Rosales! You have 7 unread messages.","favoriteFruit":"banana"},{"_id":"5f22ddc5b59273c777fc6651","index":6,"guid":"9286cfc4-e469-4aa9-9ce5-0b73f2125af9","isActive":false,"balance":"$1,317.71","picture":"http://placehold.it/32x32","age":26,"eyeColor":"brown","name":"Ford Bailey","gender":"male","company":"EXODOC","email":"fordbailey@exodoc.com","phone":"+1 (862) 596-3650","address":"850 Albemarle Terrace, Hobucken, Puerto Rico, 8410","about":"Cillum consectetur qui aliqua nostrud laborum fugiat sunt laborum. Reprehenderit cillum nostrud qui proident. Nostrud magna sit proident nostrud reprehenderit incididunt occaecat deserunt laboris. Tempor commodo excepteur aliqua fugiat excepteur elit est officia esse.\r\nSint amet ullamco officia commodo qui fugiat id pariatur pariatur Lorem ad. Sunt esse veniam anim ea qui deserunt occaecat reprehenderit non deserunt magna ipsum. Mollit tempor eu eiusmod dolor. Culpa cillum non Lorem irure minim sit incididunt veniam pariatur dolor Lorem sint proident. Aliqua aute in ullamco ut enim minim officia eu commodo esse.\r\n","registered":"2019-10-17T10:49:04 -02:00","latitude":39.767847,"longitude":71.200715,"tags":["est","ad","velit","velit","occaecat","Lorem","dolore"],"friends":[{"id":0,"name":"Herring Gallegos"},{"id":1,"name":"Lillie Hickman"},{"id":2,"name":"Holder Oneal"}],"greeting":"Hello, Ford Bailey! You have 2 unread messages.","favoriteFruit":"banana"}]"""

  private val input6 = """{"opId": "Driver-cf6237ea21b2f84d", "opTarget": "fetchPartialResult", "projectName": "driver", "tier": "tier-0", "buildHash": "df051b1e95b9486bc8fe3c5873e3fef772813835", "databricksCommit": "c05580c5137b32994594d54e3deae9b6b739762a", "sparkVersion": "2.2.0", "hostName": "1118-023520-divot1_10_0_170_149", "apacheCommit": "99ce551a13f0918b440ddc094c3a32167d7ab3dd", "sqlFuncName": "head", "rootOpId": "SingletonRunTask-cf6237ea21b2f848", "executorName": "Partial Result Fetchers", "status": "success", "branchName": "development", "opType": "Driver", "parentOpId": "SingletonRunTask-cf6237ea21b2f848"}"""

  private val input7 = "\"{\"\"logicalPlan\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.GlobalLimit\"\",\"\"num-children\"\":1,\"\"limitExpr\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.Literal\"\",\"\"num-children\"\":0,\"\"value\"\":\"\"1001\"\",\"\"dataType\"\":\"\"integer\"\"}],\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.LocalLimit\"\",\"\"num-children\"\":1,\"\"limitExpr\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.Literal\"\",\"\"num-children\"\":0,\"\"value\"\":\"\"1001\"\",\"\"dataType\"\":\"\"integer\"\"}],\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.SubqueryAlias\"\",\"\"num-children\"\":1,\"\"alias\"\":\"\"display_query_8\"\",\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.execution.streaming.MemoryPlan\"\",\"\"num-children\"\":0,\"\"sink\"\":null,\"\"output\"\":[[{\"\"nullable\"\":true,\"\"name\"\":\"\"time\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1500,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":true,\"\"name\"\":\"\"sparkVersion\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1501,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":false,\"\"name\"\":\"\"count\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"long\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1502,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}]]}],\"\"analyzedPlan\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.GlobalLimit\"\",\"\"num-children\"\":1,\"\"limitExpr\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.Literal\"\",\"\"num-children\"\":0,\"\"value\"\":\"\"1001\"\",\"\"dataType\"\":\"\"integer\"\"}],\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.LocalLimit\"\",\"\"num-children\"\":1,\"\"limitExpr\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.Literal\"\",\"\"num-children\"\":0,\"\"value\"\":\"\"1001\"\",\"\"dataType\"\":\"\"integer\"\"}],\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.SubqueryAlias\"\",\"\"num-children\"\":1,\"\"alias\"\":\"\"display_query_8\"\",\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.execution.streaming.MemoryPlan\"\",\"\"num-children\"\":0,\"\"sink\"\":null,\"\"output\"\":[[{\"\"nullable\"\":true,\"\"name\"\":\"\"time\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1500,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":true,\"\"name\"\":\"\"sparkVersion\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1501,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":false,\"\"name\"\":\"\"count\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"long\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1502,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}]]}],\"\"optimizedPlan\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.GlobalLimit\"\",\"\"num-children\"\":1,\"\"limitExpr\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.Literal\"\",\"\"num-children\"\":0,\"\"value\"\":\"\"1001\"\",\"\"dataType\"\":\"\"integer\"\"}],\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.plans.logical.LocalLimit\"\",\"\"num-children\"\":1,\"\"limitExpr\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.Literal\"\",\"\"num-children\"\":0,\"\"value\"\":\"\"1001\"\",\"\"dataType\"\":\"\"integer\"\"}],\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.execution.streaming.MemoryPlan\"\",\"\"num-children\"\":0,\"\"sink\"\":null,\"\"output\"\":[[{\"\"nullable\"\":true,\"\"name\"\":\"\"time\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1500,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":true,\"\"name\"\":\"\"sparkVersion\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1501,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":false,\"\"name\"\":\"\"count\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"long\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1502,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}]]}],\"\"executedPlan\"\":[{\"\"class\"\":\"\"org.apache.spark.sql.execution.CollectLimitExec\"\",\"\"num-children\"\":1,\"\"limit\"\":1001,\"\"child\"\":0},{\"\"class\"\":\"\"org.apache.spark.sql.execution.LocalTableScanExec\"\",\"\"num-children\"\":0,\"\"output\"\":[[{\"\"nullable\"\":true,\"\"name\"\":\"\"time\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1500,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":true,\"\"name\"\":\"\"sparkVersion\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"string\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1501,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}],[{\"\"nullable\"\":false,\"\"name\"\":\"\"count\"\",\"\"num-children\"\":0,\"\"metadata\"\":{},\"\"class\"\":\"\"org.apache.spark.sql.catalyst.expressions.AttributeReference\"\",\"\"dataType\"\":\"\"long\"\",\"\"exprId\"\":{\"\"product-class\"\":\"\"org.apache.spark.sql.catalyst.expressions.ExprId\"\",\"\"id\"\":1502,\"\"jvmId\"\":\"\"b667fa5d-29a2-47a0-8c23-a5f449558984\"\"}}]],\"\"rows\"\":[]}],\"\"exception\"\":\"\"\"\"}\""
}
