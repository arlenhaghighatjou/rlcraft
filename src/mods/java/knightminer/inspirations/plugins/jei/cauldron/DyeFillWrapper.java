package knightminer.inspirations.plugins.jei.cauldron;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredientHelper;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class DyeFillWrapper implements ICauldronRecipeWrapper {
   private static List<List<DyeIngredient>> dyes = ImmutableList.of(DyeIngredientHelper.ALL_DYES);
   private static List<List<ItemStack>> filled = ImmutableList.of(Arrays.stream(EnumDyeColor.values()).map((color) -> {
      return new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, color.getDyeDamage());
   }).collect(Collectors.toList()));
   private static ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
   private boolean fill;

   public DyeFillWrapper(boolean fill) {
      this.fill = fill;
   }

   public void getIngredients(IIngredients ingredients) {
      if (this.fill) {
         ingredients.setInput(ItemStack.class, bottle);
         ingredients.setOutputLists(ItemStack.class, filled);
         ingredients.setInputLists(DyeIngredient.class, dyes);
      } else {
         ingredients.setOutput(ItemStack.class, bottle);
         ingredients.setInputLists(ItemStack.class, filled);
         ingredients.setOutputLists(DyeIngredient.class, dyes);
      }

   }

   public BlockEnhancedCauldron.CauldronContents getInputType() {
      return BlockEnhancedCauldron.CauldronContents.DYE;
   }

   public BlockEnhancedCauldron.CauldronContents getOutputType() {
      return BlockEnhancedCauldron.CauldronContents.DYE;
   }

   public int getInputLevel() {
      return this.fill ? 1 : 0;
   }

   public int getOutputLevel() {
      return this.fill ? 0 : 1;
   }
}
