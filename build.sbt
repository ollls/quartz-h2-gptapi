ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.2.1"

Runtime / unmanagedClasspath += baseDirectory.value / "src" / "main" / "resources"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _                                   => MergeStrategy.first
}

assembly / assemblyJarName := "qh2-http-run.jar"

/*
resolvers +=
  "Staging" at "https://s01.oss.sonatype.org/content/repositories/iogithubollls-1046"
*/  

lazy val root = (project in file(".")).settings(
  name := "json-template-qh2",
  libraryDependencies ++= Seq(
    "io.github.ollls" %% "quartz-h2" % "0.4.1",
    "org.typelevel" %% "cats-effect" % "3.4.7",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.19.1",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.19.1" % "compile-internal",
    "software.amazon.awssdk" % "aws-core" % "2.17.286"
  )
)

scalacOptions ++= Seq(
  // "-Wunused:imports",
  // "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-explain"
)
