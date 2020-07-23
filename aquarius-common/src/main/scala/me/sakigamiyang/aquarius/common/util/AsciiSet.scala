package me.sakigamiyang.aquarius.common.util

import java.util.{BitSet => JBitSet}

/**
 * Ascii set
 */
object AsciiSet {
  /**
   * Create a set of a single character.
   *
   * @param c character
   * @return ascii character
   */
  def apply(c: Char): AsciiChar = new AsciiChar(c)

  /**
   * Create a set of more than one character.
   *
   * @param c  character
   * @param cs characters
   * @return ascii set
   */
  def apply(c: Char, cs: Char*): AsciiSet = cs.foldLeft[AsciiSet](apply(c)) {
    case (acc, c1) => acc ||| apply(c1)
  }

  /**
   * Some useful sets of ASCII characters.
   */
  object Sets {
    // Core Rules (https://tools.ietf.org/html/rfc5234#appendix-B.1).
    // These are used in HTTP (https://tools.ietf.org/html/rfc7230#section-1.2).
    val Digit: AsciiSet = new AsciiRange('0', '9')
    val Lower: AsciiSet = new AsciiRange('a', 'z')
    val Upper: AsciiSet = new AsciiRange('A', 'Z')
    val Alpha: AsciiSet = Lower ||| Upper
    val AlphaDigit: AsciiSet = Alpha ||| Digit
    // https://en.wikipedia.org/wiki/ASCII#Printable_characters
    val VChar: AsciiSet = new AsciiRange(0x20, 0x7e)
  }

}

/**
 * A set of ASCII characters. The set should be built out of [[AsciiRange]],
 * [[AsciiChar]], [[AsciiUnion]], etc then converted to an [[AsciiBitSet]] using `toBitSet` for fast querying.
 */
trait AsciiSet {
  /**
   * The internal method used to query for set membership.
   * Doesn't do any bounds checks. Also may be slow, so to query from outside this package
   * you should convert to an [[AsciiBitSet]] using `toBitSet`.
   *
   * @param i integer
   */
  def getInternal(i: Int): Boolean

  /**
   * Join together two sets.
   *
   * @param that another ascii set
   * @return joined ascii set
   */
  def |||(that: AsciiSet): AsciiUnion = new AsciiUnion(this, that)

  /**
   * Convert into an [[AsciiBitSet]] for fast querying.
   *
   * @return ascii bit set
   */
  def toBitSet: AsciiBitSet = {
    val bitSet = new JBitSet(256)
    for (i <- 0 until 256)
      if (this.getInternal(i))
        bitSet.set(i)
    new AsciiBitSet(bitSet)
  }
}

/** An inclusive range of ASCII characters */
private[common] final class AsciiRange(first: Int, last: Int) extends AsciiSet {
  override def toString: String = s"(${Integer.toHexString(first)}- ${Integer.toHexString(last)})"

  override def getInternal(i: Int): Boolean = i >= first && i <= last
}

/** A set with a single ASCII character in it. */
private[common] final class AsciiChar(i: Int) extends AsciiSet {
  override def getInternal(i: Int): Boolean = i == this.i
}

/** A union of two [[AsciiSet]]s. */
private[common] final class AsciiUnion(a: AsciiSet, b: AsciiSet) extends AsciiSet {
  override def getInternal(i: Int): Boolean = a.getInternal(i) || b.getInternal(i)
}

/**
 * An efficient representation of a set of ASCII characters. Created by
 * building an [[AsciiSet]] then calling `toBitSet` on it.
 */
private[common] final class AsciiBitSet private[util](bitSet: JBitSet) extends AsciiSet {
  def get(i: Int): Boolean = {
    if (i < 0 || i > 255)
      throw new IllegalArgumentException(s"Character $i cannot match AsciiSet because it is out of range")
    getInternal(i)
  }

  override def getInternal(i: Int): Boolean = bitSet.get(i)

  override def toBitSet: AsciiBitSet = this
}
