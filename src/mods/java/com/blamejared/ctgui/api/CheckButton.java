package com.blamejared.ctgui.api;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

public class CheckButton extends GuiCheckBox {
   protected GuiBase parent;
   protected List<GuiButton> incompatible = new ArrayList();
   protected int backgroundSize = 100;

   public CheckButton(GuiBase parent, int id, int xPos, int yPos, String displayString, boolean isChecked) {
      super(id, xPos, yPos, displayString, isChecked);
      this.parent = parent;
   }

   public int getBackgroundSize() {
      return this.backgroundSize;
   }

   public void setBackgroundSize(int backgroundSize) {
      this.backgroundSize = backgroundSize;
   }

   public List<GuiButton> getIncompatible() {
      return this.incompatible;
   }

   public void setIncompatible(List<GuiButton> incompatible) {
      this.incompatible = incompatible;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.getButtonWidth() && mouseY < this.y + this.height;
         GlStateManager.enableAlpha();
         GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.0D);
         GuiUtils.drawGradientRect(30, this.x - 2, this.y - 2, this.x + this.getBackgroundSize(), this.y + this.height + 2, -1626341360, -1626341360);
         GlStateManager.disableAlpha();
         GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, 11, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
         this.mouseDragged(mc, mouseX, mouseY);
         int color = 14737632;
         if (this.packedFGColour != 0) {
            color = this.packedFGColour;
         } else if (!this.enabled) {
            color = 10526880;
         }

         if (this.isChecked()) {
            this.drawGradientRect(this.x + 2, this.y + 2, this.x + 11 - 2, this.y + this.height - 2, Color.cyan.darker().getRGB(), Color.cyan.darker().getRGB());
         }

         this.drawString(mc.fontRenderer, this.displayString, this.x + 11 + 2, this.y + 2, color);
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
         this.setIsChecked(!this.isChecked());
         return true;
      } else {
         return false;
      }
   }

   public void setIsChecked(boolean isChecked) {
      super.setIsChecked(isChecked);
      if (this.isChecked()) {
         for(GuiButton but : this.incompatible) {
            but.enabled = false;
            if (but instanceof CheckButton) {
               ((CheckButton)but).setIsChecked(false);
            }
         }
      } else {
         for(GuiButton but : this.incompatible) {
            but.enabled = true;
         }
      }

   }
}
