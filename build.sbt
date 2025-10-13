
val chiselVersion = "7.1.0"

ThisBuild / scalaVersion := "2.13.16"

val diplomacy = (project in file(".")).settings(
  name := "diplomacy",
  version := "0.0.2",
  organization := "edu.duke.cs.apex",
  libraryDependencies ++= Seq(
    "org.chipsalliance" %% "chisel" % chiselVersion,
    "edu.duke.cs.apex" %% "rocketchip-cde-fork" % "0.1.7",
    "com.lihaoyi" %% "sourcecode" % "0.3.1"
  ),
  resolvers += ("reposilite-repository-releases" at "http://54.165.244.214:8080/releases").withAllowInsecureProtocol(true),
  publishTo := Some(("reposilite-repository" at "http://54.165.244.214:8080/releases/").withAllowInsecureProtocol(true)),
  credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
  addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full)
)
