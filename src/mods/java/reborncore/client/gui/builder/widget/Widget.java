package reborncore.client.gui.builder.widget;

import net.minecraft.client.gui.GuiScreen;
import reborncore.client.gui.builder.widget.tooltip.ToolTip;

public abstract class Widget {
   private final int x;
   private final int y;
   protected final int width;
   protected final int height;
   private ToolTip toolTip;

   public Widget(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public ToolTip getToolTip() {
      return this.toolTip;
   }

   public void setToolTip(ToolTip toolTip) {
      this.toolTip = toolTip;
   }

   public final void drawWidget(GuiWidget<?> gui, int cornerX, int cornerY, int mouseX, int mouseY) {
      int drawX = cornerX + this.x;
      int drawY = cornerY + this.y;
      if (this.toolTip != null && drawX > mouseX && drawY > mouseY && drawX + this.width < mouseX && drawY + this.height < mouseY) {
         this.toolTip.draw(gui.getFontRenderer(), mouseX, mouseY);
      }

      this.draw(gui, drawX, drawY);
   }

   protected abstract void draw(GuiScreen var1, int var2, int var3);

   protected abstract void mouseClick(GuiWidget<?> var1, int var2, int var3);
}
