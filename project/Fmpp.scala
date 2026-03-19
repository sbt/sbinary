import sbt._
import Keys._

object Fmpp {

  /**
   * * Templating *
   */
  lazy val fmpp = TaskKey[Seq[File]]("fmpp")
  lazy val fmppOptions = SettingKey[Seq[String]]("fmpp-options")
  lazy val FmppConfig = config("fmpp").hide

  lazy val templateSettings = fmppConfig(Test) ++ fmppConfig(Compile) ++ templateBase
  lazy val templateBase = Seq(
    libraryDependencies += "net.sourceforge.fmpp" % "fmpp" % "0.9.16" % FmppConfig,
    ivyConfigurations += FmppConfig,
    fmppOptions := "--ignore-temporary-files" :: Nil,
    FmppConfig / fullClasspath := update.value select configurationFilter(
      FmppConfig.name
    ) map Attributed.blank
  )

  import sbt.io.Path._
  def fmppConfig(c: Configuration): Seq[Setting[?]] =
    inConfig(c)(
      Seq(
        sourceGenerators += fmpp,
        fmpp := fmppTask.value,
        packageSrc / mappings ++= managedSources.value pair relativeTo(sourceManaged.value)
      )
    )
  lazy val fmppTask =
    Def.task {
      val cp = (FmppConfig / fullClasspath).value
      val r = (fmpp / runner).value
      val srcRoot = baseDirectory.value / "src" / "main" / "fmpp"
      val sources = (srcRoot ** "*.scala").get
      val output = sourceManaged.value
      val args = fmppOptions.value
      val s = streams.value
      IO.delete(output)
      val arguments =
        "-U" +: "all" +: "-S" +: srcRoot.getAbsolutePath +: "-O" +: output.getAbsolutePath +: (args ++ sources.getPaths)
      r.run("fmpp.tools.CommandLine", cp.files, arguments, s.log) // .foreach(sys.error)
      (output ** "*.scala").get
    }
}
