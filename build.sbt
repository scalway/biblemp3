

val common = Seq(
  version := "0.1",
  organization := "org.my",
  scalaVersion := "2.12.4",
  sbtVersion := "1.0.4",
  testFrameworks += new TestFramework("utest.runner.Framework"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % "0.6.3" % "test"
  )
)

lazy val macros = project
  .enablePlugins(ScalaJSPlugin)
  .settings(common)
  .settings(
    name := "macros",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.scala-js"      %%% "scalajs-dom" % "0.9.1",
      "com.lihaoyi"       %%% "utest" % "0.6.3" % "test",
      "com.lihaoyi"       %%% "scalatags" % "0.6.7",
      "com.lihaoyi"       %%% "sourcecode" % "0.1.3",
      "com.propensive"    %%% "magnolia" % "0.7.1",
      "com.lihaoyi"       %%% "upickle" % "0.6.5",
      "be.doeraene"       %%% "scalajs-jquery" % "0.9.1",
      "com.github.lukajcb" %%% "rxscala-js" % "0.15.0"
    )
  )

lazy val app = project.in(file("app"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(macros)
  .settings(common)
  .settings(
    // This is an application with a main method
    //scalaJSUseMainModuleInitializer := true,

    skip in packageJSDependencies := false,
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv,
    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % "3.3.1" / "3.3.1/jquery.js",
      "org.webjars.npm" % "rxjs" % "5.4.0" / "bundles/Rx.min.js" commonJSName "Rx"
    )
  ).settings(
    name := "ScalaJS Hello World Demo",
  )