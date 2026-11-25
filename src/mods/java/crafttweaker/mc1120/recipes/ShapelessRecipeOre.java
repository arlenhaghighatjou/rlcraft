package crafttweaker.mc1120.recipes;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IMTRecipe;
import crafttweaker.api.recipes.ShapelessRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/** @deprecated */
@Deprecated
public class ShapelessRecipeOre extends ShapelessOreRecipe implements IMTRecipe {
   private final ShapelessRecipe recipe;

   public ShapelessRecipeOre(ResourceLocation group, Object[] ingredients, ShapelessRecipe recipe) {
      super(group, CraftTweakerMC.getItemStack(recipe.getOutput()), ingredients);
      this.recipe = recipe;
   }

   public boolean matches(InventoryCrafting inventory, World world) {
      return this.recipe.matches(MCCraftingInventory.get(inventory));
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      return CraftTweakerMC.getItemStack(this.recipe.getCraftingResult(MCCraftingInventory.get(inventory))).copy();
   }

   public ICraftingRecipe getRecipe() {
      return this.recipe;
   }
}
