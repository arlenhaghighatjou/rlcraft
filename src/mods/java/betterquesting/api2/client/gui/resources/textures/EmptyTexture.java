package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.minecraft.util.ResourceLocation;

public class EmptyTexture implements IGuiTexture {
   public EmptyTexture() {
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick) {
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor color) {
   }

   public ResourceLocation getTexture() {
      return null;
   }

   public IGuiRect getBounds() {
      return null;
   }
}
