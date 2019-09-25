name := "polix"

version := "0.1"

scalaVersion := "2.13.0-M5"

resolvers ++= Seq(
  // "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
  // "Atlassian Releases"  at "https://maven.atlassian.com/public/",
  "Sonatype snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"    % "1.6.0",
  "org.typelevel" %% "cats-effect"  % "1.3.0",
)
