package mezz.jei.plugins.vanilla.anvil;

import java.util.Map;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IGuiIngredient;
import net.minecraft.item.ItemStack;

public class AnvilRecipeDisplayData {
   @Nullable
   private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients = null;
   @Nullable
   private ItemStack lastLeftStack;
   @Nullable
   private ItemStack lastRightStack;
   private int lastCost;

   public AnvilRecipeDisplayData() {
   }

   @Nullable
   public Map<Integer, ? extends IGuiIngredient<ItemStack>> getCurrentIngredients() {
      return this.currentIngredients;
   }

   public void setCurrentIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients) {
      this.currentIngredients = currentIngredients;
   }

   @Nullable
   public ItemStack getLastLeftStack() {
      return this.lastLeftStack;
   }

   @Nullable
   public ItemStack getLastRightStack() {
      return this.lastRightStack;
   }

   public int getLastCost() {
      return this.lastCost;
   }

   public void setLast(ItemStack leftStack, ItemStack rightStack, int lastCost) {
      this.lastLeftStack = leftStack;
      this.lastRightStack = rightStack;
      this.lastCost = lastCost;
   }
}
