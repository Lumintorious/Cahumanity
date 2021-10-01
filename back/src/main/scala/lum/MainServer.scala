package lum

import cats.effect.*
import lum.cah.data.Card
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import concurrent.ExecutionContext.Implicits.global
import org.http4s.Response
import fs2.text
import org.http4s._, org.http4s.dsl.io._, org.http4s.implicits._
import org.http4s.server.Router
import lum.endpoint.*
import io.circe.Codec
import org.http4s.blaze.client.BlazeClientBuilder
import cats.syntax.all.*
import org.http4s.client.Client
import cats.effect.std.Console
import org.http4s.blaze.client.BlazeClient
import org.http4s.Uri.Host
import org.http4s.Uri.Ipv4Address
import org.http4s.Uri.RegName
import org.log4s.Logger

case class Person(name: String, age: Int) derives Codec.AsObject

val happyBirthday = Endpoint[Person, String]("happyBirthday")

object MainServer extends IOApp.Simple:
  given Resource[IO, Client[IO]] = BlazeClientBuilder[IO](global).resource
  given Host = RegName("http://localhost:80/")

  def birthdayMessage(person: Person) =
    "Happy birthday, %s on your %s birthday!" format (person.name, person.age)

  val happyBirthdayRoute = happyBirthday.mapping(birthdayMessage)

  def happyBirthdayCli =
    for
      name    <- Console[IO].readLine
      age     <- Console[IO].readLine.map(_.toInt)
      person   = Person(name, age)
      message <- happyBirthday.call(person).map(println)
    yield
      message

  def run =
    BlazeServerBuilder[IO](global)
      .bindHttp(80, "localhost")
      .withHttpApp((happyBirthdayRoute).orNotFound)
      .resource
      .use { _ => happyBirthdayCli }
      .as(())
  