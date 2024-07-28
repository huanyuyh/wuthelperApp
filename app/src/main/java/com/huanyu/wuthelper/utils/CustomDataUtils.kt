package com.huanyu.wuthelper.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CustomDataUtils {
    companion object{
//        获取当前时间
        fun isTimeInRange(currentTime: LocalTime, startTime: LocalTime, endTime: LocalTime): Boolean {
            return if (startTime.isBefore(endTime)) {
                currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
            } else {
                currentTime.isAfter(startTime) || currentTime.isBefore(endTime)
            }
        }
        fun getCurrentDateTime():String{
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        }
        fun getCurrentDate(): String {
            val currentDate = LocalDate.now()
            return currentDate.toString() // 格式为 YYYY-MM-DD
        }
        fun getCurrentYear(): Int {
            val currentDate = LocalDate.now()
            return currentDate.year // 返回当前年份
        }

        fun getCurrentMonth(): Int {
            val currentDate = LocalDate.now()
            return currentDate.monthValue // 返回当前月份（1-12）
        }

        fun getCurrentDay(): Int {
            val currentDate = LocalDate.now()
            return currentDate.dayOfMonth // 返回当前月份中的日（1-31）
        }
        fun getCurrentTime(): String {
            val currentTime = LocalTime.now()
            return currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) // 格式为 HH:MM:SS
        }

        fun getCurrentWeekDay(): String {
            val currentWeekDay = LocalDate.now().dayOfWeek
            return currentWeekDay.getDisplayName(TextStyle.FULL, Locale.getDefault()) // 获取完整星期名称
        }
        //判断当前时间是否在给定时间段
        fun isCurrentTimeInRange(startTime: String, endTime: String): Boolean {
            val currentTime = LocalTime.now()
            val start = LocalTime.parse(startTime)
            val end = LocalTime.parse(endTime)

            return if (start.isBefore(end)) {
                // Time range is within the same day
                currentTime.isAfter(start) && currentTime.isBefore(end)
            } else {
                // Time range spans midnight
                currentTime.isAfter(start) || currentTime.isBefore(end)
            }
        }
        fun calculateTimeDifference(targetTimeString:String): Duration {
            val currentTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val targetTime = LocalDateTime.parse(targetTimeString, formatter)

            return Duration.between(currentTime, targetTime)
        }
        fun UTCtoTime(isoDate:String):String{
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val zonedDateTime = ZonedDateTime.parse(isoDate)

            // 将时间转换为东八区时间
            val beijingTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
            val formattedDate = beijingTime.format(formatter)
            Log.d("formattedDate",formattedDate)  // 输出：2020-01-13 16:00:00
            return formattedDate
        }
        fun addDaysToDateReturnMonthDay(dateStr: String?, daysToAdd: Long): String {
            val date = LocalDate.parse(dateStr)
            val newDate = date.plusDays(daysToAdd)
            val formatter = DateTimeFormatter.ofPattern("MM-dd")
            return newDate.format(formatter)
        }
        fun addDaysToDateReturnMonth(dateStr: String?, daysToAdd: Long): String {
            val date = LocalDate.parse(dateStr)
            val newDate = date.plusDays(daysToAdd)
            val formatter = DateTimeFormatter.ofPattern("MM")
            return newDate.format(formatter)
        }
        fun addDaysToDateReturnWeekday(dateStr: String?, daysToAdd: Long): Int {
            val date = LocalDate.parse(dateStr)
            val newDate = date.plusDays(daysToAdd)
            return newDate.dayOfWeek.value
        }
        fun addDaysToDateReturnDay(dateStr: String?, daysToAdd: Long): String {
            val date = LocalDate.parse(dateStr)
            val newDate = date.plusDays(daysToAdd)
            val formatter = DateTimeFormatter.ofPattern("dd")
            return newDate.format(formatter)
        }
        fun monthDaysDifference(inputDate: String): Long {
            val currentDate = LocalDate.now()
            // 解析输入日期（默认为当前年份）
            var targetDate = LocalDate.parse("${currentDate.year}-$inputDate", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // 如果目标日期早于当前日期，则假设它是下一年的日期
            if (targetDate.isBefore(currentDate)) {
                targetDate = LocalDate.parse("${currentDate.year + 1}-$inputDate", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }

            // 计算天数差
            return ChronoUnit.DAYS.between(currentDate, targetDate)
        }
        fun yearMonthDaysDifference(inputDate: String): Long {
            val currentDate = LocalDate.now()
            // 解析输入日期（默认为当前年份）
            var targetDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // 计算天数差
            return ChronoUnit.DAYS.between(targetDate,currentDate )
        }
        fun calculateMinutesDifference(timeStr: String): Long {
            val now = LocalTime.now()
            val givenTime = LocalTime.parse(timeStr)
            return ChronoUnit.MINUTES.between(now, givenTime)
        }
        fun calculateTimeDifference(startTime: LocalTime, endTime: LocalTime): Long {
            return ChronoUnit.SECONDS.between(startTime, endTime) // 返回两个时间之间的秒数差
        }
        fun unixtoDateString(unix:Long):String{
            val date = Date(unix)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }

    }

}