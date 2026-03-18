import sbt._
import Keys._

object Dependencies {
  val scala212 = "2.12.21"
  val scala213 = "2.13.18"
  val scala3 = "3.3.7"

  val scalacheck = Def.setting {
    "org.scalacheck" %% "scalacheck" % "1.15.4"
  }
  def scalaXmlDep(scalaV: String): List[ModuleID] =
    CrossVersion.partialVersion(scalaV) match {
      case Some((2, 12)) =>
        List("org.scala-lang.modules" %% "scala-xml" % "1.0.6")
      case Some((2, 13)) =>
        List("org.scala-lang.modules" %% "scala-xml" % "1.2.0")
      case _ =>
        List(("org.scala-lang.modules" %% "scala-xml" % "2.1.0").cross(CrossVersion.for3Use2_13))
    }
}
