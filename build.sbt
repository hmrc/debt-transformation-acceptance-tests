name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "2.13.12"

val CucumberVersion = "4.7.1"

resolvers += "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"

libraryDependencies ++= Seq(
  "org.playframework"          %% "play-json"                % "3.0.1",
  "org.playframework"          %% "play-ahc-ws-standalone"   % "3.0.0",
  "org.playframework"          %% "play-ws-standalone-json"  % "3.0.0",
  "org.playframework"          %% "play-ws-standalone-xml"   % "3.0.0" % Test,
  "org.scalatest"              %% "scalatest"                % "3.0.8",
  "io.cucumber"                %% "cucumber-scala"           % CucumberVersion,
  "io.cucumber"                 % "cucumber-junit"           % CucumberVersion,
  "io.cucumber"                 % "cucumber-picocontainer"   % CucumberVersion,
  "com.novocode"                % "junit-interface"          % "0.11",
  "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.5",
  "org.apache.pekko"           %% "pekko-http"               % "1.0.0",
  "org.apache.pekko"           %% "pekko-stream"             % "1.0.2",
  "com.github.mifmif"           % "generex"                  % "1.0.2",
  "com.google.zxing"            % "core"                     % "3.3.3",
  "uk.gov.hmrc"                %% "webdriver-factory"        % "0.39.0",
  "com.google.zxing"            % "javase"                   % "3.4.1",
  "commons-io"                  % "commons-io"               % "2.6",
  "org.julienrf"               %% "play-json-derived-codecs" % "7.0.0",
  "com.beachape"               %% "enumeratum-play-json"     % "1.6.1"
)
