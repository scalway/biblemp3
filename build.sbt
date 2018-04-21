name := "ScalaJS Hello World Demo"

version := "0.1"

organization := "org.my"

scalaVersion := "2.12.4"

sbtVersion := "1.0.4"

enablePlugins(ScalaJSPlugin)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false

libraryDependencies ++= Seq(
  "org.scala-js"      %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi"       %%% "utest" % "0.6.3" % "test",
  "com.lihaoyi"       %%% "scalatags" % "0.6.7",
  "com.lihaoyi"       %%% "sourcecode" % "0.1.3",
  "be.doeraene"       %%% "scalajs-jquery" % "0.9.1"
)

jsDependencies ++= Seq(
  "org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js"
)