package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterBoolean extends TextFilter<Boolean> {
   public static final FilterBoolean INSTANCE = new FilterBoolean();

   private FilterBoolean() {
   }

   public String transformInput(String input) {
      input = input.trim().toLowerCase();
      if (!input.equals("t") && !input.equals("y") && !input.equals("yes")) {
         if (input.equals("f") || input.equals("n") || input.equals("no")) {
            input = "false";
         }
      } else {
         input = "true";
      }

      return input;
   }

   public boolean acceptable(String input) {
      String s = this.transformInput(input);
      return s.equals("true") || s.equals("false");
   }

   public Boolean parse(String input) {
      return !this.acceptable(input) ? null : Boolean.parseBoolean(this.transformInput(input));
   }
}
