import java.time.Instant

import spray.json._

package object Model {
  case class User(id: Long,
                  email: String,
                  firstName: String,
                  lastName: String,
                  gender: Char,
                  birthDate: Instant)
  case class Location(id: Long,
                      place: String,
                      country: String,
                      city: String,
                      distance: Int)
  case class Visit(id: Long,
                   location: Long,
                   user: Long,
                   visitedAt: Instant,
                   mark: Int)

  private implicit object EpochDateTimeFormat extends RootJsonFormat[Instant] {
    override def write(obj: Instant) = JsNumber(obj.getEpochSecond)
    override def read(json: JsValue) : Instant = json match {
      case JsNumber(s) => Instant.ofEpochSecond(s.toLongExact)
      case _ => throw DeserializationException("Error in Instant parsing")
    }
  }

  object TravelsProtocol extends DefaultJsonProtocol {
    implicit val userFormat: RootJsonFormat[User] = jsonFormat6(User)
    implicit val locationFormat: RootJsonFormat[Location] = jsonFormat5(Location)
    implicit val visitFormat: RootJsonFormat[Visit] = jsonFormat5(Visit)
  }

}
