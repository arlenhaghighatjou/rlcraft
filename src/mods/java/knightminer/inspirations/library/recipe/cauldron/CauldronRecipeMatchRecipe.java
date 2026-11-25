package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;
import javax.annotation.Nullable;
import knightminer.inspirations.library.Util;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatch.Match;

public abstract class CauldronRecipeMatchRecipe implements ISimpleCauldronRecipe {
   private RecipeMatch input;
   @Nullable
   protected Boolean boiling;
   private SoundEvent sound;
   private ItemStack result;
   private int levels;

   public CauldronRecipeMatchRecipe(RecipeMatch input, ItemStack result, @Nullable Boolean boiling, int levels, SoundEvent sound) {
      this.input = input;
      this.result = result;
      this.levels = levels;
      this.boiling = boiling;
      this.sound = sound;
   }

   public CauldronRecipeMatchRecipe(RecipeMatch input, ItemStack result, @Nullable Boolean boiling, int levels) {
      this(input, result, boiling, levels, SoundEvents.ENTITY_BOBBER_SPLASH);
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level >= this.levels && this.matches(state) && (this.boiling == null || boiling == this.boiling.booleanValue()) ? this.matches(stack) : false;
   }

   protected abstract boolean matches(ICauldronRecipe.CauldronState var1);

   protected boolean matches(ItemStack stack) {
      return this.input.matches(Util.createNonNullList(stack)).isPresent();
   }

   public ItemStack transformInput(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      NonNullList<ItemStack> list = Util.<ItemStack>createNonNullList(stack);
      RecipeMatch.removeMatch(list, (Match)this.input.matches(list).get());
      return (ItemStack)list.get(0);
   }

   public List<ItemStack> getInput() {
      return this.input.getInputs();
   }

   public ItemStack getResult() {
      return this.result;
   }

   public int getLevel(int level) {
      return level - this.levels;
   }

   public int getInputLevel() {
      return this.levels == 0 ? 1 : this.levels;
   }

   public boolean isBoiling() {
      return this.boiling == Boolean.TRUE;
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return this.sound;
   }
}
