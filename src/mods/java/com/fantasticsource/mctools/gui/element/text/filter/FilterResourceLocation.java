package com.fantasticsource.mctools.gui.element.text.filter;

import com.fantasticsource.tools.Tools;
import net.minecraft.util.ResourceLocation;

public class FilterResourceLocation extends TextFilter<ResourceLocation> {
   public static final FilterResourceLocation INSTANCE = new FilterResourceLocation();

   protected FilterResourceLocation() {
   }

   public String transformInput(String input) {
      return input.trim();
   }

   public boolean acceptable(String input) {
      String[] tokens = Tools.fixedSplit(this.transformInput(input), ":");
      return tokens.length == 2 && !tokens[0].trim().equals("") && !tokens[1].trim().equals("");
   }

   public ResourceLocation parse(String input) {
      return !this.acceptable(input) ? null : new ResourceLocation(this.transformInput(input));
   }
}
