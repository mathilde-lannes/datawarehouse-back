package ice.master.datawarehouse

import org.mongodb.scala._

class Database {
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("test")

  def testDatabase(): SingleObservable[Seq[Document]] = {
    val collection: MongoCollection[Document] = database.getCollection("test")

    // make a document and insert it
    val doc: Document = Document("_id" -> 0, "name" -> "MongoDB", "type" -> "database",
      "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

    val insertObservable: Observable[Completed] = collection.insertOne(doc)

    insertObservable.subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println(s"onNext: $result")
      override def onError(e: Throwable): Unit = println(s"onError: $e")
      override def onComplete(): Unit = println("onComplete")
    })

    // find documents
    collection.find().collect()

  }

  def closeConnection() : Unit = {
    mongoClient.close()
  }
}
