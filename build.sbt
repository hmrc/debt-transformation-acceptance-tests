name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "2.13.18"

lazy val root       = (project in file(".")).settings(Test / testOptions := Seq.empty)
val CucumberVersion = "4.7.1"

libraryDependencies ++= Seq(
  "org.playframework"          %% "play-ahc-ws-standalone"   % "3.0.10",
  "org.playframework"          %% "play-ws-standalone-json"  % "3.0.10",
  "org.scalatest"              %% "scalatest"                % "3.2.20",
  "io.cucumber"                %% "cucumber-scala"           % CucumberVersion,
  "io.cucumber"                 % "cucumber-junit"           % CucumberVersion,
  "io.cucumber"                 % "cucumber-picocontainer"   % CucumberVersion,
  "com.novocode"                % "junit-interface"          % "0.11",
  "commons-io"                  % "commons-io"               % "2.22.0",
  "com.beachape"               %% "enumeratum-play-json"     % "1.9.7",
  "uk.gov.hmrc"                %% "api-test-runner"          % "0.10.0" % Test,
  "ch.qos.logback"              % "logback-core"             % "1.5.32"
)
