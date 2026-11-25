package com.fantasticsource.mctools.gui.element.text.filter;

import com.fantasticsource.tools.Tools;
import net.minecraft.util.ResourceLocation;

public class FilterNullableResourceLocation extends TextFilter<ResourceLocation> {
   public static final FilterNullableResourceLocation INSTANCE = new FilterNullableResourceLocation();

   protected FilterNullableResourceLocation() {
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      input = this.transformInput(input);
      if (input.equals("")) {
         return true;
      } else {
         String[] tokens = Tools.fixedSplit(input, ":");
         return tokens.length == 2 && !tokens[0].trim().equals("") && !tokens[1].trim().equals("");
      }
   }

   public ResourceLocation parse(String input) {
      if (!this.acceptable(input)) {
         return null;
      } else {
         input = this.transformInput(input);
         return input.equals("") ? null : new ResourceLocation(input);
      }
   }
}
