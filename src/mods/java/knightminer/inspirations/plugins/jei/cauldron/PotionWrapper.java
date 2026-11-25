package knightminer.inspirations.plugins.jei.cauldron;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredientHelper;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.oredict.OreDictionary;

public abstract class PotionWrapper implements ICauldronRecipeWrapper {
   protected static List<List<PotionIngredient>> potions = ImmutableList.of(PotionIngredientHelper.ALL_POTIONS);
   protected List<List<ItemStack>> filled;
   private ItemStack item;

   public PotionWrapper(ItemStack item) {
      this.item = item;
      this.filled = ImmutableList.of(PotionIngredientHelper.ALL_POTIONS.stream().map((potion) -> {
         return PotionUtils.addPotionToItemStack(item.copy(), potion.getPotion());
      }).collect(Collectors.toList()));
   }

   public ItemStack getPotionItem() {
      return this.item.copy();
   }

   public BlockEnhancedCauldron.CauldronContents getInputType() {
      return BlockEnhancedCauldron.CauldronContents.POTION;
   }

   public BlockEnhancedCauldron.CauldronContents getOutputType() {
      return BlockEnhancedCauldron.CauldronContents.POTION;
   }

   public static class Empty extends PotionWrapper {
      private ItemStack bottle;

      public Empty(ItemStack item, ItemStack bottle) {
         super(item);
         this.bottle = bottle;
      }

      public void getIngredients(IIngredients ingredients) {
         ingredients.setOutput(ItemStack.class, this.bottle);
         ingredients.setInputLists(ItemStack.class, this.filled);
         ingredients.setOutputLists(PotionIngredient.class, potions);
      }

      public int getInputLevel() {
         return 0;
      }

      public int getOutputLevel() {
         return 1;
      }
   }

   public static class Fill extends PotionWrapper {
      private List<List<ItemStack>> bottles;

      public Fill(ItemStack item, String bottle) {
         super(item);
         this.bottles = ImmutableList.of(OreDictionary.getOres(bottle, false));
      }

      public Fill(ItemStack item, ItemStack bottle) {
         super(item);
         this.bottles = ImmutableList.of(ImmutableList.of(bottle));
      }

      public void getIngredients(IIngredients ingredients) {
         ingredients.setInputLists(ItemStack.class, this.bottles);
         ingredients.setOutputLists(ItemStack.class, this.filled);
         ingredients.setInputLists(PotionIngredient.class, potions);
      }

      public int getInputLevel() {
         return 1;
      }

      public int getOutputLevel() {
         return 1;
      }
   }
}
