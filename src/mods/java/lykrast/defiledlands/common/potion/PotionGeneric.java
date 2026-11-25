package lykrast.defiledlands.common.potion;

import lykrast.defiledlands.common.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PotionGeneric extends Potion {
   protected static final ResourceLocation TEXTURE = new ResourceLocation("defiledlands", "textures/gui/potion_effects.png");
   protected int tickrate;

   public PotionGeneric(boolean isBadEffectIn, int liquidColorIn, int icon, int rate) {
      super(isBadEffectIn, liquidColorIn);
      this.setIconIndex(icon % 8, icon / 8);
      this.tickrate = rate;
   }

   public int getStatusIconIndex() {
      Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
      return super.getStatusIconIndex();
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      if (this == ModPotions.grounded) {
         if (!entityLivingBaseIn.onGround) {
            if (entityLivingBaseIn.motionY > 0.0D) {
               entityLivingBaseIn.motionY /= (double)(amplifier + 2);
            }

            entityLivingBaseIn.motionY -= 0.05D * (double)(amplifier + 1);
         }
      } else if (this == ModPotions.bleeding) {
         entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, (float)(amplifier + 1));
      }

   }

   public boolean isReady(int duration, int amplifier) {
      if (this.tickrate < 0) {
         return false;
      } else {
         return this.tickrate <= 0 || duration % this.tickrate == 0;
      }
   }
}
