package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import java.awt.Dimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPGuiButtonEnable extends GuiButton {
   FontRenderer _fr;
   Dimension _screen;

   public FBPGuiButtonEnable(int buttonID, int xPos, int yPos, Dimension screen, FontRenderer fr) {
      super(buttonID, xPos, yPos, 25, 25, "");
      this._screen = screen;
      this._fr = fr;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(FBP.FBP_FBP);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int centerX = this.x + 12;
         int centerY = this.y + 12;
         double distance = Math.sqrt((double)((mouseX - centerX) * (mouseX - centerX) + (mouseY - centerY) * (mouseY - centerY)));
         double radius = Math.sqrt(2.0D * Math.pow(16.0D, 2.0D));
         boolean flag = distance <= radius / 2.0D;
         int i = FBP.isEnabled() ? 0 : 50;
         if (this.hovered = flag) {
            i += 25;
         }

         Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, (float)i, 25, 25, 25.0F, 100.0F);
         String text = (FBP.isEnabled() ? "Disable" : "Enable") + " FBP";
         if (flag) {
            this.drawString(this._fr, text, mouseX - this._fr.getStringWidth(text) - 25, mouseY - 3, this._fr.getColorCode('a'));
         }
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible && this.hovered) {
         this.playPressSound(mc.getSoundHandler());
         return true;
      } else {
         return false;
      }
   }
}
