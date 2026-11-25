package rustic.compat.jei;

import java.util.ArrayList;
import javax.annotation.Nullable;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rustic.client.gui.GuiBrewingBarrel;
import rustic.client.gui.GuiCondenser;
import rustic.client.gui.GuiCondenserAdvanced;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.IBrewingBarrelRecipe;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.ICrushingTubRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.items.ModItems;

@JEIPlugin
public class RusticJEIPlugin implements IModPlugin {
   public static IRecipeRegistry recipeRegistry;
   public CrushingTubRecipeCategory crushingTubCat;
   @Nullable
   private ISubtypeRegistry subtypeRegistry;

   public RusticJEIPlugin() {
   }

   public void register(IModRegistry reg) {
      IJeiHelpers helper = reg.getJeiHelpers();
      IGuiHelper guiHelper = helper.getGuiHelper();
      if (Config.ENABLE_OLIVE_OILING) {
         reg.addRecipes(OliveOilRecipeMaker.getOliveOilRecipes(), "minecraft.crafting");
      }

      reg.addRecipes(CabinetRecipeWrapper.getCabinetRecipes(), "minecraft.crafting");
      this.crushingTubCat = new CrushingTubRecipeCategory(guiHelper);
      reg.addRecipeCategories(new IRecipeCategory[]{this.crushingTubCat});
      ArrayList<ICrushingTubRecipe> crushingTubRecipes = new ArrayList();

      for(int i = 0; i < Recipes.crushingTubRecipes.size(); ++i) {
         crushingTubRecipes.add(Recipes.crushingTubRecipes.get(i));
      }

      reg.addRecipes(crushingTubRecipes, "rustic.crushing_tub");
      reg.handleRecipes(ICrushingTubRecipe.class, new CrushingTubRecipeWrapperFactory(), "rustic.crushing_tub");
      reg.addRecipeCategories(new IRecipeCategory[]{new EvaporatingRecipeCategory(guiHelper)});
      ArrayList<IEvaporatingBasinRecipe> evaporatingRecipes = new ArrayList();

      for(IEvaporatingBasinRecipe recipe : Recipes.evaporatingRecipesMap.values()) {
         evaporatingRecipes.add(recipe);
      }

      reg.addRecipes(evaporatingRecipes, "rustic.evaporating");
      reg.handleRecipes(IEvaporatingBasinRecipe.class, new EvaporatingRecipeWrapperFactory(), "rustic.evaporating");
      reg.addRecipeCategories(new IRecipeCategory[]{new AdvancedAlchemyRecipeCategory(guiHelper)});
      reg.addRecipes(AdvancedAlchemyRecipeMaker.getAlchemyRecipes(helper), "rustic.alchemy_advanced");
      reg.handleRecipes(ICondenserRecipe.class, new AdvancedAlchemyRecipeWrapperFactory(), "rustic.alchemy_advanced");
      reg.addRecipeCategories(new IRecipeCategory[]{new SimpleAlchemyRecipeCategory(guiHelper)});
      reg.addRecipes(SimpleAlchemyRecipeMaker.getSimpleAlchemyRecipes(helper), "rustic.alchemy_simple");
      reg.handleRecipes(ICondenserRecipe.class, new SimpleAlchemyRecipeWrapperFactory(), "rustic.alchemy_simple");
      reg.addRecipeCategories(new IRecipeCategory[]{new BrewingRecipeCategory(guiHelper)});
      reg.addRecipes(Recipes.brewingRecipes, "rustic.brewing");
      reg.handleRecipes(IBrewingBarrelRecipe.class, new BrewingRecipeWrapperFactory(), "rustic.brewing");
      reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CRUSHING_TUB), new String[]{"rustic.crushing_tub"});
      reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.EVAPORATING_BASIN), new String[]{"rustic.evaporating"});
      reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CONDENSER), new String[]{"rustic.alchemy_simple", "minecraft.fuel"});
      reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CONDENSER_ADVANCED), new String[]{"rustic.alchemy_advanced", "rustic.alchemy_simple", "minecraft.fuel"});
      reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.BREWING_BARREL), new String[]{"rustic.brewing"});
      reg.addRecipeClickArea(GuiBrewingBarrel.class, 85, 35, 24, 16, new String[]{"rustic.brewing"});
      reg.addRecipeClickArea(GuiCondenser.class, 44, 29, 50, 28, new String[]{"rustic.alchemy_simple"});
      reg.addRecipeClickArea(GuiCondenserAdvanced.class, 44, 25, 50, 35, new String[]{"rustic.alchemy_simple", "rustic.alchemy_advanced"});
   }

   public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
      this.subtypeRegistry = subtypeRegistry;
      subtypeRegistry.useNbtForSubtypes(new Item[]{ModItems.FLUID_BOTTLE, ModItems.ELIXIR});
   }

   public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
      recipeRegistry = iJeiRuntime.getRecipeRegistry();
   }
}
