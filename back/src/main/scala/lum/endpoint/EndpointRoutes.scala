package lum.endpoint

import org.http4s.HttpRoutes
import cats.effect.IO
import org.http4s.dsl.Http4sDsl
import io.circe.parser.parse
import io.circe.Json
import org.http4s.Response
import org.http4s.Status
import fs2.text
import scala.annotation.targetName

extension [In, Out] (self: Endpoint[In, Out])
  @targetName("implementedWithPure")
  def mapping(impl: In => Out): HttpRoutes[IO] =
    EndpointRoutes.of(self, i => IO { impl(i)})

  def implementedWith(impl: In => IO[Out]): HttpRoutes[IO] =
    EndpointRoutes.of(self, impl)

object EndpointRoutes:
  val dsl = Http4sDsl[IO]
  import dsl.*

  def respFromErr(e: Throwable) = 
      Response[IO](status = Status.InternalServerError, body = fs2.Stream.emit(e.getMessage().nn).through(text.utf8Encode))
        

  def of[In, Out](endpoint: Endpoint[In, Out], implementation: In => IO[Out]) =
    HttpRoutes.of[IO] {
      case req @ (endpoint.method -> Root / endpoint.path) =>
        try
          val json: IO[Json] = req.bodyText.compile.toList.map(v => parse(v.head).getOrElse(throw Exception("???")))
          val input = json.map(endpoint.inputDecoder.decodeJson(_).getOrElse(throw Exception("???")))
          val output = input.flatMap(implementation)
          val outJson = output.map(endpoint.outputEncoder.apply)
          val outString = outJson.map(_.toString)
          val body = fs2.Stream.eval(outString).through(text.utf8Encode)
          IO { 
            try
              Response[IO](status = Status.Ok, body = body)
            catch respFromErr
          }
        catch case e: Throwable => {
          IO { respFromErr(e) }
        }
    }
