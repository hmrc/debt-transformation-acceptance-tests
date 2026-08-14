name := "debt-transformation-acceptance-tests"
version := "0.1"
scalaVersion := "3.3.7"

lazy val root = (project in file(".")).settings(Test / testOptions := Seq.empty)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-source:future-migration"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"       % "3.2.20" % Test,
  "uk.gov.hmrc"   %% "api-test-runner" % "0.10.0" % Test
)
