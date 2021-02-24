name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "2.12.11"

val CucumberVersion = "4.8.0"

resolvers += "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play"          %% "play-ahc-ws-standalone"  % "1.1.7",
  "com.typesafe.play"          %% "play-ws-standalone-json" % "1.1.7",
  "com.typesafe.play"          %% "play-ws-standalone-xml"  % "1.1.7",
  "org.scalatest"              %% "scalatest"               % "3.0.8" % "test",
  "io.cucumber"                %% "cucumber-scala"          % "4.7.1" % "test",
  "io.cucumber"                % "cucumber-junit"           % CucumberVersion % "test",
  "io.cucumber"                % "cucumber-picocontainer"   % CucumberVersion % "test",
  "com.novocode"               % "junit-interface"          % "0.11" % "test",
  "com.typesafe.scala-logging" %% "scala-logging"           % "3.8.0",
  "com.typesafe.akka"          %% "akka-http"               % "10.1.11" % "test",
  "com.typesafe.akka"          %% "akka-stream"             % "2.5.27" % "test",
  "com.github.mifmif"          % "generex"                  % "1.0.2",
  "uk.gov.hmrc"                %% "webdriver-factory"       % "0.11.0" % "test",
  "uk.gov.hmrc"                %% "zap-automation"          % "2.7.0" % "test",
  "uk.gov.hmrc"                %% "simple-reactivemongo"    % "7.30.0-play-26",
  "com.google.zxing"           % "core"                     % "3.3.3",
  "com.google.zxing"           % "javase"                   % "3.4.1"
)

addCompilerPlugin(
  ("org.scalamacros" %% "paradise" % "2.1.1").cross(CrossVersion.full)
)
