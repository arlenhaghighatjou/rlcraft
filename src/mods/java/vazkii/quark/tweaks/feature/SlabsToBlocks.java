package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vazkii.arl.recipe.MultiRecipe;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class SlabsToBlocks extends Feature {
   public static final Map<IBlockState, ItemStack> slabs = new HashMap();
   private static final List<ItemStack> alreadyFound = new ArrayList();
   public static int originalSize;
   private MultiRecipe multiRecipe;

   public SlabsToBlocks() {
   }

   public void setupConfig() {
      originalSize = this.loadPropInt("Vanilla stack size", "The stack size for the vanilla slab recipe, used for automatically detecting slab recipes", 6);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void registerRecipe(Register<IRecipe> event) {
      ResourceLocation key = new ResourceLocation("quark", "slabs_to_blocks");
      event.getRegistry().register(this.multiRecipe = new MultiRecipe(key));
   }

   public void postInit() {
      label61:
      for(ResourceLocation res : new ArrayList(CraftingManager.REGISTRY.getKeys())) {
         IRecipe recipe = (IRecipe)CraftingManager.REGISTRY.getObject(res);
         if (recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
            NonNullList<Ingredient> recipeItems;
            if (recipe instanceof ShapedRecipes) {
               recipeItems = ((ShapedRecipes)recipe).recipeItems;
            } else {
               recipeItems = recipe.getIngredients();
            }

            ItemStack output = recipe.getRecipeOutput();
            if (!output.isEmpty() && output.getCount() == originalSize) {
               ItemStack singleOut = output.copy();
               singleOut.setCount(1);

               for(ItemStack stack : alreadyFound) {
                  if (ItemStack.areItemStacksEqual(singleOut, stack)) {
                     continue label61;
                  }
               }

               alreadyFound.add(singleOut);
               Item outputItem = output.getItem();
               Block outputBlock = Block.getBlockFromItem(outputItem);
               if (outputBlock instanceof BlockSlab) {
                  ItemStack outStack = StairsMakeMore.findResult(recipeItems, 3);
                  if (!outStack.isEmpty()) {
                     ItemStack outCopy = outStack.copy();
                     if (outCopy.getItemDamage() == 32767) {
                        outCopy.setItemDamage(0);
                     }

                     ItemStack in = output.copy();
                     in.setCount(1);
                     if (in.getItem() instanceof ItemBlock && outCopy.getItem() instanceof ItemBlock) {
                        Block block = Block.getBlockFromItem(outCopy.getItem());
                        slabs.put(block.getStateFromMeta(outCopy.getItemDamage()), in);
                     }

                     RecipeHandler.addShapelessRecipe(this.multiRecipe, outCopy, new Object[]{in, in});
                  }
               }
            }
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
