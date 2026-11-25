package com.endertech.common;

public interface Ordered<T> extends Comparable<T> {
   static <T extends Comparable<T>> T min(T a, T b) {
      return (T)(a.compareTo(b) <= 0 ? a : b);
   }

   static <T extends Comparable<T>> T max(T a, T b) {
      return (T)(a.compareTo(b) >= 0 ? a : b);
   }

   default boolean isLessThan(T other) {
      return this.compareTo(other) < 0;
   }

   default boolean isLessOrEqualTo(T other) {
      return this.compareTo(other) <= 0;
   }

   default boolean isGreaterThan(T other) {
      return this.compareTo(other) > 0;
   }

   default boolean isGreaterOrEqualTo(T other) {
      return this.compareTo(other) >= 0;
   }
}
