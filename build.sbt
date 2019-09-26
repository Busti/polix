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
  "org.typelevel" %% "cats-effect" % "2.0.0"
)
