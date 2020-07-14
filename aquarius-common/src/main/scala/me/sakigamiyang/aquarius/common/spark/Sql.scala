package me.sakigamiyang.aquarius.common.spark

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.{DataType, StructType}
import org.apache.spark.sql.{Encoder, Encoders, Row}

import scala.io.Source
import scala.reflect.runtime.universe._

object Sql {
  /**
   * Extract the schema ([[StructType]]) for a given type.
   *
   * @tparam T the type to extract the schema for.
   * @return schema.
   */
  def schemaFor[T: TypeTag]: StructType = ScalaReflection.schemaFor[T].dataType.asInstanceOf[StructType]

  /**
   * Load a schema ([[StructType]]) from a JSON string.
   *
   * @param json JSON string.
   * @return schema.
   */
  def loadSchemaFromJson(json: String): StructType = DataType.fromJson(json).asInstanceOf[StructType]

  /**
   * Load a schema ([[StructType]]) from a given file. The schema must be in json format.
   *
   * @param path path of JSON formatted resource file.
   * @param enc  encoding of file.
   * @return schema.
   */
  def loadSchemaFromFile(path: String, enc: String = "UTF-8"): StructType = {
    val bs = Source.fromFile(path, enc)
    try {
      val json = bs.getLines().mkString(" ")
      loadSchemaFromJson(json)
    } finally {
      bs.close()
    }
  }

  /**
   * Fix that Scala.Any is not serializable.
   */
  implicit val kyroEncForMapStringAny: Encoder[Map[String, Any]] = Encoders.kryo[Map[String, Any]]

  /**
   * Convert Spark SQL Row into a Map. Inner rows are also transformed into maps.
   *
   * @param row spark SQL Row.
   * @return map.
   */
  def rowToMap(row: Row): Map[String, Any] =
    row.schema.fields.map { key =>
      val value = row.getAs[Any](key.name) match {
        case r: Row => rowToMap(r)
        case v => v
      }
      (key.name, value)
    }.toMap
}
