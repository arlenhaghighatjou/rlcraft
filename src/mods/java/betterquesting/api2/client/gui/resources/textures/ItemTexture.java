package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ItemTexture implements IGuiTexture {
   private static final IGuiColor defColor = new GuiColorStatic(255, 255, 255, 255);
   private final BigItemStack stack;
   private final boolean showCount;
   private final boolean keepAspect;
   private float zDepth;
   private final IGuiRect bounds;

   public ItemTexture(BigItemStack stack) {
      this(stack, false, true);
   }

   public ItemTexture(BigItemStack stack, boolean showCount, boolean keepAspect) {
      this.zDepth = 16.0F;
      this.bounds = new GuiRectangle(0, 0, 16, 16);
      this.stack = stack;
      this.showCount = showCount;
      this.keepAspect = keepAspect;
   }

   public ItemTexture setDepth(float z) {
      this.zDepth = z;
      return this;
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick) {
      this.drawTexture(x, y, width, height, zLevel, partialTick, defColor);
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0) {
         float sx = (float)width / 16.0F;
         float sy = (float)height / 16.0F;
         double dx = 0.0D;
         double dy = 0.0D;
         if (this.keepAspect) {
            float sa = Math.min(sx, sy);
            dx = Math.floor((double)((sx - sa) * 8.0F));
            dy = Math.floor((double)((sy - sa) * 8.0F));
            sx = sa;
            sy = sa;
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate((double)x + dx, (double)y + dy, 0.0D);
         GlStateManager.scale(sx, sy, 1.0F);
         color.applyGlColor();
         RenderUtils.RenderItemStack(Minecraft.getMinecraft(), this.stack.getBaseStack(), 0, 0, this.zDepth, this.showCount && this.stack.stackSize > 1 ? "" + this.stack.stackSize : "", -1);
         GlStateManager.popMatrix();
      }
   }

   public ResourceLocation getTexture() {
      return PresetTexture.TX_NULL;
   }

   public IGuiRect getBounds() {
      return this.bounds;
   }
}
