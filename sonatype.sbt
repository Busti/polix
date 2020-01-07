//sonatypeProfileName := "com.github.busti"
organization        := "com.github.busti"

publishMavenStyle := true

licenses += ("Apache 2.0" -> url("https://github.com/Busti/polix/blob/master/LICENSE"))

import xerial.sbt.Sonatype._

sonatypeProjectHosting := Some(
  GitHubHosting(
    "Busti",
    "polix",
    "Moritz Bust",
    "mbust+polix@mailbox.org"
  )
)

homepage := Some(url("https://github.com/busti/polix"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/busti/polix"),
    "git@github.com:busti/polix.git"
  )
)

developers := List(
  Developer("Busti", "Moritz Bust", "mbust+polix@mailbox.org", url("https://github.com/busti"))
)