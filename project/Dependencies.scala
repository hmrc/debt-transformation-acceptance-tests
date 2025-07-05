import sbt._

object Dependencies {

  val test = Seq(
    "org.scalatest"       %% "scalatest"                % "3.2.0"   % Test,
    "com.novocode"         % "junit-interface"          % "0.11"    % Test,
    "com.typesafe"         % "config"                   % "1.3.2"   % Test,
    "org.playframework"   %% "play-test"                % "2.8.18"  % Test,
    "org.slf4j"            % "slf4j-simple"             % "1.7.25"  % Test,
    "org.julienrf"        %% "play-json-derived-codecs" % "7.0.0",
    Test,
    "com.vladsch.flexmark" % "flexmark-all"             % "0.35.10" % Test,
    "com.beachape"        %% "enumeratum-play-json"     % "1.6.1",
    Test,
    "uk.gov.hmrc"         %% "api-test-runner"          % "0.10.0"  % Test
  )
}
