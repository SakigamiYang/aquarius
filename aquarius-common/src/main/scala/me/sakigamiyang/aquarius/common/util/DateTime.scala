package me.sakigamiyang.aquarius.common.util

import java.time._
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.ConcurrentHashMap


object DateTime {

  var CHINA_ZONE_ID: ZoneId = ZoneId.of("GMT+8")
  var UTC_ZONE_ID: ZoneId = ZoneId.of("GMT")

  var DEFAULT_DATE_PATTERN = "yyyy-MM-dd"
  var DEFAULT_TIME_PATTERN = "HH:mm:ss"
  var DEFAULT_DATETIME_PATTERN: String = DEFAULT_DATE_PATTERN + " " + DEFAULT_TIME_PATTERN

  var TS_8_PATTERN = "yyyyMMdd"
  var TS_10_PATTERN: String = DEFAULT_DATE_PATTERN
  var TS_14_PATTERN = "yyyyMMddHHmmss"
  var TS_17_PATTERN = "yyyyMMddHHmmssSSS"

  var DEFAULT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)
  var DEFAULT_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)
  var DEFAULT_DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)
  var TS_8_PATTERN_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TS_8_PATTERN)
  var TS_10_PATTERN_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TS_10_PATTERN)
  var TS_14_PATTERN_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TS_14_PATTERN)
  var TS_17_PATTERN_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TS_17_PATTERN)

  private val formatterCache: ConcurrentHashMap[String, DateTimeFormatter] = new ConcurrentHashMap[String, DateTimeFormatter]()
  formatterCache.put(DEFAULT_DATE_PATTERN, DEFAULT_DATE_FORMATTER)
  formatterCache.put(DEFAULT_TIME_PATTERN, DEFAULT_TIME_FORMATTER)
  formatterCache.put(DEFAULT_DATETIME_PATTERN, DEFAULT_DATETIME_FORMATTER)

  def format(localDateTime: LocalDateTime): String = DEFAULT_DATETIME_FORMATTER.format(localDateTime)

  def format(localDateTime: LocalDateTime, pattern: String): String = formatterCache.computeIfAbsent(pattern, DateTimeFormatter.ofPattern(_)).format(localDateTime)

  def format(localDate: LocalDate): String = DEFAULT_DATE_FORMATTER.format(localDate)

  def format(localDate: LocalDate, pattern: String): String = formatterCache.computeIfAbsent(pattern, DateTimeFormatter.ofPattern(_)).format(localDate)

  // 2020-04-17
  def todayStr: String = LocalDate.now.format(DEFAULT_DATE_FORMATTER)

  def utcTodayStr: String = LocalDateTime.now(UTC_ZONE_ID).format(DEFAULT_DATE_FORMATTER)

  // 2020-04-16
  def yesterdayStr: String = LocalDate.now.minusDays(1).format(DEFAULT_DATE_FORMATTER)

  def utcYesterdayStr: String = LocalDate.now(UTC_ZONE_ID).minusDays(1).format(DEFAULT_DATE_FORMATTER)

  // 2020-04-18
  def tomorrowStr: String = LocalDate.now.plusDays(1).format(DEFAULT_DATE_FORMATTER)

  def utcTomorrowStr: String = LocalDate.now(UTC_ZONE_ID).plusDays(1).format(DEFAULT_DATE_FORMATTER)

  // 2020-04-17 14:39:14
  def nowStr: String = LocalDateTime.now.format(DEFAULT_DATETIME_FORMATTER)

  // 2020-04-17 06:39:14
  def utcNowStr: String = LocalDateTime.now(UTC_ZONE_ID).format(DEFAULT_DATETIME_FORMATTER)

  // 14:39:14
  def currentTimeStr: String = LocalTime.now.format(DEFAULT_TIME_FORMATTER)

  def utcCurrentTimeStr: String = LocalTime.now(UTC_ZONE_ID).format(DEFAULT_TIME_FORMATTER)

  // 20200417
  def currentTs8: String = LocalDateTime.now.format(TS_8_PATTERN_FORMATTER)

  def utcCurrentTs8: String = LocalDateTime.now(UTC_ZONE_ID).format(TS_8_PATTERN_FORMATTER)

  def currentTs10: String = LocalDateTime.now.format(TS_10_PATTERN_FORMATTER)

  def utcCurrentTs10: String = LocalDateTime.now(UTC_ZONE_ID).format(TS_10_PATTERN_FORMATTER)

  // 20200417063914
  def currentTs14: String = LocalDateTime.now.format(TS_14_PATTERN_FORMATTER)

  def utcCurrentTs14: String = LocalDateTime.now(UTC_ZONE_ID).format(TS_14_PATTERN_FORMATTER)

  // 20200417063914123
  def currentTs17: String = LocalDateTime.now.format(TS_17_PATTERN_FORMATTER)

  def utcCurrentTs17: String = LocalDateTime.now(UTC_ZONE_ID).format(TS_17_PATTERN_FORMATTER)

  def parseLocalDateTime(dateTimeStr: String, pattern: String): LocalDateTime = LocalDateTime.parse(dateTimeStr, formatterCache.computeIfAbsent(pattern, DateTimeFormatter.ofPattern(_)))

  def parseLocalDate(dateStr: String, pattern: String): LocalDate = LocalDate.parse(dateStr, formatterCache.computeIfAbsent(pattern, DateTimeFormatter.ofPattern(_)))

  // parse ISO-8601 format zoned datetime
  def parseZonedDateTime(dateTimeStr: String): ZonedDateTime = ZonedDateTime.parse(dateTimeStr)

  def convertZone(localDateTime: LocalDateTime, from: ZoneId, to: ZoneId): LocalDateTime = localDateTime.atZone(from).withZoneSameInstant(to).toLocalDateTime

  def fromLegacy2ZonedDateTime(date: Date): ZonedDateTime = ZonedDateTime.from(date.toInstant.atZone(UTC_ZONE_ID))

  def fromLegacy2UTC(date: Date): LocalDateTime = fromLegacy2ZonedDateTime(date).withZoneSameInstant(UTC_ZONE_ID).toLocalDateTime

  def fromLegacy2China(date: Date): LocalDateTime = fromLegacy2ZonedDateTime(date).withZoneSameInstant(CHINA_ZONE_ID).toLocalDateTime
}
