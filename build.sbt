/* basic project info */
name := "http4s"

organization in ThisBuild := "org.http4s"

version in ThisBuild := "0.1.0-SNAPSHOT"

description := "Common HTTP framework for Scala"

homepage in ThisBuild := Some(url("https://github.com/http4s/http4s"))

startYear in ThisBuild := Some(2013)

licenses in ThisBuild := Seq(
  ("BSD 2-clause", url("https://raw.github.com/http4s/http4s/develop/LICENSE"))
)

scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/http4s/http4s"),
    "scm:git:https://github.com/http4s/http4s.git",
    Some("scm:git:git@github.com:http4s/http4s.git")
  )
)

/* scala versions and options */
scalaVersion in ThisBuild := "2.10.0"

offline in ThisBuild := false

scalacOptions in ThisBuild ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked"
)

javacOptions in ThisBuild ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

resolvers in ThisBuild ++= Seq(
  Resolver.typesafeRepo("releases"),
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("snapshots")
)

testOptions in ThisBuild += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")

/* sbt behavior */
logLevel in (ThisBuild, compile) := Level.Warn

traceLevel in ThisBuild := 5

/* publishing */
publishMavenStyle in ThisBuild := true

publishTo in ThisBuild <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some(
    "snapshots" at nexus + "content/repositories/snapshots"
  )
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in (ThisBuild, Test) := false

pomIncludeRepository in ThisBuild := { _ => false }

pomExtra in ThisBuild := (
  <developers>
    <developer>
      <id>rossabaker</id>
      <name>Ross A. Baker</name>
      <email>baker@alumni.indiana.edu</email>
      <!-- <url></url> -->
    </developer>
    <developer>
      <id>casualjim</id>
      <name>Ivan Porto Carrero</name>
      <email>ivan@flanders.co.nz</email>
      <url>http://flanders.co.nz</url>
    </developer>
  </developers>
)
