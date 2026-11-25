package mezz.jei.plugins.vanilla.anvil;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class AnvilRecipeWrapper implements IRecipeWrapper {
   private final List<List<ItemStack>> inputs;
   private final List<List<ItemStack>> output;

   public AnvilRecipeWrapper(List<ItemStack> leftInput, List<ItemStack> rightInputs, List<ItemStack> outputs) {
      this.inputs = ImmutableList.of(leftInput, rightInputs);
      this.output = Collections.singletonList(outputs);
   }

   public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      AnvilRecipeDisplayData data = AnvilRecipeDataCache.getDisplayData(this);
      Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients = data.getCurrentIngredients();
      if (currentIngredients != null) {
         ItemStack newLeftStack = (ItemStack)((IGuiIngredient)currentIngredients.get(Integer.valueOf(0))).getDisplayedIngredient();
         ItemStack newRightStack = (ItemStack)((IGuiIngredient)currentIngredients.get(Integer.valueOf(1))).getDisplayedIngredient();
         if (newLeftStack != null && newRightStack != null) {
            ItemStack lastLeftStack = data.getLastLeftStack();
            ItemStack lastRightStack = data.getLastRightStack();
            int lastCost = data.getLastCost();
            if (lastLeftStack == null || lastRightStack == null || !ItemStack.areItemStacksEqual(lastLeftStack, newLeftStack) || !ItemStack.areItemStacksEqual(lastRightStack, newRightStack)) {
               lastCost = AnvilRecipeMaker.findLevelsCost(newLeftStack, newRightStack);
               data.setLast(newLeftStack, newRightStack, lastCost);
            }

            if (lastCost != 0) {
               String costText = lastCost < 0 ? "err" : Integer.toString(lastCost);
               String text = I18n.format("container.repair.cost", new Object[]{costText});
               int mainColor = -8323296;
               EntityPlayerSP player = minecraft.player;
               if (player != null && (lastCost >= 40 || lastCost > player.experienceLevel) && !player.capabilities.isCreativeMode) {
                  mainColor = -40864;
               }

               this.drawRepairCost(minecraft, text, mainColor, recipeWidth);
            }

         }
      }
   }

   private void drawRepairCost(Minecraft minecraft, String text, int mainColor, int recipeWidth) {
      int shadowColor = -16777216 | (mainColor & 16579836) >> 2;
      int width = minecraft.fontRenderer.getStringWidth(text);
      int x = recipeWidth - 2 - width;
      int y = 27;
      if (minecraft.fontRenderer.getUnicodeFlag()) {
         Gui.drawRect(x - 2, y - 2, x + width + 2, y + 10, -16777216);
         Gui.drawRect(x - 1, y - 1, x + width + 1, y + 9, -12895429);
      } else {
         minecraft.fontRenderer.drawString(text, x + 1, y, shadowColor);
         minecraft.fontRenderer.drawString(text, x, y + 1, shadowColor);
         minecraft.fontRenderer.drawString(text, x + 1, y + 1, shadowColor);
      }

      minecraft.fontRenderer.drawString(text, x, y, mainColor);
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
      ingredients.setOutputLists(VanillaTypes.ITEM, this.output);
   }
}
