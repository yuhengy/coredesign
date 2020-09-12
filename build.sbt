// See LICENSE for license details.
import complete.DefaultParsers._
import scala.sys.process._

def scalacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // If we're building with Scala > 2.11, enable the compile option
    //  switch to support our anonymous Bundle definitions:
    //  https://github.com/scala/bug/issues/10047
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 => Seq()
      case _ => Seq("-Xsource:2.11")
    }
  }
}

def javacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // Scala 2.12 requires Java 8. We continue to generate
    //  Java 7 compatible code for Scala 2.11
    //  for compatibility with old clients.
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 =>
        Seq("-source", "1.7", "-target", "1.7")
      case _ =>
        Seq("-source", "1.8", "-target", "1.8")
    }
  }
}

  
scalaVersion := "2.12.6"
scalacOptions := Seq("-deprecation", "-feature") ++ scalacOptionsVersion(scalaVersion.value)
javacOptions ++= javacOptionsVersion(scalaVersion.value)
  
libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.3.2"

/*
//-----------------alternative sbt usage begin-----------------
lazy val commonSettings = Seq (
  scalaVersion := "2.12.6",
  scalacOptions := Seq("-deprecation", "-feature") ++ scalacOptionsVersion(scalaVersion.value),
  javacOptions ++= javacOptionsVersion(scalaVersion.value),
  
  libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.3.2",
)

lazy val common = (project in file("src/main/scala/common")).
  settings(commonSettings)

lazy val mycore = (project in file("src/main/scala/mycore")).
  settings(commonSettings).
  dependsOn(common)

lazy val sim = (project in file("src/main/scala/sim")).
  settings(commonSettings).
  dependsOn(common).
  dependsOn(mycore)
//-----------------alternative sbt usage end-----------------
*/
