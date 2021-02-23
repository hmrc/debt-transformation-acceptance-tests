import sbt._

object Dependencies {

  val test = Seq(
    "org.scalatest"     %% "scalatest"              % "3.0.7"  % Test,
    "io.cucumber"       %% "cucumber-scala"         % "6.9.1"  % Test,
    "io.cucumber"        % "cucumber-junit"         % "6.9.1"  % Test,
    "junit"              % "junit"                  % "4.12"   % Test,
    "com.novocode"       % "junit-interface"        % "0.11"   % Test,
    "com.typesafe"       % "config"                 % "1.3.2"  % Test,
    "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.2"  % Test,
    "org.slf4j"          % "slf4j-simple"           % "1.7.25" % Test
  )
}
