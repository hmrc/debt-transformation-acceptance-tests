name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "2.13.12"

val CucumberVersion = "4.7.1"

resolvers += "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play"          %% "play-ahc-ws-standalone"   % "2.1.0",
  "com.typesafe.play"          %% "play-ws-standalone-json"  % "2.1.0",
  "com.typesafe.play"          %% "play-ws-standalone-xml"   % "2.1.0",
  "org.scalatest"              %% "scalatest"                % "3.0.8"         % "test",
  "io.cucumber"                %% "cucumber-scala"           % "8.7.0" % "test",
  "io.cucumber"                 % "cucumber-junit"           % "7.6.0" % "test",
  "io.cucumber"                 % "cucumber-picocontainer"   % CucumberVersion % "test",
  "com.novocode"                % "junit-interface"          % "0.11"          % "test",
  "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.5",
  "com.typesafe.akka"          %% "akka-http"                % "10.1.15"       % "test",
  "com.typesafe.akka"          %% "akka-stream"              % "2.6.20"        % "test",
  "com.github.mifmif"           % "generex"                  % "1.0.2",
  "com.google.zxing"            % "core"                     % "3.3.3",
  "uk.gov.hmrc"                %% "webdriver-factory"        % "0.39.0"        % "test",
  "com.google.zxing"            % "javase"                   % "3.4.1",
  "commons-io"                  % "commons-io"               % "2.6",
  "org.julienrf"               %% "play-json-derived-codecs" % "7.0.0",
  "com.beachape"               %% "enumeratum-play-json"     % "1.6.1",
  "org.pegdown"                 % "pegdown"                  % "1.6.0"         % "test",
)
