package slimeknights.mantle.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiWidget {
   public int xPos;
   public int yPos;
   public int height;
   public int width;

   public GuiWidget() {
   }

   public abstract void draw();

   public void setPosition(int x, int y) {
      this.xPos = x;
      this.yPos = y;
   }

   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
   }
}
