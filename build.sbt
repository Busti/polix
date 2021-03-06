name := "polix"

version := "0.1"

scalaVersion := "2.13.0"

resolvers ++= Seq(
  // "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
  // "Atlassian Releases"  at "https://maven.atlassian.com/public/",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"   % "2.0.0",
  "org.typelevel" %% "cats-effect" % "2.0.0",
  "org.scalatest" %% "scalatest"   % "3.0.8" % "test",
  "io.monix"      %% "monix"       % "3.0.0" % "test",
  "co.fs2"        %% "fs2-core"    % "2.0.1" % "test"
)

publishTo := sonatypePublishTo.value