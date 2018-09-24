val ScalatraVersion = "2.6.3"

organization := "ice.master"

name := "Datawarehouse"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Classpaths.typesafeReleases
resolvers += "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"
resolvers += "maven-central" at "http://repo1.maven.org/maven2/"

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container;compile",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.2"
)

mainClass in Compile := Some("ice.master.datawarehouse.JettyLauncher")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)