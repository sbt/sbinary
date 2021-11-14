import sbt._
import Keys._

object Dependencies {
  val scala210 = "2.10.7"
  val scala211 = "2.11.12"
  val scala212 = "2.12.11"
  val scala213 = "2.13.1"
  val scala3 = "3.1.0"

  val scalacheck = Def.setting {
    scalaBinaryVersion.value match {
      case "2.10" | "2.11" =>
        "org.scalacheck" %% "scalacheck" % "1.14.3"
      case _ =>
        "org.scalacheck" %% "scalacheck" % "1.15.4"
    }
  }
  def scalaXmlDep(scalaV: String): List[ModuleID] =
    CrossVersion.partialVersion(scalaV) match {
      case Some((2, minor)) if minor <= 10 =>
        Nil
      case Some((2, 11 | 12)) =>
        List("org.scala-lang.modules" %% "scala-xml" % "1.0.6")
      case Some((2, 13)) =>
        List("org.scala-lang.modules" %% "scala-xml" % "1.2.0")
      case _ =>
        List("org.scala-lang.modules" %% "scala-xml" % "2.0.1")
    }
}
