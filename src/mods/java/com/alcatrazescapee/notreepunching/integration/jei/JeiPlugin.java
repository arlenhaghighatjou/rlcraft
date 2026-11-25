package com.alcatrazescapee.notreepunching.integration.jei;

import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.client.gui.GuiFirePit;
import com.alcatrazescapee.notreepunching.common.recipe.FirePitRecipe;
import com.alcatrazescapee.notreepunching.common.recipe.KnifeRecipe;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@JEIPlugin
public final class JeiPlugin implements IModPlugin {
   static final String KNIFE_UID = "notreepunching.knife";
   static final String FIRE_PIT_UID = "notreepunching.fire_pit";

   public JeiPlugin() {
   }

   public void registerCategories(IRecipeCategoryRegistration registry) {
      registry.addRecipeCategories(new IRecipeCategory[]{new KnifeRecipeCategory(registry.getJeiHelpers().getGuiHelper()), new FirePitRecipeCategory(registry.getJeiHelpers().getGuiHelper())});
   }

   public void register(IModRegistry registry) {
      if (ModConfig.GENERAL.replaceVanillaRecipes) {
         IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
         blacklist.addIngredientToBlacklist(new ItemStack(Items.WOODEN_AXE));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.WOODEN_HOE));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.WOODEN_PICKAXE));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.WOODEN_SHOVEL));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.WOODEN_SWORD));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.STONE_AXE));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.STONE_HOE));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.STONE_PICKAXE));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.STONE_SHOVEL));
         blacklist.addIngredientToBlacklist(new ItemStack(Items.STONE_SWORD));
      }

      registry.handleRecipes(KnifeRecipe.class, KnifeRecipeCategory.Wrapper::new, "notreepunching.knife");
      registry.addRecipes(ModRecipes.KNIFE.getAll(), "notreepunching.knife");
      registry.handleRecipes(FirePitRecipe.class, FirePitRecipeCategory.Wrapper::new, "notreepunching.fire_pit");
      registry.addRecipes(ModRecipes.FIRE_PIT.getAll(), "notreepunching.fire_pit");
      registry.addRecipeClickArea(GuiFirePit.class, 75, 22, 26, 19, new String[]{"notreepunching.fire_pit"});
   }
}
