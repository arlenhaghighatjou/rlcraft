package mezz.jei.gui.elements;

import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.client.Minecraft;

public class DrawableBlank implements IDrawableStatic, IDrawableAnimated {
   private final int width;
   private final int height;

   public DrawableBlank(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset) {
   }
}
