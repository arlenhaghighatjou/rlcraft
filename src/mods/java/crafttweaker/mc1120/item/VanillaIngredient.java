package crafttweaker.mc1120.item;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

@MethodsReturnNonnullByDefault
public class VanillaIngredient extends Ingredient {
   private IIngredient ingredient;
   private IntList stacks = null;

   public VanillaIngredient(@Nonnull IIngredient ingredient) {
      super(new ItemStack[0]);
      this.ingredient = ingredient;
   }

   public ItemStack[] getMatchingStacks() {
      return CraftTweakerMC.getItemStacks(this.ingredient.getItemArray());
   }

   public boolean apply(@Nullable ItemStack itemStack) {
      return this.ingredient.matches(CraftTweakerMC.getIItemStackForMatching(itemStack));
   }

   public IntList getValidItemStacksPacked() {
      if (this.stacks != null) {
         return this.stacks;
      } else {
         IntList list = new IntArrayList();
         if (this.ingredient != null) {
            NonNullList<ItemStack> itemStacks = NonNullList.create();

            for(ItemStack stack : CraftTweakerMC.getItemStacks(this.ingredient.getItemArray())) {
               stack.getItem().getSubItems(CreativeTabs.SEARCH, itemStacks);
            }

            itemStacks.stream().mapToInt(RecipeItemHelper::pack).forEach(list::add);
         }

         return this.stacks = list;
      }
   }

   protected void invalidate() {
      this.stacks = null;
   }

   public boolean isSimple() {
      return false;
   }

   public boolean test(@Nullable ItemStack input) {
      return this.apply(input);
   }

   public IIngredient getIngredient() {
      return this.ingredient;
   }
}
