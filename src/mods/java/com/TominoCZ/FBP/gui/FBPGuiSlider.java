package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.math.MathHelper;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class FBPGuiSlider extends GuiButton {
   public double value;
   double sliderPosX;
   double mouseGap;
   boolean dragging = false;
   boolean mouseDown = false;

   public FBPGuiSlider(int x, int y, double value) {
      super(Integer.MIN_VALUE, x, y, "");
      this.value = value;
      this.width = 200;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      FontRenderer fontrenderer = mc.fontRenderer;
      mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.enabled ? 1 : 0;
      int j = this.enabled ? (!this.isMouseOverSlider(mouseX, mouseY) && !this.dragging ? 1 : 2) : 0;
      GlStateManager.enableBlend();
      this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + 6 - 9, fontrenderer.getColorCode('f'));
      mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      this.drawTexturedModalRect(this.x, this.y, 0, 60 + i * 20, this.width / 2, this.height);
      this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 60 + i * 20, this.width / 2, this.height);
      boolean tmpMouseDown = Mouse.isButtonDown(0);
      if (!tmpMouseDown && this.mouseDown && this.dragging) {
         this.dragging = false;
         FBPConfigHandler.write();
      }

      this.mouseDown = tmpMouseDown;
      this.sliderPosX = (double)this.x + 15.0D + MathHelper.clamp(this.value, 0.0D, 1.0D) * (double)(this.width - 30);
      if (this.dragging) {
         double max = (double)(this.x + this.width - 15);
         double min = (double)(this.x + 15);
         this.sliderPosX = MathHelper.clamp((double)mouseX - this.mouseGap, min, max);
         double val = this.sliderPosX - min;
         this.value = (double)MathHelper.clamp(MathHelper.abs((float)(val / (double)(this.width - 30))), 0.0F, 1.0F);
      }

      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      this.drawTexturedModalRect((float)this.sliderPosX - 15.0F, (float)this.y, 0, 100 + j * 20, 15, this.height);
      this.drawTexturedModalRect((float)this.sliderPosX, (float)this.y, 185, 100 + j * 20, 15, this.height);
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (!this.enabled) {
         return false;
      } else {
         if (this.dragging = this.isMouseOverSlider(mouseX, mouseY)) {
            this.mouseGap = (double)mouseX - this.sliderPosX;
         } else if (this.isMouseOverBar(mouseX, mouseY)) {
            float posX = (float)MathHelper.clamp(mouseX - (this.x + 4), 0, this.width - 5);
            this.value = (double)MathHelper.clamp(posX / (float)(this.width - 10), 0.0F, 1.0F);
            this.dragging = true;
            this.mouseGap = 0.0D;
         }

         return false;
      }
   }

   boolean isMouseOverBar(int mouseX, int mouseY) {
      int X1 = this.x + 4;
      int X2 = this.x + this.width - 6;
      int Y1 = this.y + 4;
      int Y2 = this.y + 15;
      boolean inRectangle = mouseX > X1 && mouseX < X2 && mouseY > Y1 && mouseY <= Y2;
      boolean inCircle1 = FBPGuiHelper.isMouseInsideCircle(mouseX, mouseY, (double)X1, (double)(Y1 + 5), 5.0D);
      boolean inCircle2 = FBPGuiHelper.isMouseInsideCircle(mouseX, mouseY, (double)X2, (double)(Y1 + 5), 5.0D);
      return inRectangle || inCircle1 || inCircle2;
   }

   boolean isMouseOverSlider(int mouseX, int mouseY) {
      int X1 = (int)(this.sliderPosX - 15.0D + 5.0D);
      int X2 = (int)(this.sliderPosX + 15.0D - 5.0D);
      int Y1 = this.y + 4;
      int Y2 = this.y + 15;
      boolean inRectangle = mouseX > X1 && mouseX < X2 && mouseY > Y1 && mouseY <= Y2;
      boolean inCircle1 = FBPGuiHelper.isMouseInsideCircle(mouseX, mouseY, (double)X1, (double)(Y1 + 5), 5.0D);
      boolean inCircle2 = FBPGuiHelper.isMouseInsideCircle(mouseX, mouseY, (double)X2, (double)(Y1 + 5), 5.0D);
      return inRectangle || inCircle1 || inCircle2;
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      return this.isMouseOverBar(mouseX, mouseY) || this.isMouseOverSlider(mouseX, mouseY);
   }
}
