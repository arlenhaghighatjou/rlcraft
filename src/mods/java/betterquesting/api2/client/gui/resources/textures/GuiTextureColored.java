package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.minecraft.util.ResourceLocation;

public class GuiTextureColored implements IGuiTexture {
   private final IGuiTexture texture;
   private final IGuiColor color;

   public GuiTextureColored(IGuiTexture texture, IGuiColor color) {
      this.texture = texture;
      this.color = color;
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick) {
      if (width > 0 && height > 0) {
         this.texture.drawTexture(x, y, width, height, zDepth, partialTick, this.color);
      }
   }

   /** @deprecated */
   @Deprecated
   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor c) {
      if (width > 0 && height > 0) {
         this.texture.drawTexture(x, y, width, height, zDepth, partialTick, c);
      }
   }

   public ResourceLocation getTexture() {
      return this.texture.getTexture();
   }

   public IGuiRect getBounds() {
      return this.texture.getBounds();
   }
}
