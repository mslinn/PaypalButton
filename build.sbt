organization := "com.micronautics"

name := "Encrypted PayPal Buttons"

version := "0.1"

scalaVersion := "2.10.2"

scalaVersion in update := "2.10"

autoCompilerPlugins := true


scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.6", "-unchecked",
      "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint")

resolvers ++= Seq(
  "Typesafe Releases"        at "http://repo.typesafe.com/typesafe/releases",
  "Scala Tools Releases"     at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype Nexus Releases"  at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.bouncycastle" % "bcprov-jdk16" % "1.46" withSources(),
  "org.bouncycastle" % "bcpg-jdk16" % "1.46" withSources(),
  "org.bouncycastle" % "bcmail-jdk16" % "1.46" withSources()
//  "org.bouncycastle" % "bctest-jdk16" % "1.46" withSources()
)

logLevel := Level.Error

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn

