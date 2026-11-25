package vazkii.arl.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class BlacklistOreIngredient extends Ingredient {
   private NonNullList<ItemStack> ores;
   private IntList itemIds = null;
   private ItemStack[] array = null;
   private Predicate<ItemStack> predicate;
   private int lastSizeA = -1;
   private int lastSizeL = -1;

   public BlacklistOreIngredient(String ore, Predicate<ItemStack> predicate) {
      super(0);
      this.ores = OreDictionary.getOres(ore);
      this.predicate = predicate.negate();
   }

   @Nonnull
   public ItemStack[] getMatchingStacks() {
      if (this.array == null || this.lastSizeA != this.ores.size()) {
         NonNullList<ItemStack> lst = NonNullList.create();

         for(ItemStack itemstack : this.ores) {
            if (itemstack.getMetadata() == 32767) {
               NonNullList<ItemStack> newList = NonNullList.create();
               itemstack.getItem().getSubItems(CreativeTabs.SEARCH, newList);

               for(ItemStack stack : newList) {
                  if (this.predicate.test(stack)) {
                     lst.add(stack);
                  }
               }
            } else if (this.predicate.test(itemstack)) {
               lst.add(itemstack);
            }
         }

         this.array = (ItemStack[])lst.toArray(new ItemStack[0]);
         this.lastSizeA = this.ores.size();
      }

      return this.array;
   }

   @Nonnull
   public IntList getValidItemStacksPacked() {
      if (this.itemIds == null || this.lastSizeL != this.ores.size()) {
         this.itemIds = new IntArrayList(this.ores.size());

         for(ItemStack itemstack : this.ores) {
            if (itemstack.getMetadata() == 32767) {
               NonNullList<ItemStack> lst = NonNullList.create();
               itemstack.getItem().getSubItems(CreativeTabs.SEARCH, lst);

               for(ItemStack item : lst) {
                  if (this.predicate.test(item)) {
                     this.itemIds.add(RecipeItemHelper.pack(item));
                  }
               }
            } else if (this.predicate.test(itemstack)) {
               this.itemIds.add(RecipeItemHelper.pack(itemstack));
            }
         }

         this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
         this.lastSizeL = this.ores.size();
      }

      return this.itemIds;
   }

   public boolean apply(@Nullable ItemStack input) {
      if (input != null && this.predicate.test(input)) {
         for(ItemStack target : this.ores) {
            if (OreDictionary.itemMatches(target, input, false)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   protected void invalidate() {
      this.itemIds = null;
      this.array = null;
   }

   public boolean isSimple() {
      return true;
   }
}
