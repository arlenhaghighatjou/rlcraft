package mezz.jei.input;

import java.awt.Rectangle;
import javax.annotation.Nullable;
import mezz.jei.gui.GuiScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiContainerWrapper implements IShowsRecipeFocuses {
   private final GuiScreenHelper guiScreenHelper;

   public GuiContainerWrapper(GuiScreenHelper guiScreenHelper) {
      this.guiScreenHelper = guiScreenHelper;
   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
      if (!(guiScreen instanceof GuiContainer)) {
         return null;
      } else {
         GuiContainer guiContainer = (GuiContainer)guiScreen;
         Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
         if (slotUnderMouse != null) {
            ItemStack stack = slotUnderMouse.getStack();
            if (!stack.isEmpty()) {
               Rectangle slotArea = new Rectangle(slotUnderMouse.xPos, slotUnderMouse.yPos, 16, 16);
               return ClickedIngredient.create(stack, slotArea);
            }
         }

         return this.guiScreenHelper.getPluginsIngredientUnderMouse(guiContainer, mouseX, mouseY);
      }
   }

   public boolean canSetFocusWithMouse() {
      return false;
   }
}
