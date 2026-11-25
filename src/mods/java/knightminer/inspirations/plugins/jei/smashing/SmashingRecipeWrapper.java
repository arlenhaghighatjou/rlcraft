package knightminer.inspirations.plugins.jei.smashing;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import knightminer.inspirations.library.InspirationsRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.client.CreativeTab;

public class SmashingRecipeWrapper implements IRecipeWrapper {
   protected final List<List<ItemStack>> input;
   protected final List<ItemStack> output;

   public SmashingRecipeWrapper(ItemStack input, ItemStack output) {
      this.input = ImmutableList.of(ImmutableList.of(input));
      this.output = ImmutableList.of(output);
   }

   public SmashingRecipeWrapper(Block input, ItemStack output) {
      NonNullList<ItemStack> inputList = NonNullList.create();
      input.getSubBlocks(CreativeTab.SEARCH, inputList);
      inputList.removeIf((stack) -> {
         return InspirationsRegistry.hasAnvilSmashStateResult(input.getStateFromMeta(stack.getMetadata()));
      });
      this.input = ImmutableList.of(inputList);
      this.output = ImmutableList.of(output);
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInputLists(ItemStack.class, this.input);
      ingredients.setOutputs(ItemStack.class, this.output);
   }
}
