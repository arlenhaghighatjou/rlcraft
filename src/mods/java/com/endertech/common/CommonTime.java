package com.endertech.common;

import java.util.Date;
import java.util.Objects;

public final class CommonTime {
   public CommonTime() {
   }

   public interface IMessure {
      long getCapacity();
   }

   public static class Interval implements Comparable<CommonTime.Interval> {
      public static final CommonTime.Interval ZERO = new CommonTime.Interval(0.0D);
      public static final int MILLISECONDS_IN_SECOND = 1000;
      public static final int SECONDS_IN_MINUTE = 60;
      public static final int MINUTES_IN_HOUR = 60;
      public static final int HOURS_IN_DAY = 24;
      public static final int SECONDS_IN_HOUR = 3600;
      public static final int SECONDS_IN_DAY = 86400;
      private final long value;

      protected Interval(double value) {
         this.value = Math.round(value);
      }

      public static CommonTime.Interval of(CommonTime.IMessure messure, double amount) {
         return new CommonTime.Interval((double)messure.getCapacity() * amount);
      }

      public static CommonTime.Interval millis(long milliseconds) {
         return new CommonTime.Interval((double)milliseconds);
      }

      public static CommonTime.Interval seconds(double seconds) {
         return new CommonTime.Interval(seconds * 1000.0D);
      }

      public static CommonTime.Interval minutes(double minutes) {
         return new CommonTime.Interval(minutes * 60.0D * 1000.0D);
      }

      public static CommonTime.Interval hours(double hours) {
         return new CommonTime.Interval(hours * 3600.0D * 1000.0D);
      }

      public static CommonTime.Interval days(int days) {
         return new CommonTime.Interval((double)(days * 86400 * 1000));
      }

      public static CommonTime.Interval leftTo(CommonTime.Stamp time) {
         return millis(CommonTime.Stamp.now().distanceTo(time));
      }

      public static CommonTime.Interval passedFrom(CommonTime.Stamp stamp) {
         return millis(stamp.distanceTo(CommonTime.Stamp.now()));
      }

      public boolean moreThan(CommonTime.Interval interval) {
         return this.getValue() > interval.getValue();
      }

      public boolean lessThan(CommonTime.Interval interval) {
         return this.getValue() < interval.getValue();
      }

      public boolean equals(Object obj) {
         if (obj instanceof CommonTime.Interval) {
            return ((CommonTime.Interval)obj).getValue() == this.getValue();
         } else {
            return super.equals(obj);
         }
      }

      public long inMillis() {
         return this.getValue();
      }

      public double inSeconds() {
         return (double)this.getValue() / 1000.0D;
      }

      public double inMinutes() {
         return (double)this.getValue() / 60000.0D;
      }

      public double inHours() {
         return (double)this.getValue() / 3600000.0D;
      }

      public double inDays() {
         return (double)this.getValue() / 8.64E7D;
      }

      public double in(CommonTime.IMessure messure) {
         return (double)this.getValue() / (double)messure.getCapacity();
      }

      public CommonTime.Interval add(CommonTime.Interval time) {
         return millis(this.getValue() + time.getValue());
      }

      public CommonTime.Interval substract(CommonTime.Interval time) {
         return millis(this.getValue() - time.getValue());
      }

      public CommonTime.Interval mult(double factor) {
         return millis(Math.round((double)this.getValue() * factor));
      }

      protected long getValue() {
         return this.value;
      }

      public int compareTo(CommonTime.Interval o) {
         return Long.compare(this.getValue(), o.getValue());
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.getValue()});
      }
   }

   public static class Stamp implements Comparable<CommonTime.Stamp> {
      private final long value;

      protected Stamp(long value) {
         this.value = value;
      }

      public static CommonTime.Stamp now() {
         return new CommonTime.Stamp(System.currentTimeMillis());
      }

      public static CommonTime.Stamp at(Date date) {
         return new CommonTime.Stamp(date.getTime());
      }

      public long distanceTo(CommonTime.Stamp time) {
         return time.value - this.value;
      }

      public int compareTo(CommonTime.Stamp o) {
         return Long.compare(this.value, o.value);
      }

      public boolean equals(Object obj) {
         if (obj instanceof CommonTime.Stamp) {
            return this.value == ((CommonTime.Stamp)obj).value;
         } else {
            return super.equals(obj);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.value});
      }
   }
}
