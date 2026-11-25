package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterBlacklist extends TextFilter<String> {
   public ArrayList<String> disallowed = new ArrayList();

   public FilterBlacklist(String... disallowed) {
      this.disallowed.addAll(Arrays.asList(disallowed));
   }

   public String transformInput(String input) {
      return input == null ? null : input.trim();
   }

   public boolean acceptable(String input) {
      if (input == null) {
         return false;
      } else {
         input = this.transformInput(input);
         if (input.equals("")) {
            return false;
         } else {
            for(String s : this.disallowed) {
               if (input.equals(s)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String parse(String input) {
      return input != null && this.acceptable(input) ? this.transformInput(input) : null;
   }
}
