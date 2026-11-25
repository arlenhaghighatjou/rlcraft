package mezz.jei.api.gui;

import net.minecraft.client.Minecraft;

public interface IDrawable {
   int getWidth();

   int getHeight();

   default void draw(Minecraft minecraft) {
      this.draw(minecraft, 0, 0);
   }

   void draw(Minecraft var1, int var2, int var3);
}
