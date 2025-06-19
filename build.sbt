name := "FunctionalFinance"
version := "0.1"
scalaVersion := "2.13.12"


libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.23.23",
  "org.http4s" %% "http4s-dsl" % "0.23.30",
  "org.http4s" %% "http4s-circe" % "0.23.30",
  "io.circe" %% "circe-generic" % "0.14.14",
  "org.typelevel" %% "cats-effect" % "3.6.1",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test

)