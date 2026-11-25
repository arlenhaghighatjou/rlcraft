package knightminer.inspirations.plugins.jei.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import knightminer.inspirations.library.recipe.TextureRecipe;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TextureRecipeWrapper implements IRecipeWrapper, IShapedCraftingRecipeWrapper, ICustomCraftingRecipeWrapper {
   private final TextureRecipe recipe;
   private final int width;
   private final int height;
   private final List<List<ItemStack>> outputs;

   public TextureRecipeWrapper(TextureRecipe recipe) {
      this.recipe = recipe;

      for(Object input : this.recipe.getIngredients()) {
         if (input instanceof ItemStack) {
            ItemStack itemStack = (ItemStack)input;
            if (itemStack.getCount() != 1) {
               itemStack.setCount(1);
            }
         }
      }

      this.width = recipe.getRecipeWidth();
      this.height = recipe.getRecipeHeight();
      Builder<ItemStack> builder = ImmutableList.builder();
      Block block = Block.getBlockFromItem(recipe.getRecipeOutput().getItem());

      for(ItemStack stack : recipe.texture.getMatchingStacks()) {
         Block textureBlock = Block.getBlockFromItem(stack.getItem());
         if (stack.getItemDamage() == 32767) {
            for(ItemStack sub : JEIPlugin.jeiHelpers.getStackHelper().getSubtypes(stack)) {
               builder.add(TextureBlockUtil.createTexturedStack(block, recipe.getRecipeOutput().getItemDamage(), textureBlock, sub.getItemDamage()));
            }
         } else {
            builder.add(TextureBlockUtil.createTexturedStack(block, recipe.getRecipeOutput().getItemDamage(), textureBlock, stack.getItemDamage()));
         }
      }

      this.outputs = ImmutableList.of(builder.build());
   }

   public void getIngredients(IIngredients ingredients) {
      IStackHelper stackHelper = JEIPlugin.jeiHelpers.getStackHelper();
      List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(this.recipe.getIngredients());
      ingredients.setInputLists(ItemStack.class, inputs);
      if (!this.outputs.isEmpty()) {
         ingredients.setOutputLists(ItemStack.class, this.outputs);
      }

   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private boolean isOutputBlock(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         for(ItemStack output : this.recipe.texture.getMatchingStacks()) {
            if (OreDictionary.itemMatches(output, stack, false)) {
               return true;
            }
         }

         return false;
      }
   }

   public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
      List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
      List<ItemStack> outputs = (List)ingredients.getOutputs(ItemStack.class).get(0);
      IFocus<?> ifocus = recipeLayout.getFocus();
      if (ifocus != null && ifocus.getValue() instanceof ItemStack) {
         IGuiIngredientGroup<ItemStack> guiIngredients = recipeLayout.getIngredientsGroup(ItemStack.class);
         ItemStack focus = (ItemStack)ifocus.getValue();
         Mode mode = ifocus.getMode();
         if (mode == Mode.INPUT && this.isOutputBlock(focus)) {
            ItemStack output = this.recipe.getPlainRecipeOutput();
            Block block = Block.getBlockFromItem(output.getItem());
            ItemStack outputFocus = TextureBlockUtil.createTexturedStack(block, output.getItemDamage(), Block.getBlockFromItem(focus.getItem()), focus.getItemDamage());
            guiIngredients.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(Mode.OUTPUT, outputFocus));
         } else if (mode == Mode.OUTPUT) {
            ItemStack legs = TextureBlockUtil.getStackTexture(focus);
            if (!legs.isEmpty()) {
               guiIngredients.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(Mode.INPUT, legs));
            }
         }
      }

      JEIPlugin.craftingGridHelper.setInputs(guiItemStacks, inputs, this.getWidth(), this.getHeight());
      recipeLayout.getItemStacks().set(0, outputs);
   }
}
