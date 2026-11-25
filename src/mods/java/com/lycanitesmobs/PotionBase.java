package com.lycanitesmobs;

import com.lycanitesmobs.client.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBase extends Potion {
   public String name;

   public PotionBase(String name, boolean badEffect, int color) {
      super(badEffect, color);
      this.name = name;
      this.setRegistryName("lycanitesmobs", name);
      this.setPotionName("effect." + name);
      AssetManager.addTexture("effect." + name, LycanitesMobs.modInfo, "textures/effects/" + name + ".png");
   }

   public boolean isInstant() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (mc.currentScreen != null) {
         ResourceLocation texture = AssetManager.getTexture("effect." + this.name);
         if (texture != null) {
            mc.getTextureManager().bindTexture(texture);
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      ResourceLocation texture = AssetManager.getTexture("effect." + this.name);
      if (texture != null) {
         mc.getTextureManager().bindTexture(texture);
         Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }
   }
}
