name := "SparkLauncherInYarn"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
    "org.apache.hadoop" % "hadoop-client" % "2.7.1" % "provided" excludeAll ExclusionRule(organization = "javax.servlet"),
    "org.apache.hbase" % "hbase-common" % "1.1.4",
    "org.apache.hbase" % "hbase-client" % "1.1.4",
    "org.apache.spark" % "spark-core_2.10" % "1.5.1"
)
