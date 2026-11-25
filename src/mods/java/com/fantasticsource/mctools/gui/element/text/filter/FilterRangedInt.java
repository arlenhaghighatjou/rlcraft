package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.LinkedHashMap;
import java.util.function.Function;

public class FilterRangedInt extends TextFilter<Integer> {
   private static LinkedHashMap<String, FilterRangedInt> instances = new LinkedHashMap();
   private final int min;
   private final int max;

   private FilterRangedInt(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public static FilterRangedInt get(int min, int max) {
      return (FilterRangedInt)instances.computeIfAbsent(min + "," + max, (o) -> {
         return new FilterRangedInt(min, max);
      });
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      try {
         int i = Integer.parseInt(this.transformInput(input));
         return i >= this.min && i <= this.max;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public Integer parse(String input) {
      return !this.acceptable(input) ? null : Integer.parseInt(this.transformInput(input));
   }
}
