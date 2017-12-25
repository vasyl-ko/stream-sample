val root = Project("streamsample", file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.5.8",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.8" % Test,
      "io.spray" %% "spray-json" % "1.3.4"
    )
  )
