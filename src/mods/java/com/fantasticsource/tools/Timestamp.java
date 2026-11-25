package com.fantasticsource.tools;

import java.time.Instant;

public class Timestamp {
   protected final Instant instant;
   protected final int year;
   protected final int month;
   protected final int day;
   protected final int hour;
   protected final int minute;
   protected final int second;
   protected final int millisecond;
   protected final String yearString;
   protected final String monthString;
   protected final String dayString;
   protected final String hourString;
   protected final String minuteString;
   protected final String secondString;
   protected final String millisecondString;

   protected Timestamp(Instant instant) {
      this.instant = instant;
      String s = instant.toString();
      int last = s.length() - 1;
      this.yearString = s.substring(0, 4);
      this.monthString = s.substring(5, 7);
      this.dayString = s.substring(8, 10);
      this.hourString = s.substring(11, 13);
      this.minuteString = s.substring(14, 16);
      this.secondString = s.substring(17, Tools.min(19, last));
      this.millisecondString = last >= 20 ? s.substring(20, Tools.min(23, last)) : "0";
      this.year = Integer.parseInt(this.yearString);
      this.month = Integer.parseInt(this.monthString);
      this.day = Integer.parseInt(this.dayString);
      this.hour = Integer.parseInt(this.hourString);
      this.minute = Integer.parseInt(this.minuteString);
      this.second = Integer.parseInt(this.secondString);
      this.millisecond = Integer.parseInt(this.millisecondString);
   }

   public static Timestamp getInstance() {
      return getInstance(Instant.now());
   }

   public static Timestamp getInstance(Instant instant) {
      return instant == null ? null : new Timestamp(instant);
   }

   public Instant getInstant() {
      return this.instant;
   }

   public int getYear() {
      return this.year;
   }

   public int getMonth() {
      return this.month;
   }

   public int getDay() {
      return this.day;
   }

   public int getHour() {
      return this.hour;
   }

   public int getMinute() {
      return this.minute;
   }

   public int getSecond() {
      return this.second;
   }

   public int getMillisecond() {
      return this.millisecond;
   }

   public String getYearString() {
      return this.yearString;
   }

   public String getMonthString() {
      return this.monthString;
   }

   public String getDayString() {
      return this.dayString;
   }

   public String getHourString() {
      return this.hourString;
   }

   public String getMinuteString() {
      return this.minuteString;
   }

   public String getSecondString() {
      return this.secondString;
   }

   public String getMillisecondString() {
      return this.millisecondString;
   }

   public String toString() {
      return this.toString(true, true, true);
   }

   public String toString(boolean date, boolean time, boolean ms) {
      return (date ? this.yearString + "-" + this.monthString + "-" + this.dayString : "") + (date && time ? " " : "") + (time ? this.hourString + ":" + this.minuteString + ":" + this.secondString + (ms ? "." + this.millisecondString : "") : "");
   }

   public int compareTo(Timestamp other) {
      return this.instant.compareTo(other.instant);
   }
}
