scalaVersion in ThisBuild := "2.12.4"

name := "h2-doobie-flyway-numeric"

val doobieVersion = "0.5.0-M13"
val flywayApiVersion = "4.2.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2" % doobieVersion % Test,
  "org.tpolecat" %% "doobie-h2" % doobieVersion % Test,
  "org.flywaydb" % "flyway-core" % flywayApiVersion % Test
)
