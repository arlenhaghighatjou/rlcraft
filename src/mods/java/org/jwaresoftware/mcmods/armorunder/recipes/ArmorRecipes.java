package org.jwaresoftware.mcmods.armorunder.recipes;

import java.util.List;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import org.jwaresoftware.mcmods.armorunder.ModConfig;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.SharedGlue;

public final class ArmorRecipes {
   static boolean ENABLED = false;
   static boolean SPECIAL_SETS = false;

   public static final void initFinal(ModConfig config) {
      ENABLED = TLinedArmorRecipe.initFinal(config);
      AutoregLinedArmorRecipe.initSharedDictIds();
      SPECIAL_SETS = config.includeSpecialLiners();
      if (SPECIAL_SETS && SharedGlue.isDefined(ModItems.diamond_strong_thread)) {
         OreDictionary.registerOre("stringDiamond", ModItems.diamond_strong_thread);
      }

   }

   public static final List<IRecipe> addJEISamples(List<IRecipe> recipes) {
      TLinedArmorRecipe.addSampleRecipes(recipes);
      AutoregLinedArmorRecipe.addSampleRecipes(recipes);
      BuffAutoregLiningRecipe.addSampleRecipes(recipes);
      return recipes;
   }

   private ArmorRecipes() {
   }
}
