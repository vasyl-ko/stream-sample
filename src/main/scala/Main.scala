import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.collection.convert.ImplicitConversionsToScala._


object Main {

  def main(args: Array[String]): Unit = {
    implicit val sys = ActorSystem("akka-stream-patterns")
    implicit val mat = ActorMaterializer()

    val jsonRegExp = "([^\\{]*)(\\{.*\\})".r

    def fileParser(file: java.io.File): Future[List[String]] =
      Source
        .fromIterator(() => io.Source.fromFile(file, "utf-8").iter)
        .runFold((Nil: List[String], "")) { case ((resList, buffer), char) =>
          buffer match {
            case jsonRegExp(_, jsonString) =>
              (resList :+ jsonString, "")
            case _ => (resList, if (char.isControl) buffer else buffer + char)
          }
        }
        .map { case (resultList, _) =>
          println(s"in ${file.getName} found ${resultList.size}")
          resultList
        }


    val future = Source
      .fromIterator(() => new java.io.File("C:\\Users\\vasyl-ko\\Desktop\\projects\\stream-sample\\src\\main\\resources\\data").listFiles().toIterator)
      .filter(_.isFile)
      .filter(! _.getName.contains("visits"))
      .mapAsync(2)(fileParser)
      .runWith(Sink.seq)
      .andThen { case _ =>
        Source
          .fromIterator(() => new java.io.File("C:\\Users\\vasyl-ko\\Desktop\\projects\\stream-sample\\src\\main\\resources\\data").listFiles().toIterator)
          .filter(_.isFile)
          .filter(_.getName.contains("visits"))
          .mapAsync(2)(fileParser)
          .runWith(Sink.seq)
      }





    //      .groupedWithin(10, 100.millis)
    //      .map { batch =>
    //        println(s"Processing batch of ${batch.size} elements")
    //        batch
    //      }

    //      .runWith(Sink.ignore).andThen {
    //      case _ => sys.terminate()
    //    }

    def isJson(string: String): Boolean = string.matches("\\{(.*)\\}")

    println(Await.result(future.map(_.size), Duration.Inf))

    sys.terminate()
  }

}
