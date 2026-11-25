package de.Whitedraco.switchbow.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionFreeze extends Potion {
   public static final ResourceLocation icon = new ResourceLocation("switchbow".toLowerCase(), "textures/gui/container/potion.png");

   public PotionFreeze(boolean isBadEffectIn, int liquidColorIn) {
      super(isBadEffectIn, liquidColorIn);
   }

   public Potion setIconIndex(int x, int y) {
      super.setIconIndex(x, y);
      return this;
   }

   public int getStatusIconIndex() {
      ITextureObject texture = Minecraft.getMinecraft().renderEngine.getTexture(icon);
      Minecraft.getMinecraft().renderEngine.bindTexture(icon);
      return super.getStatusIconIndex();
   }
}
