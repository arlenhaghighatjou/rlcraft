package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundEvent;

public class FillCauldronFromPotion implements ICauldronRecipe {
   private ItemStack bottle;
   private Item potion;

   public FillCauldronFromPotion(Item potion, ItemStack bottle) {
      this.bottle = bottle;
      this.potion = potion;
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level < InspirationsRegistry.getCauldronMax() && (level == 0 || state.getPotion() != null) && stack.getItem() == this.potion;
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return this.bottle.copy();
   }

   public int getLevel(int level) {
      return level + 1;
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      PotionType inputType = PotionUtils.getPotionFromItem(stack);
      if (level == 0) {
         return ICauldronRecipe.CauldronState.potion(inputType);
      } else {
         return state.getPotion() != inputType ? ICauldronRecipe.CauldronState.potion(PotionTypes.THICK) : state;
      }
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level > 0 && PotionUtils.getPotionFromItem(stack) != state.getPotion() ? SoundEvents.BLOCK_FIRE_EXTINGUISH : SoundEvents.ITEM_BOTTLE_EMPTY;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
