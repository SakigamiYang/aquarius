package me.sakigamiyang.aquarius.common.io


sealed abstract class NewLine(content: String) {
  val name: String = toString

  def value: String = content

  override def toString: String = value
}

object NewLines {

  case object SYSTEM_DEPENDENT extends NewLine(System.getProperty("line.separator"))

  case object WINDOWS extends NewLine("\r\n")

  case object UNIX_AND_MACOS extends NewLine("\n")

  case object CLASSIC_MACOS extends NewLine("\r")

}
