import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Put, HTable, HBaseAdmin}
import org.apache.hadoop.hbase.util.Bytes

object SparkInYarnTest {
    def main(args: Array[String]) {
        def createTable(name: String, config: Configuration) {
            val tableName = name
            val hbaseAdmin = new HBaseAdmin(config)
            val family = Bytes.toBytes("f1")

            if (!hbaseAdmin.tableExists(tableName)) {
                val desc = new HTableDescriptor(tableName)
                desc.addFamily(new HColumnDescriptor(family))
                hbaseAdmin.createTable(desc)
                println(s"Table '$tableName' created")
            }
            else {
                println(s"Table '$tableName' already exists")
            }
        }

        def dropTable(name: String, config: Configuration) {
            val tableName = name
            val hbaseAdmin = new HBaseAdmin(config)

            if (hbaseAdmin.tableExists(tableName)) {
                hbaseAdmin.disableTable(tableName)
                hbaseAdmin.deleteTable(tableName)
                println(s"Table '$tableName' deleted")
            }
            else {
                println(s"Table '$tableName' does not exist")
            }
        }

        val logFile = "/tmp/file.txt"
        // Write the file
        val conf = new Configuration()
        conf.set("fs.defaultFS", "hdfs://hbase-master:8020")
        val fs = FileSystem.get(conf)

        fs.create(new Path(logFile))
        val os = fs.create(new Path(logFile))
        os.write("This test counts the number of letters 'a' and 'b'".getBytes)
        fs.close

        // Create HBASE table
        val config = HBaseConfiguration.create()
        config.set("hbase.zookeeper.quorum", "hbase-master")
        config.set("hbase.zookeeper.property.clientPort", "2181")

        createTable("TestTable", config)

        // Process the file
        val sparkConf = new SparkConf().setAppName("Simple Application")
        val sc = new SparkContext(sparkConf)
        val logData = sc.textFile(logFile, 2).cache()
        val numAs = logData.filter(line => line.contains("a")).count()
        val numBs = logData.filter(line => line.contains("b")).count()
        println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))

        dropTable("TestTable", config)

        sc.stop()
    }
}
