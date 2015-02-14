name := "SolrAdmin"

version := "1.0"

lazy val `solradmin` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.2"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws ,
  "org.apache.curator" % "curator-client" % "2.7.0" withSources() withJavadoc(),
  "org.apache.curator" % "curator-framework" % "2.7.0" withSources() withJavadoc(),
  "org.apache.curator" % "curator-recipes" % "2.6.0" withSources() withJavadoc(),
  "org.apache.solr" % "solr-core" % "4.10.0" withSources() withJavadoc() excludeAll(ExclusionRule(organization = "org.slf4j"), ExclusionRule(organization = "com.google.protobuf")),
  "org.apache.solr" % "solr-solrj" % "4.10.0" withSources() withJavadoc(),
  /*"javax.mail" % "mail" % "1.4.1" withSources() withJavadoc(),*/
  "com.typesafe.akka" % "akka-actor_2.10" % "2.3.6" withSources() withJavadoc(),
  "com.typesafe.akka" % "akka-slf4j_2.10" % "2.3.6" withSources() withJavadoc(),
  "com.typesafe.akka" % "akka-testkit_2.10" % "2.3.6" withSources() withJavadoc(),
  "org.json4s" %% "json4s-native" % "3.2.1" withSources(),
  "org.json4s" %% "json4s-jackson" % "3.2.1" withSources(),
  "org.apache.zookeeper" % "zookeeper" % "3.4.6"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )