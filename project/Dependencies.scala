import sbt._
import Keys._

object Dependencies {
  val scala212 = "2.12.21"
  val scala213 = "2.13.18"
  val scala3 = "3.3.8"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.19.0"
  val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "2.5.0"
}
