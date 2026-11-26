package com.oblivioussp.spartanhudbaubles.client.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiPreviewHUD extends GuiScreen {
   private static final int BUTTON_ID_DONE = 100;
   private static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("textures/gui/widgets.png");
   private final GuiScreen parentScreen;
   private HudElementBaubles hud;

   public GuiPreviewHUD(GuiScreen parent) {
      this.parentScreen = parent;
      this.hud = new HudElementBaubles(true);
   }

   public void initGui() {
      ScaledResolution res = new ScaledResolution(this.mc);
      String doneTitle = I18n.format("gui.done", new Object[0]);
      int doneWidth = this.fontRenderer.getStringWidth(doneTitle) + 20;
      this.buttonList.add(new GuiButtonExt(100, res.getScaledWidth() / 2 - doneWidth / 2, res.getScaledHeight() / 2 - 50, doneWidth, 20, doneTitle));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 100) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.mc.world == null) {
         this.drawDefaultBackground();
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      ScaledResolution res = new ScaledResolution(this.mc);
      EnumHandSide offhandSide = this.mc.gameSettings.mainHand.opposite();
      this.mc.getTextureManager().bindTexture(WIDGETS_TEXTURE);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      if (this.mc.world == null) {
         int halfWidth = res.getScaledWidth() / 2;
         int halfHeight = res.getScaledHeight() / 2;
         this.drawTexturedModalRect(halfWidth - 91, res.getScaledHeight() - 22, 0, 0, 182, 22);
         if (offhandSide == EnumHandSide.LEFT) {
            this.drawTexturedModalRect(halfWidth - 91 - 29, res.getScaledHeight() - 23, 24, 22, 29, 24);
         } else {
            this.drawTexturedModalRect(halfWidth + 91, res.getScaledHeight() - 23, 53, 22, 29, 24);
         }

         this.mc.getTextureManager().bindTexture(ICONS);
         GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_ONE_MINUS_DST_COLOR, RealOpenGLEnums.GL_ONE_MINUS_SRC_COLOR, RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
         GlStateManager.enableAlpha();
         this.drawTexturedModalRect(halfWidth - 7, halfHeight - 7, 0, 0, 16, 16);
         GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA, RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
         this.drawTexturedModalRect(halfWidth - 91, res.getScaledHeight() - 32 + 3, 0, 64, 182, 5);
         int armorHeight = res.getScaledHeight() - 49;

         for(int i = 0; i < 10; ++i) {
            this.drawTexturedModalRect(halfWidth - 91 + i * 8, armorHeight, 43, 9, 9, 9);
         }

         int healthHeight = res.getScaledHeight() - 39;

         for(int i = 0; i < 10; ++i) {
            this.drawTexturedModalRect(halfWidth - 91 + i * 8, healthHeight, 16, 0, 9, 9);
            this.drawTexturedModalRect(halfWidth - 91 + i * 8, healthHeight, 52, 0, 9, 9);
         }

         int hungerHeight = res.getScaledHeight() - 39;

         for(int i = 0; i < 10; ++i) {
            this.drawTexturedModalRect(halfWidth + 82 - i * 8, hungerHeight, 16, 27, 9, 9);
            this.drawTexturedModalRect(halfWidth + 82 - i * 8, hungerHeight, 52, 27, 9, 9);
         }

         String hoverText = I18n.format("config.spartanhudbaubles:hover_text_example", new Object[0]);
         int hoverTextSize = this.fontRenderer.getStringWidth(hoverText);
         int hoverX = (res.getScaledWidth() - hoverTextSize) / 2;
         int hoverY = res.getScaledHeight() - 59;
         this.fontRenderer.drawStringWithShadow(hoverText, (float)hoverX, (float)hoverY, -1);
      }

      GlStateManager.disableBlend();
      this.hud.render();
   }
}
