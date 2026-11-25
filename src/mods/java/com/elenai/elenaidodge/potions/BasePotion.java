package com.elenai.elenaidodge.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class BasePotion extends Potion {
   public BasePotion(String name, boolean isBadEffectIn, int liquidColorIn, int iconIndexX, int iconIndexY) {
      super(isBadEffectIn, liquidColorIn);
      this.setPotionName("effect." + name);
      this.setIconIndex(iconIndexX, iconIndexY);
      this.setRegistryName(new ResourceLocation("elenaidodge:" + name));
   }

   public boolean hasStatusIcon() {
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("elenaidodge", "textures/gui/icons.png"));
      return true;
   }
}
