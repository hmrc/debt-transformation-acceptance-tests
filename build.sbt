name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "2.13.18"

lazy val root       = (project in file(".")).settings(Test / testOptions := Seq.empty)


libraryDependencies ++= Seq(
  "org.playframework" %% "play-ahc-ws-standalone"  % "3.0.13",
  "org.playframework" %% "play-ws-standalone-json" % "3.0.13",
  "org.scalatest"     %% "scalatest"               % "3.2.20",
  "commons-io"         % "commons-io"              % "2.22.0",
  "com.beachape"      %% "enumeratum-play-json"    % "1.9.8",
  "uk.gov.hmrc"       %% "api-test-runner"         % "0.10.0" % Test,
  "ch.qos.logback"     % "logback-core"            % "1.5.36"
)
