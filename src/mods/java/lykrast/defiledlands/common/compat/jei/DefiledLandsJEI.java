package lykrast.defiledlands.common.compat.jei;

import lykrast.defiledlands.common.init.ModItems;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JEIPlugin
public class DefiledLandsJEI implements IModPlugin {
   public static final ResourceLocation GUI = new ResourceLocation("defiledlands", "textures/gui/jei.png");

   public DefiledLandsJEI() {
   }

   public void register(IModRegistry registry) {
      DefilementMaker.registerRecipes(registry);
      registry.addRecipeCatalyst(new ItemStack(ModItems.defilementPowder), new String[]{"dldefilement"});
   }

   public void registerCategories(IRecipeCategoryRegistration registry) {
      IJeiHelpers jeiHelpers = registry.getJeiHelpers();
      IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
      registry.addRecipeCategories(new IRecipeCategory[]{new DefilementCategory(guiHelper)});
   }
}
