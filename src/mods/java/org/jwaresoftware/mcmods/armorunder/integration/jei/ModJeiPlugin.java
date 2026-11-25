package org.jwaresoftware.mcmods.armorunder.integration.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.armorunder.recipes.ArmorRecipes;

@JEIPlugin
public class ModJeiPlugin implements IModPlugin {
   public ModJeiPlugin() {
   }

   public void register(IModRegistry jei) {
      IJeiHelpers jeiHelpers = jei.getJeiHelpers();
      jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModItems.icon));
      List<IRecipe> armor_recipes = ArmorRecipes.addJEISamples(new ArrayList(331));
      if (!armor_recipes.isEmpty()) {
         jei.addRecipes(armor_recipes, "minecraft.crafting");
      }

   }
}
