package com.fantasticsource.mctools.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BetterPotion extends Potion {
   private final ResourceLocation texture;
   private final boolean isInstant;

   protected BetterPotion(ResourceLocation name, ResourceLocation texture, boolean isBad, boolean isInstant, int liquidColor) {
      super(isBad, liquidColor);
      if (!isBad) {
         this.setBeneficial();
      }

      this.setRegistryName(name);
      this.setPotionName(name.getResourceDomain() + "." + name.getResourcePath());
      this.texture = texture;
      this.isInstant = isInstant;
   }

   public boolean isInstant() {
      return this.isInstant;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (mc.currentScreen != null) {
         mc.getTextureManager().bindTexture(this.texture);
         Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      mc.getTextureManager().bindTexture(this.texture);
      Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
   }
}
