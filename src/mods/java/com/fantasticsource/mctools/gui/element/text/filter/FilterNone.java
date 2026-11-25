package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterNone extends TextFilter<String> {
   public static final FilterNone INSTANCE = new FilterNone();

   private FilterNone() {
   }

   public String transformInput(String input) {
      return input;
   }

   public boolean acceptable(String input) {
      return true;
   }

   public String parse(String input) {
      return input;
   }
}
