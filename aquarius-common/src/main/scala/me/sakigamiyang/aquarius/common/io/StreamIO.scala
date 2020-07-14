package me.sakigamiyang.aquarius.common.io

import java.io.{ByteArrayOutputStream, InputStream, OutputStream}

/**
 * StreamIO
 */
object StreamIO {
  /**
   * Copy an InputStream to an OutputStream in chunks of the given
   * buffer size (default = 1KB).
   *
   * @param inputStream  input stream
   * @param outputStream output stream
   * @param bufferSize   buffer size for copy input stream to output stream
   */
  final def copy(
                  inputStream: InputStream,
                  outputStream: OutputStream,
                  bufferSize: Int = 1024
                ): Unit = {
    val buffer = new Array[Byte](bufferSize)
    copy(inputStream, outputStream, buffer)
  }

  /**
   * Buffer (fully) the given input stream by creating & copying it to a ByteArrayOutputStream.
   *
   * @param inputStream input stream
   * @return output stream
   */
  def buffer(inputStream: InputStream): ByteArrayOutputStream = {
    val bos = new java.io.ByteArrayOutputStream
    copy(inputStream, bos)
    bos
  }
  
  /**
   * Copy an InputStream to an OutputStream in chunks of the given
   * buffer size (default = 1KB).
   *
   * @param inputStream  input stream
   * @param outputStream output stream
   * @param buffer       buffer  for copy input stream to output stream
   */
  @annotation.tailrec
  final private def copy(
                          inputStream: InputStream,
                          outputStream: OutputStream,
                          buffer: Array[Byte]
                        ): Unit = {
    inputStream.read(buffer, 0, buffer.length) match {
      case -1 => ()
      case n =>
        outputStream.write(buffer, 0, n)
        copy(inputStream, outputStream, buffer)
    }
  }
}
