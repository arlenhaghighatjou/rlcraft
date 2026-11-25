package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterWhitelist extends TextFilter<String> {
   public ArrayList<String> allowed = new ArrayList();

   public FilterWhitelist(String... allowed) {
      this.allowed.addAll(Arrays.asList(allowed));
   }

   public String transformInput(String input) {
      return input == null ? null : input.trim();
   }

   public boolean acceptable(String input) {
      if (input == null) {
         return false;
      } else {
         for(String s : this.allowed) {
            if (input.equals(s)) {
               return true;
            }
         }

         return false;
      }
   }

   public String parse(String input) {
      return input != null && this.acceptable(input) ? this.transformInput(input) : null;
   }
}
