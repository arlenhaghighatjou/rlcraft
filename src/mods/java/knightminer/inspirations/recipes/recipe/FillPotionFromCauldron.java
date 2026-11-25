package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.util.RecipeMatch;

public class FillPotionFromCauldron implements ICauldronRecipe {
   private RecipeMatch bottle;
   private Item potion;

   public FillPotionFromCauldron(Item potion, ItemStack bottle) {
      this.bottle = RecipeMatch.of(bottle);
      this.potion = potion;
   }

   public FillPotionFromCauldron(Item potion, String bottle) {
      this.bottle = RecipeMatch.of(bottle);
      this.potion = potion;
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      if (level == 0) {
         return false;
      } else {
         return state.getPotion() != null && this.bottle.matches(Util.createNonNullList(stack)).isPresent();
      }
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return PotionUtils.addPotionToItemStack(new ItemStack(this.potion), state.getPotion());
   }

   public int getLevel(int level) {
      return level - 1;
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return SoundEvents.ITEM_BOTTLE_FILL;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
