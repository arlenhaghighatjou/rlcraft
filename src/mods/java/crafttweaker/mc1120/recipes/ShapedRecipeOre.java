package crafttweaker.mc1120.recipes;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IMTRecipe;
import crafttweaker.api.recipes.ShapedRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/** @deprecated */
@Deprecated
public class ShapedRecipeOre extends ShapedOreRecipe implements IMTRecipe {
   private final ShapedRecipe recipe;

   public ShapedRecipeOre(ResourceLocation name, Object[] contents, ShapedRecipe recipe) {
      super(name, CraftTweakerMC.getItemStack(recipe.getOutput()), contents);
      this.recipe = recipe;
   }

   public boolean matches(InventoryCrafting inventory, World world) {
      return this.recipe.matches(MCCraftingInventory.get(inventory));
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      return this.recipe != null && this.recipe.getCraftingResult(MCCraftingInventory.get(inventory)) != null ? CraftTweakerMC.getItemStack(this.recipe.getCraftingResult(MCCraftingInventory.get(inventory))).copy() : ItemStack.EMPTY;
   }

   public ItemStack getRecipeOutput() {
      return CraftTweakerMC.getItemStack(this.recipe.getOutput());
   }

   public ICraftingRecipe getRecipe() {
      return this.recipe;
   }
}
