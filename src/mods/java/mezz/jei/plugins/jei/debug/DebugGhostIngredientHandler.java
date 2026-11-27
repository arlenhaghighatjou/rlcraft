package mezz.jei.plugins.jei.debug;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.Internal;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.util.Log;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DebugGhostIngredientHandler<T extends GuiContainer> implements IGhostIngredientHandler<T> {
   public DebugGhostIngredientHandler() {
   }

   public <I> List<IGhostIngredientHandler.Target<I>> getTargets(T gui, I ingredient, boolean doStart) {
      List<IGhostIngredientHandler.Target<I>> targets = new ArrayList();
      targets.add(new DebugGhostIngredientHandler.DebugInfoTarget("Got an Ingredient", new Rectangle(0, 0, 20, 20)));
      if (doStart) {
         IIngredientHelper<I> ingredientHelper = Internal.getIngredientRegistry().getIngredientHelper(ingredient);
         Log.get().info("Ghost Ingredient Handling Starting with {}", ingredientHelper.getErrorInfo(ingredient));
         targets.add(new DebugGhostIngredientHandler.DebugInfoTarget("Got an Ingredient", new Rectangle(20, 20, 20, 20)));
      }

      if (ingredient instanceof ItemStack) {
         boolean even = true;

         for(Slot slot : gui.inventorySlots.inventorySlots) {
            if (even) {
               Rectangle area = new Rectangle(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, 16, 16);
               targets.add(new DebugGhostIngredientHandler.DebugInfoTarget("Got an Ingredient in Gui", area));
            }

            even = !even;
         }
      }

      return targets;
   }

   public void onComplete() {
      Log.get().info("Ghost Ingredient Handling Complete");
   }

   private static class DebugInfoTarget<I> implements IGhostIngredientHandler.Target<I> {
      private final String message;
      private final Rectangle rectangle;

      public DebugInfoTarget(String message, Rectangle rectangle) {
         this.message = message;
         this.rectangle = rectangle;
      }

      public Rectangle getArea() {
         return this.rectangle;
      }

      public void accept(I ingredient) {
         IIngredientHelper<I> ingredientHelper = Internal.getIngredientRegistry().getIngredientHelper(ingredient);
         Log.get().info("{}: {}", this.message, ingredientHelper.getErrorInfo(ingredient));
      }
   }
}
