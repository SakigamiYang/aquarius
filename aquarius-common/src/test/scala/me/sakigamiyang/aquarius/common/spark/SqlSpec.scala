package me.sakigamiyang.aquarius.common.spark

import java.nio.file.Paths

import me.sakigamiyang.aquarius.common.app.{SparkApp, SparkParameter, SparkParameterParser}
import me.sakigamiyang.aquarius.common.spark.Sql._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scopt.OptionParser

case class A(name: String, age: Int)

class SqlSpec extends AnyFunSpec with Matchers {
  describe("test Spark sql utilities") {
    it("return right schema for type tag") {
      val schema = schemaFor[A]
      schema.fields(0).name shouldBe "name"
      schema.fields(0).dataType shouldBe DataTypes.StringType
      schema.fields(0).nullable shouldBe true
      schema.fields(1).name shouldBe "age"
      schema.fields(1).dataType shouldBe DataTypes.IntegerType
      schema.fields(1).nullable shouldBe false
    }

    it("load schema from JSON") {
      val json = "{\"type\":\"struct\",\"fields\":[{\"name\":\"name\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"age\",\"type\":\"integer\",\"nullable\":false,\"metadata\":{}}]}"
      val schema = loadSchemaFromJson(json)
      schema.fields(0).name shouldBe "name"
      schema.fields(0).dataType shouldBe DataTypes.StringType
      schema.fields(0).nullable shouldBe true
      schema.fields(1).name shouldBe "age"
      schema.fields(1).dataType shouldBe DataTypes.IntegerType
      schema.fields(1).nullable shouldBe false
    }

    it("load schema from JSON file encoding by UTF-8") {
      val projectRootDir = System.getProperty("user.dir")
      val testFile = Paths.get(projectRootDir, "src", "test", "resources", "sample-schema-utf8.json")
      val schema = loadSchemaFromFile(testFile.toString)
      schema.fields(0).name shouldBe "name"
      schema.fields(0).dataType shouldBe DataTypes.StringType
      schema.fields(0).nullable shouldBe true
      schema.fields(1).name shouldBe "age"
      schema.fields(1).dataType shouldBe DataTypes.IntegerType
      schema.fields(1).nullable shouldBe false
    }

    it("load schema from JSON file encoding by GBK") {
      val projectRootDir = System.getProperty("user.dir")
      val testFile = Paths.get(projectRootDir, "src", "test", "resources", "sample-schema-gbk.json")
      val schema = loadSchemaFromFile(testFile.toString, "GBK")
      schema.fields(0).name shouldBe "name"
      schema.fields(0).dataType shouldBe DataTypes.StringType
      schema.fields(0).nullable shouldBe true
      schema.fields(1).name shouldBe "age"
      schema.fields(1).dataType shouldBe DataTypes.IntegerType
      schema.fields(1).nullable shouldBe false
    }

    it("Spark DataFrame Row to Map") {
      final case class AlphaSparkParameter(override val appName: String = "local-spark-job",
                                           override val master: String = "local[*]",
                                           override val enableHiveSupport: Boolean = false)
        extends SparkParameter(appName, master, enableHiveSupport)

      final class AlphaSparkParameterParser(sparkParameter: SparkParameter) extends SparkParameterParser(sparkParameter) {
        override type parameterT = AlphaSparkParameter

        final lazy val parser = new OptionParser[parameterT]("alpha-app") {
          help("alpha-app")

          opt[String]("app-name")
            .optional
            .valueName("app-name")
            .action((value, param) => param.copy(appName = value))

          opt[String]("master")
            .optional
            .valueName("master")
            .action((value, param) => param.copy(master = value))

          opt[Boolean]("enable-hive-support")
            .optional
            .valueName("app-name")
            .action((value, param) => param.copy(enableHiveSupport = value))

          override def showUsageOnError: Boolean = false
        }
      }

      final class AlphaSparkApp(sparkParameterParser: SparkParameterParser) extends SparkApp(sparkParameterParser) {
        override type parameterT = AlphaSparkParameter
        override type parameterParserT = AlphaSparkParameterParser

        override def run(spark: SparkSession, parameters: parameterT): Unit = {
          import spark.implicits._

          val df = spark.createDataset(Seq(A("Tom", 32), A("Jerry", 25))).toDF()
          val maps = df.map(rowToMap).collect().sortBy(_.getOrElse("age", -1).toString.toInt)
          maps(0).getOrElse("name", "").toString shouldBe "Jerry"
          maps(0).getOrElse("age", -1).toString.toInt shouldBe 25
          maps(1).getOrElse("name", "").toString shouldBe "Tom"
          maps(1).getOrElse("age", -1).toString.toInt shouldBe 32
        }
      }

      val args = Array[String]()
      val alphaSparkParameter = AlphaSparkParameter()
      val alphaSparkParameterParser = new AlphaSparkParameterParser(alphaSparkParameter)
      new AlphaSparkApp(alphaSparkParameterParser)(args)
    }
  }
}
