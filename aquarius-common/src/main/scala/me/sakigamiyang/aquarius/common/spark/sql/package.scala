package me.sakigamiyang.aquarius.common.spark

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.{DataType, StructType}
import org.apache.spark.sql.{Encoder, Encoders, Row}

import scala.io.Source
import scala.reflect.runtime.universe._

package object sql {
  /**
   * Extract the schema ([[StructType]]) for a given type.
   *
   * @tparam T The type to extract the schema for.
   * @return Schema.
   */
  def schemaFor[T: TypeTag]: StructType = ScalaReflection.schemaFor[T].dataType.asInstanceOf[StructType]

  /**
   * Load a schema ([[StructType]]) from a JSON string.
   *
   * @param json JSON string.
   * @return Schema.
   */
  def loadSchemaFromJson(json: String): StructType = DataType.fromJson(json).asInstanceOf[StructType]

  /**
   * Load a schema ([[StructType]]) from a given file. The schema must be in json format.
   *
   * @param path Path of JSON formatted resource file.
   * @param enc  Encoding of file.
   * @return Schema.
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
   * @param row Spark SQL Row.
   * @return Map.
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
