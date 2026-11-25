package melonslise.locks.client.gui.sprite;

import melonslise.locks.client.util.LocksClientUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Texture {
   public int startX;
   public int startY;
   public int width;
   public int height;
   public int canvasWidth;
   public int canvasHeight;

   public Texture(int startX, int startY, int width, int height, int canvasWidth, int canvasHeight) {
      this.startX = startX;
      this.startY = startY;
      this.width = width;
      this.height = height;
      this.canvasWidth = canvasWidth;
      this.canvasHeight = canvasHeight;
   }

   public void draw(float x, float y, float alpha) {
      LocksClientUtil.drawTexturedRectangle(x, y, this.startX, this.startY, this.width, this.height, this.canvasWidth, this.canvasHeight, alpha);
   }
}
