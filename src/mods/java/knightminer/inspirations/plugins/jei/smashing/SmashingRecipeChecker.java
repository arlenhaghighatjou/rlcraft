package knightminer.inspirations.plugins.jei.smashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SmashingRecipeChecker {
   public SmashingRecipeChecker() {
   }

   public static List<SmashingRecipeWrapper> getRecipes() {
      List<SmashingRecipeWrapper> recipes = new ArrayList();

      for(Entry<IBlockState, IBlockState> entry : InspirationsRegistry.getAllAnvilStateSmashing()) {
         ItemStack input = Util.getStackFromState((IBlockState)entry.getKey());
         ItemStack output = Util.getStackFromState((IBlockState)entry.getValue());
         if (!input.isEmpty() && !output.isEmpty()) {
            recipes.add(new SmashingRecipeWrapper(input, output));
         }
      }

      for(Entry<Block, IBlockState> entry : InspirationsRegistry.getAllAnvilBlockSmashing()) {
         ItemStack output = Util.getStackFromState((IBlockState)entry.getValue());
         Block block = (Block)entry.getKey();
         if (block != null && block != Blocks.AIR && !output.isEmpty()) {
            recipes.add(new SmashingRecipeWrapper((Block)entry.getKey(), output));
         }
      }

      return recipes;
   }
}
