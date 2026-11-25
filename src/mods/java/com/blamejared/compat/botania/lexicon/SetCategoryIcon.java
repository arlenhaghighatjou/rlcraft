package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.lexicon.LexiconCategory;

public class SetCategoryIcon implements IAction {
   private LexiconCategory category;
   private ResourceLocation newIcon;
   private final String name;

   public SetCategoryIcon(String name, String icon) {
      this.name = name;
      this.newIcon = new ResourceLocation(icon);
   }

   public void apply() {
      this.category = BotaniaHelper.findCatagory(this.name);
      if (this.category == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon category " + this.name);
      } else {
         this.category.setIcon(this.newIcon);
         CraftTweakerAPI.getLogger().logInfo("Setting Lexicon Category icon: " + this.category.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to set the icon for Lexicon Category " + this.name + " to " + this.newIcon;
   }
}
