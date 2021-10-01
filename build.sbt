import scala.sys.process._

lazy val sass = taskKey[Unit]("Compile sass.")

sass := { "cmd sass client/src/main/scala:static" ! }

lazy val full = taskKey[Unit]("Compile Sass, Js and Jvm")

full := {
  (sass).value
  (front / Compile / compile).value
  (back / Compile / compile).value
}

ThisBuild / scalaVersion := "3.0.2"

lazy val sharedSettings = Seq(
  scalaJSUseMainModuleInitializer := true,
  scalacOptions ++= Seq(
    "-language:implicitConversions"
  ),
  libraryDependencies ++= Seq(
    "org.typelevel" % "cats-effect_3" % "3.2.9",
    "io.circe" %% "circe-core" % "0.15.0-M1",
    "io.circe" %% "circe-generic" % "0.15.0-M1",
    "io.circe" %% "circe-parser" % "0.15.0-M1",
    "org.http4s" %% "http4s-core" % "1.0.0-M23",
    "org.http4s" %% "http4s-blaze-client" % "1.0.0-M23",
  )
)

lazy val frontSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" % "cats-effect_sjs1_3" % "3.2.9",
    "com.raquo" % "laminar_sjs1_3" % "0.13.1"
  )
)

lazy val backSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" % "cats-effect_3" % "3.2.9",
    "org.http4s" %% "http4s-blaze-core" % "1.0.0-M23",
    "org.http4s" %% "http4s-blaze-server" % "1.0.0-M23",
    "org.http4s" %% "http4s-dsl" % "1.0.0-M23"
  )
)

lazy val shared = project
  .in(file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    sharedSettings
  )

lazy val front = project
  .in(file("front"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared)
  .settings(
    sharedSettings,
    frontSettings,

    Compile / mainClass := Some("lum.cah.MainClient"),
    Compile / fullOptJS / artifactPath := baseDirectory.value / ".." / "app" / "index.js",
    Compile / fastOptJS / artifactPath := baseDirectory.value / ".." / "app" / "index.js"
)

lazy val book = project
  .in(file("book"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(front)
  .settings(
    sharedSettings,
    frontSettings,
    
    Compile / mainClass := Some("lum.cah.MainBook"),
    Compile / fullOptJS / artifactPath := baseDirectory.value / ".." / "app" / "book.js",
    Compile / fastOptJS / artifactPath := baseDirectory.value / ".." / "app" / "book.js"
)

lazy val back = project
  .in(file("back"))
  .dependsOn(shared)
  .settings(
    sharedSettings,
    backSettings,
  )