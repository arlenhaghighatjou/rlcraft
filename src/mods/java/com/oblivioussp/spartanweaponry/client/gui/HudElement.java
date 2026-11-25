package com.oblivioussp.spartanweaponry.client.gui;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class HudElement {
   protected int width;
   protected int height;

   public int getAlignedX(AlignmentHelper.Alignment align, int offset, ScaledResolution res) {
      switch(align) {
      case TOP_LEFT:
      case CENTER_LEFT:
      case BOTTOM_LEFT:
         return offset;
      case TOP_CENTER:
      case CENTER:
      case BOTTOM_CENTER:
         return res.getScaledWidth() / 2 - this.width / 2 + offset;
      case TOP_RIGHT:
      case CENTER_RIGHT:
      case BOTTOM_RIGHT:
         return res.getScaledWidth() - this.width + offset;
      default:
         return 0;
      }
   }

   public int getAlignedY(AlignmentHelper.Alignment align, int offset, ScaledResolution res) {
      switch(align) {
      case TOP_LEFT:
      case TOP_CENTER:
      case TOP_RIGHT:
         return offset;
      case CENTER_LEFT:
      case CENTER_RIGHT:
         return res.getScaledHeight() / 2 - this.height / 2 + offset;
      case BOTTOM_LEFT:
      case BOTTOM_RIGHT:
         return res.getScaledHeight() - this.height + offset;
      case CENTER:
         return res.getScaledHeight() / 2 - this.height / 2 + 26 + offset;
      case BOTTOM_CENTER:
         return res.getScaledHeight() - this.height - 65 + offset;
      default:
         return 0;
      }
   }

   public HudElement(int elementWidth, int elementHeight) {
      this.width = elementWidth;
      this.height = elementHeight;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public abstract void render();
}
