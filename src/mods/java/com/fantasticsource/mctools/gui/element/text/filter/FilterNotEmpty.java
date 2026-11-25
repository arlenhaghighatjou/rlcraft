package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterNotEmpty extends TextFilter<String> {
   public static final FilterNotEmpty INSTANCE = new FilterNotEmpty();

   protected FilterNotEmpty() {
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      if (input == null) {
         return false;
      } else {
         return !this.transformInput(input).equals("");
      }
   }

   public String parse(String input) {
      return !this.acceptable(input) ? null : this.transformInput(input);
   }
}
