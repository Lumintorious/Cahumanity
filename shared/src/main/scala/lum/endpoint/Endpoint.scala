package lum.endpoint

import cats.effect.IO
import io.circe.Encoder
import io.circe.Decoder
import io.circe.Json
import org.http4s.Method
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.Request
import org.http4s.EntityBody
import fs2.Stream
import fs2.text
import org.http4s.Charset
import java.nio.charset.StandardCharsets
import org.http4s.HttpRoutes
import cats.effect.kernel.Resource

object Endpoint:
  given Charset = Charset(StandardCharsets.UTF_8)

  def callApi(path: String, method: Method, host: String = "/")(json: Json)(using client: Resource[IO, Client[IO]]): IO[Json] =
    val req = Request[IO](
      method = method,
      uri = Uri.fromString(host + path).getOrElse(throw Exception("URI INVALID")),
      body = Stream.emit(json.toString).through(text.utf8Encode)
    )

    client.use { cl =>
      cl.run(req).use { resp => resp.bodyText.compile.toList.map(v => io.circe.parser.parse(v.head).getOrElse(throw ???)) }
    }
    
final case class Endpoint[In: Decoder: Encoder, Out: Decoder: Encoder](path: String, method: Method = Method.GET):
  val inputDecoder  = summon[Decoder[In]]
  val inputEncoder  = summon[Encoder[In]]
  val outputDecoder = summon[Decoder[Out]]
  val outputEncoder = summon[Encoder[Out]]

  def call(in: In)(using res: Resource[IO, Client[IO]], host: Uri.Host): IO[Out] = IO.defer {
    val jsonIn = Encoder[In].apply(in)
    for jsonOut <- Endpoint.callApi(path, method, host.renderString)(jsonIn) yield
      Decoder[Out].decodeJson(jsonOut) match
        case Left(err) => throw Exception(err.message)
        case Right(ok) => ok
  }