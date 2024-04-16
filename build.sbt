ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.3.3"

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
    "io.github.ollls" %% "quartz-h2" % "0.7",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.19.1",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.19.1" % "compile-internal",
    "org.typelevel" %% "cats-effect-testing-minitest" % "1.5.0" % Test
  )
)

scalacOptions ++= Seq(
  // "-Wunused:imports",
  // "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-explain"
)

testFrameworks += new TestFramework("minitest.runner.Framework")
