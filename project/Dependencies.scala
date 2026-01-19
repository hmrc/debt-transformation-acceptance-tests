import sbt.*

object Dependencies {

  val test: Seq[Serializable] = Seq(
    "org.scalatest"       %% "scalatest"                % "3.2.19"  % Test,
    "com.novocode"         % "junit-interface"          % "1.4.5"    % Test,
    "com.typesafe"         % "config"                   % "3.0.10"   % Test,
    "org.playframework"   %% "play-test"                % "3.0.10"  % Test,
    "org.slf4j"            % "slf4j-simple"             % "2.0.17"  % Test,
    "org.julienrf"        %% "play-json-derived-codecs" % "11.0.0",
    Test,
    "com.vladsch.flexmark" % "flexmark-all"             % "0.64.8" % Test,
    "com.beachape"        %% "enumeratum-play-json"     % "1.9.2",
    Test
  )
}
