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
public class FBPGuiButtonBugReport extends GuiButton {
   FontRenderer _fr;
   String _textOnHover = "Found a bug? Click to report it!";
   Dimension _screen;
   long lastTime;
   long time;
   int fadeAmmount = 0;

   public FBPGuiButtonBugReport(int buttonID, int xPos, int yPos, Dimension screen, FontRenderer fr) {
      super(buttonID, xPos, yPos, 25, 25, "");
      this._screen = screen;
      this._fr = fr;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(FBP.FBP_BUG);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int centerX = this.x + 12;
         int centerY = this.y + 12;
         double distance = Math.sqrt((double)((mouseX - centerX) * (mouseX - centerX) + (mouseY - centerY) * (mouseY - centerY)));
         double radius = Math.sqrt(2.0D * Math.pow(16.0D, 2.0D));
         boolean flag = distance <= radius / 2.0D;
         int i = 0;
         if (this.hovered = flag) {
            i += 25;
         }

         int step = 1;
         this.time = System.currentTimeMillis();
         if (this.lastTime > 0L) {
            step = (int)(this.time - this.lastTime);
         }

         this.lastTime = this.time;
         if (this.fadeAmmount < step) {
            this.fadeAmmount = step;
         }

         if (this.fadeAmmount <= 160 && this.fadeAmmount >= step) {
            this.fadeAmmount += flag ? step : -step;
         }

         if (this.fadeAmmount > 150) {
            if (flag) {
               this.fadeAmmount = 150;
            } else {
               this.fadeAmmount = 0;
            }
         }

         if (this.fadeAmmount > 0) {
            FBPGuiHelper.drawRect(0.0D, 0.0D, (double)this._screen.width, (double)this._screen.height, 0, 0, 0, this.fadeAmmount);
         } else {
            this.fadeAmmount = 0;
         }

         Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, (float)i, 25, 25, 25.0F, 50.0F);
         if (flag) {
            this.drawString(this._fr, this._textOnHover, mouseX - this._fr.getStringWidth(this._textOnHover) - 25, mouseY - 3, this._fr.getColorCode('a'));
         }
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.enabled && this.visible && this.hovered) {
         this.playPressSound(mc.getSoundHandler());
         return true;
      } else {
         return false;
      }
   }
}
