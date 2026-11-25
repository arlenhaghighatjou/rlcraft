package knightminer.inspirations.plugins.jei.cauldron;

import java.util.ArrayList;
import java.util.List;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public interface ICauldronRecipeWrapper extends IRecipeWrapper {
   BlockEnhancedCauldron.CauldronContents getInputType();

   BlockEnhancedCauldron.CauldronContents getOutputType();

   int getInputLevel();

   int getOutputLevel();

   default ItemStack getPotionItem() {
      return ItemStack.EMPTY;
   }

   default List<String> getTooltipStrings(int mouseX, int mouseY) {
      List<String> tooltip = new ArrayList();
      addStringTooltip(tooltip, this.getInputLevel(), 47, 19, mouseX, mouseY);
      addStringTooltip(tooltip, this.getOutputLevel(), 101, 19, mouseX, mouseY);
      return tooltip;
   }

   static void addStringTooltip(List<String> tooltips, int level, int x, int y, int mouseX, int mouseY) {
      if (level == 0 && mouseX >= x && mouseX < x + 10 && mouseY >= y && mouseY < y + 10) {
         tooltips.add(Util.translateFormatted("gui.jei.cauldron.level.empty"));
      }

   }
}
