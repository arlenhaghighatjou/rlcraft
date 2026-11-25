package knightminer.inspirations.recipes.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class DyeIngredientWrapper extends Ingredient {
   private Ingredient base;
   private int lastSizeA = -1;
   private int lastSizeL = -1;
   private ItemStack[] array = null;
   private IntList itemIds = null;

   public DyeIngredientWrapper(Ingredient old) {
      super(0);
      this.base = old;
   }

   @Nonnull
   public ItemStack[] getMatchingStacks() {
      ItemStack[] oreStacks = this.base.getMatchingStacks();
      if (this.array == null || oreStacks.length != this.lastSizeA) {
         this.array = (ItemStack[])Arrays.stream(oreStacks).filter((stack) -> {
            return stack.getItem() != InspirationsRecipes.dyedWaterBottle;
         }).toArray((x$0) -> {
            return new ItemStack[x$0];
         });
      }

      return this.array;
   }

   @Nonnull
   public IntList getValidItemStacksPacked() {
      IntList oreList = this.base.getValidItemStacksPacked();
      if (this.itemIds == null || this.lastSizeL != oreList.size()) {
         int dyedBottle = Item.REGISTRY.getIDForObject(InspirationsRecipes.dyedWaterBottle);
         this.itemIds = new IntArrayList(oreList);
         this.itemIds.removeIf((i) -> {
            return i.intValue() >> 16 == dyedBottle;
         });
      }

      return this.itemIds;
   }

   public boolean apply(@Nullable ItemStack input) {
      return input != null && input.getItem() != InspirationsRecipes.dyedWaterBottle ? this.base.apply(input) : false;
   }

   protected void invalidate() {
      this.array = null;
      this.itemIds = null;
   }

   public boolean isSimple() {
      return this.base.isSimple();
   }
}
