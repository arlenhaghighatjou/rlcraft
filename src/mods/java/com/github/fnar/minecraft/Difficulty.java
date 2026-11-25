package com.github.fnar.minecraft;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Difficulty {
   PEACEFUL,
   EASY,
   NORMAL,
   DIFFICULT;

   private Difficulty() {
   }

   public int asInt() {
      return this.ordinal();
   }

   public static Difficulty fromInt(int number) {
      return (Difficulty)Arrays.stream(values()).filter((difficulty) -> {
         return difficulty.asInt() == number;
      }).findFirst().orElseThrow(() -> {
         return new NoSuchElementException("No Difficulty with numerical equivalent of " + number);
      });
   }
}
