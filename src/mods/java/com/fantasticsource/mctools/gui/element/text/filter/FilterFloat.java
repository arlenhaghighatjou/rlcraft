package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterFloat extends TextFilter<Float> {
   public static final FilterFloat INSTANCE = new FilterFloat();

   private FilterFloat() {
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      try {
         Float.parseFloat(this.transformInput(input));
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public Float parse(String input) {
      return !this.acceptable(input) ? null : Float.parseFloat(this.transformInput(input));
   }
}
