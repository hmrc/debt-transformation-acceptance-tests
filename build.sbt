name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "2.13.12"

lazy val root       = (project in file(".")).settings(Test / testOptions := Seq.empty)
val CucumberVersion = "4.7.1"

libraryDependencies ++= Seq(
  "org.playframework"          %% "play-json"                % "3.0.6",
  "org.playframework"          %% "play-ahc-ws-standalone"   % "3.0.10",
  "org.playframework"          %% "play-ws-standalone-json"  % "3.0.10",
  "org.playframework"          %% "play-ws-standalone-xml"   % "3.0.10"  % Test,
  "org.scalatest"              %% "scalatest"                % "3.2.19",
  "io.cucumber"                %% "cucumber-scala"           % CucumberVersion,
  "io.cucumber"                 % "cucumber-junit"           % CucumberVersion,
  "io.cucumber"                 % "cucumber-picocontainer"   % CucumberVersion,
  "com.novocode"                % "junit-interface"          % "0.11",
  "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.6",
  "org.apache.pekko"           %% "pekko-http"               % "1.3.0",
  "org.apache.pekko"           %% "pekko-stream"             % "1.4.0",
  "com.github.mifmif"           % "generex"                  % "1.0.2",
  "com.google.zxing"            % "core"                     % "3.5.4",
  "com.google.zxing"            % "javase"                   % "3.5.4",
  "commons-io"                  % "commons-io"               % "2.21.0",
  "org.julienrf"               %% "play-json-derived-codecs" % "11.0.0",
  "com.beachape"               %% "enumeratum-play-json"     % "1.9.2",
  "uk.gov.hmrc"                %% "api-test-runner"          % "0.10.0" % Test,
  "org.scalameta"              %% "scalameta"                % "4.14.4",
  "com.github.scopt"           %% "scopt"                    % "4.1.0"
)
