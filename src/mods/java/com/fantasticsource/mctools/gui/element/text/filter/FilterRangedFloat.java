package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.LinkedHashMap;
import java.util.function.Function;

public class FilterRangedFloat extends TextFilter<Float> {
   private static LinkedHashMap<String, FilterRangedFloat> instances = new LinkedHashMap();
   private final float min;
   private final float max;

   private FilterRangedFloat(float min, float max) {
      this.min = min;
      this.max = max;
   }

   public static FilterRangedFloat get(float min, float max) {
      return (FilterRangedFloat)instances.computeIfAbsent(min + "," + max, (o) -> {
         return new FilterRangedFloat(min, max);
      });
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      try {
         float f = Float.parseFloat(this.transformInput(input));
         return f >= this.min && f <= this.max;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public Float parse(String input) {
      return !this.acceptable(input) ? null : Float.parseFloat(this.transformInput(input));
   }
}
