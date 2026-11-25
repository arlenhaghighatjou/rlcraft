package slimeknights.mantle.client.gui;

import java.awt.Rectangle;
import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiModule extends GuiContainer {
   protected final GuiMultiModule parent;
   protected final boolean right;
   protected final boolean bottom;
   public int yOffset = 0;
   public int xOffset = 0;

   public GuiModule(GuiMultiModule parent, Container container, boolean right, boolean bottom) {
      super(container);
      this.parent = parent;
      this.right = right;
      this.bottom = bottom;
   }

   public int guiRight() {
      return this.guiLeft + this.xSize;
   }

   public int guiBottom() {
      return this.guiTop + this.ySize;
   }

   public Rectangle getArea() {
      return new Rectangle(this.guiLeft, this.guiTop, this.xSize, this.ySize);
   }

   public void initGui() {
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
   }

   public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
      if (this.right) {
         this.guiLeft = parentX + parentSizeX;
      } else {
         this.guiLeft = parentX - this.xSize;
      }

      if (this.bottom) {
         this.guiTop = parentY + parentSizeY - this.ySize;
      } else {
         this.guiTop = parentY;
      }

      this.guiLeft += this.xOffset;
      this.guiTop += this.yOffset;
   }

   public boolean shouldDrawSlot(Slot slot) {
      return true;
   }

   public boolean isMouseInModule(int mouseX, int mouseY) {
      return mouseX >= this.guiLeft && mouseX < this.guiRight() && mouseY >= this.guiTop && mouseY < this.guiBottom();
   }

   public boolean isMouseOverFullSlot(int mouseX, int mouseY) {
      for(Slot slot : this.inventorySlots.inventorySlots) {
         if (this.parent.isMouseOverSlot(slot, mouseX, mouseY) && slot.getHasStack()) {
            return true;
         }
      }

      return false;
   }

   public void handleDrawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
   }

   public void handleDrawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.drawGuiContainerForegroundLayer(mouseX, mouseY);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      return false;
   }

   public boolean handleMouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      return false;
   }

   public boolean handleMouseReleased(int mouseX, int mouseY, int state) {
      return false;
   }
}
