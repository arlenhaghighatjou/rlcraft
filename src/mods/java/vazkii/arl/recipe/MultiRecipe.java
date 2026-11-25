package vazkii.arl.recipe;

import com.google.common.collect.ImmutableList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MultiRecipe extends ModRecipe {
   private List<IRecipe> subRecipes = new LinkedList();
   private ThreadLocal<IRecipe> matched = new ThreadLocal();

   public MultiRecipe(ResourceLocation res) {
      super(res);
   }

   public void addRecipe(IRecipe recipe) {
      this.subRecipes.add(recipe);
   }

   public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
      this.matched.set(null);

      for(IRecipe recipe : this.subRecipes) {
         if (recipe.matches(inv, worldIn)) {
            this.matched.set(recipe);
            return true;
         }
      }

      return false;
   }

   @Nonnull
   public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
      IRecipe match = (IRecipe)this.matched.get();
      return match == null ? ItemStack.EMPTY : match.getCraftingResult(inv);
   }

   public boolean canFit(int width, int height) {
      for(IRecipe recipe : this.subRecipes) {
         if (recipe.canFit(width, height)) {
            return true;
         }
      }

      return false;
   }

   public boolean isDynamic() {
      return true;
   }

   @Nonnull
   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public ImmutableList<IRecipe> getRecipes() {
      return ImmutableList.copyOf(this.subRecipes);
   }
}
