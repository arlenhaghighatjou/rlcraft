package me.ichun.mods.ichunutil.common.core.config.types;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Colour {
   public int r = 0;
   public int g = 0;
   public int b = 0;

   public Colour(int clr) {
      this.r = clr >> 16 & 255;
      this.g = clr >> 8 & 255;
      this.b = clr & 255;
   }

   public Colour(int r, int g, int b) {
      this.r = r;
      this.g = g;
      this.b = b;
   }

   public int getColour() {
      return (this.r << 16) + (this.g << 8) + this.b;
   }

   public String serialize() {
      String rS = Integer.toHexString(this.r);
      String gS = Integer.toHexString(this.g);

      String bS;
      for(bS = Integer.toHexString(this.b); rS.length() < 2; rS = "0" + rS) {
         ;
      }

      while(gS.length() < 2) {
         gS = "0" + gS;
      }

      while(bS.length() < 2) {
         bS = "0" + bS;
      }

      return "#" + rS + gS + bS;
   }

   public void deserialize(String s) {
      int clr = Integer.decode(s).intValue();
      this.r = clr >> 16 & 255;
      this.g = clr >> 8 & 255;
      this.b = clr & 255;
   }

   @SideOnly(Side.CLIENT)
   public void setColourToCurrent() {
      this.setColourToCurrent(255);
   }

   @SideOnly(Side.CLIENT)
   public void setColourToCurrent(int alpha) {
      GlStateManager.color((float)this.r / 255.0F, (float)this.g / 255.0F, (float)this.b / 255.0F, (float)alpha / 255.0F);
   }
}
