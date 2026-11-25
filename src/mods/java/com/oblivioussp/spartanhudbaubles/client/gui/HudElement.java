package com.oblivioussp.spartanhudbaubles.client.gui;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class HudElement {
   protected boolean isEnabled = true;
   protected int width;
   protected int height;

   public HudElement(int elementWidth, int elementHeight) {
      this.width = elementWidth;
      this.height = elementHeight;
   }

   public boolean isEnabled() {
      return this.isEnabled;
   }

   public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getAlignedX(AlignmentHelper.Alignment align, int offset, ScaledResolution res) {
      switch(align.getHorizontal()) {
      case LEFT:
         return offset;
      case CENTER:
         return res.getScaledWidth() / 2 - this.width / 2 + offset;
      case RIGHT:
         return res.getScaledWidth() - this.width + offset;
      default:
         return 0;
      }
   }

   public int getAlignedY(AlignmentHelper.Alignment align, int offset, ScaledResolution res) {
      switch(align.getVertical()) {
      case TOP:
         return offset;
      case CENTER:
         switch(align.getHorizontal()) {
         case CENTER:
            return res.getScaledHeight() / 2 + 26 + offset;
         default:
            return res.getScaledHeight() / 2 - this.height / 2 + offset;
         }
      case BOTTOM:
         switch(align.getHorizontal()) {
         case CENTER:
            return res.getScaledHeight() - this.height - 65 + offset;
         default:
            return res.getScaledHeight() - this.height + offset;
         }
      default:
         return 0;
      }
   }

   public abstract void render();
}
