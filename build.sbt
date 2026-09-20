import Dependencies._

ThisBuild / organization := "org.scala-sbt"
ThisBuild / organizationHomepage := Some(url("http://scala-sbt.org/"))
ThisBuild / homepage := Some(url("https://github.com/sbt/sbinary"))
ThisBuild / version := "0.5.2-SNAPSHOT"
ThisBuild / scalaVersion := scala212
ThisBuild / crossScalaVersions := Seq(scala212, scala213, scala3)
ThisBuild / developers := List(
  Developer(
    "drmaciver",
    "David R. MacIver",
    "@drmaciver",
    url("https://github.com/DRMacIver")
  ),
  Developer("harrah", "Mark Harrah", "@harrah", url("https://github.com/harrah")),
  Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n"))
)
ThisBuild / description := "Library for describing binary formats for Scala types"
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/sbt/sbinary/blob/master/LICENSE"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/sbt/sbinary"), "git@github.com:sbt/sbinary.git")
)
ThisBuild / pomIncludeRepository := (_ => false) // drop repos other than Maven Central from POM
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (version.value.endsWith("-SNAPSHOT")) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

lazy val root = (project in file("."))
  .aggregate(core, treeExample)
  .settings(nocomma {
    name := "SBinary Root"
    publish / skip := true
    mimaPreviousArtifacts := Set.empty
  })

lazy val core = (project in file("core"))
  .settings(nocomma {
    name := "SBinary"
    libraryDependencies += scalacheck % Test
    libraryDependencies += scalaXml
    Compile / unmanagedSourceDirectories += {
      val base = (Compile / scalaSource).value.getParentFile
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) | Some((3, _)) =>
          base / s"scala-2.13+"
        case _ =>
          base / s"scala-2.13-"
      }
    }
    Compile / unmanagedResources += (baseDirectory map { _ / "LICENSE" }).value
    mimaPreviousArtifacts := {
      val versions = Seq("0.5.1")
      versions.map(v => organization.value %% moduleName.value % v).toSet
    }
    scalacOptions ++= {
      scalaBinaryVersion.value match {
        case "2.12" | "2.13" =>
          Seq(
            "-Xsource:3",
            "-release:8",
          )
        case _ =>
          Seq(
            "-release:11",
            "-Yfuture-lazy-vals",
          )
      }
    }
  })
  .settings(
    relaxOldScala,
    Fmpp.templateSettings
  )

lazy val treeExample = (project in (file("examples") / "bt"))
  .dependsOn(core)
  .settings(nocomma {
    name := "SBinary Tree Example"
    publish / skip := true
    mimaPreviousArtifacts := Set.empty
  })
  .settings(relaxOldScala)

def relaxOldScala: Seq[Setting[?]] = Seq(
  scalacOptions := {
    val old = scalacOptions.value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 12 =>
        old filterNot Set(
          "-Xfatal-warnings",
          "-Ywarn-unused-import",
          "-Yno-adapted-args"
        )
      case _ =>
        old filterNot (Set(
          "-Xfatal-warnings",
          "-deprecation",
          "-Ywarn-unused",
          "-Ywarn-unused-import"
        ).apply _)
    }
  }
)
