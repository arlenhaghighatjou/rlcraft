package knightminer.inspirations.plugins.jei.cauldron;

import com.google.common.collect.ImmutableList;
import java.util.List;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CauldronRecipeWrapper implements ICauldronRecipeWrapper {
   private static final FluidStack WATER_STACK = new FluidStack(FluidRegistry.WATER, 1);
   protected final List<List<ItemStack>> input;
   protected final FluidStack inputFluid;
   protected final EnumDyeColor inputColor;
   protected final PotionType inputPotion;
   protected final int inputLevel;
   protected final boolean boiling;
   protected final List<ItemStack> output;
   protected final FluidStack outputFluid;
   protected final EnumDyeColor outputColor;
   protected final PotionType outputPotion;
   protected final int outputLevel;

   public CauldronRecipeWrapper(ISimpleCauldronRecipe recipe) {
      this.input = ImmutableList.of(recipe.getInput());
      this.inputLevel = MathHelper.clamp(recipe.getInputLevel(), 0, InspirationsRegistry.getCauldronMax());
      this.boiling = recipe.isBoiling();
      this.output = ImmutableList.of(recipe.getResult());
      this.outputLevel = MathHelper.clamp(recipe.getLevel(this.inputLevel), 0, InspirationsRegistry.getCauldronMax());
      if (this.inputLevel == 0) {
         this.inputFluid = null;
         this.inputColor = null;
         this.inputPotion = null;
      } else {
         Object inputState = recipe.getInputState();
         this.inputFluid = inputState instanceof Fluid ? new FluidStack((Fluid)inputState, this.inputLevel) : null;
         this.inputColor = inputState instanceof EnumDyeColor ? (EnumDyeColor)inputState : null;
         this.inputPotion = inputState instanceof PotionType ? (PotionType)inputState : null;
      }

      if (this.outputLevel == 0) {
         this.outputFluid = null;
         this.outputColor = null;
         this.outputPotion = null;
      } else {
         Object outputState = recipe.getState();
         this.outputFluid = outputState instanceof Fluid ? new FluidStack((Fluid)outputState, this.outputLevel) : null;
         this.outputColor = outputState instanceof EnumDyeColor ? (EnumDyeColor)outputState : null;
         this.outputPotion = outputState instanceof PotionType ? (PotionType)outputState : null;
      }

   }

   public CauldronRecipeWrapper(ItemStack input, ItemStack output) {
      this(ImmutableList.of(input), output);
   }

   public CauldronRecipeWrapper(List<ItemStack> input, ItemStack output) {
      this.input = ImmutableList.of(input);
      this.output = ImmutableList.of(output);
      this.inputLevel = 1;
      this.outputLevel = 0;
      this.boiling = false;
      this.inputFluid = WATER_STACK;
      this.outputFluid = null;
      this.inputColor = this.outputColor = null;
      this.inputPotion = this.outputPotion = null;
   }

   public BlockEnhancedCauldron.CauldronContents getInputType() {
      return getType(this.inputLevel, this.inputFluid, this.inputColor, this.inputPotion);
   }

   public BlockEnhancedCauldron.CauldronContents getOutputType() {
      return getType(this.outputLevel, this.outputFluid, this.outputColor, this.outputPotion);
   }

   public int getInputLevel() {
      return this.inputLevel;
   }

   public int getOutputLevel() {
      return this.outputLevel;
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInputLists(ItemStack.class, this.input);
      if (this.inputFluid != null) {
         ingredients.setInput(FluidStack.class, this.inputFluid);
      }

      if (this.inputColor != null) {
         ingredients.setInput(DyeIngredient.class, new DyeIngredient(this.inputColor));
      }

      if (this.inputPotion != null) {
         ingredients.setInput(PotionIngredient.class, new PotionIngredient(this.inputPotion));
      }

      ingredients.setOutputs(ItemStack.class, this.output);
      if (this.outputFluid != null) {
         ingredients.setOutput(FluidStack.class, this.outputFluid);
      }

      if (this.outputColor != null) {
         ingredients.setOutput(DyeIngredient.class, new DyeIngredient(this.outputColor));
      }

      if (this.outputPotion != null) {
         ingredients.setOutput(PotionIngredient.class, new PotionIngredient(this.outputPotion));
      }

   }

   public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      if (this.boiling) {
         JEIPlugin.cauldron.fire.draw(minecraft, 45, 36);
      }

   }

   public List<String> getTooltipStrings(int mouseX, int mouseY) {
      List<String> tooltip = super.getTooltipStrings(mouseX, mouseY);
      if (this.boiling && mouseX > 45 && mouseX <= 58 && mouseY > 36 && mouseY <= 48) {
         tooltip.add(Util.translate("gui.jei.cauldron.boiling"));
      }

      return tooltip;
   }

   private static BlockEnhancedCauldron.CauldronContents getType(int level, FluidStack fluid, EnumDyeColor color, PotionType potion) {
      if (level == 0) {
         return null;
      } else if (fluid != null) {
         return BlockEnhancedCauldron.CauldronContents.FLUID;
      } else if (color != null) {
         return BlockEnhancedCauldron.CauldronContents.DYE;
      } else {
         return potion != null ? BlockEnhancedCauldron.CauldronContents.POTION : null;
      }
   }
}
