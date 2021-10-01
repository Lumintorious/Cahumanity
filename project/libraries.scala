import sbt._

object Libraries {
  lazy val shared = Seq(
    "org.typelevel" % "cats-effect_3" % "3.2.9",
    "io.circe" %% "circe-core" % "0.15.0-M1",
    "io.circe" %% "circe-generic" % "0.15.0-M1",
    "io.circe" %% "circe-parser" % "0.15.0-M1",
  )

  lazy val client = Seq(
    "org.typelevel" % "cats-effect_sjs1_3" % "3.2.9",
    "com.raquo" % "laminar_sjs1_3" % "0.13.1"
  )
  

  lazy val server = Seq(
    "org.typelevel" % "cats-effect_3" % "3.2.9",
    "org.http4s" %% "http4s-blaze-core" % "1.0.0-M23",
    "org.http4s" %% "http4s-blaze-server" % "1.0.0-M23",
    "org.http4s" %% "http4s-core" % "1.0.0-M23",
    "org.http4s" %% "http4s-dsl" % "1.0.0-M23"
  )
}