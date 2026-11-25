package mezz.jei.gui.elements;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class GuiIconButton extends GuiButton {
   private final Consumer<List<String>> tooltipCallback;
   private final Supplier<IDrawable> iconSupplier;
   private final IMouseClickedButtonCallback mouseClickCallback;

   public GuiIconButton(int buttonId, IDrawable icon, IMouseClickedButtonCallback mouseClickCallback) {
      this(buttonId, (tooltip) -> {
      }, () -> {
         return icon;
      }, mouseClickCallback);
   }

   public GuiIconButton(int buttonId, Consumer<List<String>> tooltipCallback, Supplier<IDrawable> iconSupplier, IMouseClickedButtonCallback mouseClickCallback) {
      super(buttonId, 0, 0, 0, 0, "");
      this.tooltipCallback = tooltipCallback;
      this.iconSupplier = iconSupplier;
      this.mouseClickCallback = mouseClickCallback;
   }

   public void updateBounds(Rectangle area) {
      this.x = area.x;
      this.y = area.y;
      this.width = area.width;
      this.height = area.height;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int i = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GuiHelper guiHelper = Internal.getHelpers().getGuiHelper();
         DrawableNineSliceTexture texture = guiHelper.getButtonForState(i);
         texture.draw(mc, this.x, this.y, this.width, this.height);
         this.mouseDragged(mc, mouseX, mouseY);
         int color = 14737632;
         if (!this.enabled) {
            color = 10526880;
         } else if (this.hovered) {
            color = 16777120;
         }

         color = color | -16777216;
         float red = (float)(color >> 16 & 255) / 255.0F;
         float blue = (float)(color >> 8 & 255) / 255.0F;
         float green = (float)(color & 255) / 255.0F;
         float alpha = (float)(color >> 24 & 255) / 255.0F;
         GlStateManager.color(red, blue, green, alpha);
         IDrawable icon = (IDrawable)this.iconSupplier.get();
         double xOffset = (double)this.x + (double)(this.width - icon.getWidth()) / 2.0D;
         double yOffset = (double)this.y + (double)(this.height - icon.getHeight()) / 2.0D;
         GlStateManager.pushMatrix();
         GlStateManager.translate(xOffset, yOffset, 0.0D);
         icon.draw(mc);
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.isMouseOver()) {
         List<String> tooltip = new ArrayList();
         this.tooltipCallback.accept(tooltip);
         TooltipRenderer.drawHoveringText(minecraft, tooltip, mouseX, mouseY, 150);
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY) && this.mouseClickCallback.mousePressed(mc, mouseX, mouseY)) {
         this.playPressSound(mc.getSoundHandler());
         return true;
      } else {
         return false;
      }
   }
}
