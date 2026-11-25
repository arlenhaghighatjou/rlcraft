package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.K;
import org.checkerframework.checker.units.qual.cd;
import org.checkerframework.checker.units.qual.degrees;
import org.checkerframework.checker.units.qual.g;
import org.checkerframework.checker.units.qual.h;
import org.checkerframework.checker.units.qual.kg;
import org.checkerframework.checker.units.qual.km;
import org.checkerframework.checker.units.qual.km2;
import org.checkerframework.checker.units.qual.kmPERh;
import org.checkerframework.checker.units.qual.m;
import org.checkerframework.checker.units.qual.m2;
import org.checkerframework.checker.units.qual.mPERs;
import org.checkerframework.checker.units.qual.mPERs2;
import org.checkerframework.checker.units.qual.min;
import org.checkerframework.checker.units.qual.mm;
import org.checkerframework.checker.units.qual.mm2;
import org.checkerframework.checker.units.qual.mol;
import org.checkerframework.checker.units.qual.radians;
import org.checkerframework.checker.units.qual.s;

public class UnitsTools {
   @mPERs2
   public static final int mPERs2 = 1;
   @radians
   public static final double rad = 1.0D;
   @degrees
   public static final double deg = 1.0D;
   @mm2
   public static final int mm2 = 1;
   @m2
   public static final int m2 = 1;
   @km2
   public static final int km2 = 1;
   @A
   public static final int A = 1;
   @cd
   public static final int cd = 1;
   @mm
   public static final int mm = 1;
   @m
   public static final int m = 1;
   @km
   public static final int km = 1;
   @g
   public static final int g = 1;
   @kg
   public static final int kg = 1;
   @mPERs
   public static final int mPERs = 1;
   @kmPERh
   public static final int kmPERh = 1;
   @mol
   public static final int mol = 1;
   @K
   public static final int K = 1;
   @C
   public static final int C = 1;
   @s
   public static final int s = 1;
   @min
   public static final int min = 1;
   @h
   public static final int h = 1;

   public UnitsTools() {
   }

   @radians
   public static double toRadians(@degrees double angdeg) {
      return Math.toRadians(angdeg);
   }

   @degrees
   public static double toDegrees(@radians double angrad) {
      return Math.toDegrees(angrad);
   }

   @m
   public static int fromMilliMeterToMeter(@mm int mm) {
      return mm / 1000;
   }

   @mm
   public static int fromMeterToMilliMeter(@m int m) {
      return m * 1000;
   }

   @km
   public static int fromMeterToKiloMeter(@m int m) {
      return m / 1000;
   }

   @m
   public static int fromKiloMeterToMeter(@km int km) {
      return km * 1000;
   }

   @kg
   public static int fromGramToKiloGram(@g int g) {
      return g / 1000;
   }

   @g
   public static int fromKiloGramToGram(@kg int kg) {
      return kg * 1000;
   }

   @kmPERh
   public static double fromMeterPerSecondToKiloMeterPerHour(@mPERs double mps) {
      return mps * 3.6D;
   }

   @mPERs
   public static double fromKiloMeterPerHourToMeterPerSecond(@kmPERh double kmph) {
      return kmph / 3.6D;
   }

   @C
   public static int fromKelvinToCelsius(@K int k) {
      return k - 273;
   }

   @K
   public static int fromCelsiusToKelvin(@C int c) {
      return c + 273;
   }

   @min
   public static int fromSecondToMinute(@s int s) {
      return s / 60;
   }

   @s
   public static int fromMinuteToSecond(@min int min) {
      return min * 60;
   }

   @h
   public static int fromMinuteToHour(@min int min) {
      return min / 60;
   }

   @min
   public static int fromHourToMinute(@h int h) {
      return h * 60;
   }
}
