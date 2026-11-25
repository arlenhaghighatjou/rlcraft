package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterInt extends TextFilter<Integer> {
   public static final FilterInt INSTANCE = new FilterInt();

   private FilterInt() {
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      try {
         Integer.parseInt(this.transformInput(input));
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public Integer parse(String input) {
      return !this.acceptable(input) ? null : Integer.parseInt(this.transformInput(input));
   }
}
