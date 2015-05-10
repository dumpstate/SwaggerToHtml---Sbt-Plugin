version := "0.1"

sbtPlugin := true

organization := "com.evojam.sbt"

name := "swagger-to-html-plugin"

libraryDependencies ++= Seq(
  "com.wordnik" % "swagger-codegen" % "2.1.2-M1"
)
