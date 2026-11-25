package cursedflames.bountifulbaubles.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;

public class GuiToggleButton extends GuiBetterButton {
   public boolean pinned;
   public ResourceLocation off;
   public ResourceLocation offToOn;
   public ResourceLocation on;
   public ResourceLocation onToOff;

   public GuiToggleButton(int buttonId, int x, int y) {
      this(buttonId, x, y, "pin");
   }

   public GuiToggleButton(int buttonId, int x, int y, String type) {
      this(buttonId, x, y, type, false);
   }

   public GuiToggleButton(int buttonId, int x, int y, String type, boolean enabled) {
      super(buttonId, x, y, 16, 16, "");
      this.pinned = false;
      this.pinned = enabled;
      if (type.equals("pin")) {
         this.off = new ResourceLocation("bountifulbaubles", "textures/gui/button/pin_off.png");
         this.offToOn = new ResourceLocation("bountifulbaubles", "textures/gui/button/pin_offtoon.png");
         this.on = new ResourceLocation("bountifulbaubles", "textures/gui/button/pin_on.png");
         this.onToOff = new ResourceLocation("bountifulbaubles", "textures/gui/button/pin_ontooff.png");
      } else if (type.equals("visible")) {
         this.off = new ResourceLocation("bountifulbaubles", "textures/gui/button/visible_off.png");
         this.offToOn = new ResourceLocation("bountifulbaubles", "textures/gui/button/visible_off_hover.png");
         this.on = new ResourceLocation("bountifulbaubles", "textures/gui/button/visible_on.png");
         this.onToOff = new ResourceLocation("bountifulbaubles", "textures/gui/button/visible_on_hover.png");
      }

   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int i = this.getHoverState(this.hovered);
         if (i == 2) {
            if (this.pinned) {
               mc.getTextureManager().bindTexture(this.onToOff);
            } else {
               mc.getTextureManager().bindTexture(this.offToOn);
            }
         } else if (i != 0 && this.pinned) {
            mc.getTextureManager().bindTexture(this.on);
         } else {
            mc.getTextureManager().bindTexture(this.off);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         this.mouseDragged(mc, mouseX, mouseY);
      }

   }
}
