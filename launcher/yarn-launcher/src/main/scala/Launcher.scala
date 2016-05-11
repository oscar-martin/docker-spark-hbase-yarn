import org.apache.commons.io.IOUtils
import org.apache.spark.launcher.SparkLauncher

import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }
import scala.io.Source
import scala.reflect.io.File

// run /uberjars/HBaseScalaTest-assembly-1.0.jar SparkInYarnTest
object YarnLauncher {

    def main(args: Array[String]) {
        if (args.size < 2) {
            println("Missing parameters")
            println("   Usage: sbt run <uberJarPath> <mainClass> [args]")
            System.exit(1)
        }

        val uberJarPath = args(0)
        val mainClass = args(1)

        val env = Map(
            "HADOOP_CONF_DIR" -> sys.env("YARN_CONF_DIR"),
            "YARN_CONF_DIR" -> sys.env("YARN_CONF_DIR")
        )

        val sparkLauncher = new SparkLauncher(env.asJava)
            .setAppResource(uberJarPath)
            .setMainClass(mainClass)
            .setMaster("yarn-client")
            .setSparkHome("/usr/local/spark")
            .setConf(SparkLauncher.DRIVER_MEMORY, "256m")
            .setConf(SparkLauncher.EXECUTOR_MEMORY, "256m")
            .setConf(SparkLauncher.EXECUTOR_CORES, "4")
            .setConf("spark.executor.instances", "1")

        if (args.size > 2) {
            sparkLauncher.addAppArgs(args.splitAt(2)._2: _*)
        }
        val process = sparkLauncher.launch()

        Source.fromInputStream(process.getErrorStream).getLines().foreach { line =>
            println(line)
        }
        Source.fromInputStream(process.getInputStream).getLines().foreach { line =>
            println(line)
        }

        process.waitFor()
    }
}
