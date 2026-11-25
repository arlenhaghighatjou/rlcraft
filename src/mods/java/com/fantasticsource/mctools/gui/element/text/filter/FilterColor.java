package com.fantasticsource.mctools.gui.element.text.filter;

import com.fantasticsource.tools.Tools;

public class FilterColor extends TextFilter<Integer> {
   public static final FilterColor INSTANCE = new FilterColor();

   private FilterColor() {
   }

   public String transformInput(String input) {
      return input.trim().replace("0x", "");
   }

   public boolean acceptable(String input) {
      String transformed = this.transformInput(input);
      if (!input.equals("") && input.length() <= 8) {
         for(char c : transformed.toCharArray()) {
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Integer parse(String input) {
      return !this.acceptable(input) ? null : Tools.parseHexInt(this.transformInput(input));
   }
}
