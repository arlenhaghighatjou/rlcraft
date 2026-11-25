package rlmixins.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import rlmixins.handlers.ModRegistry;

public class PotionCow extends PotionBase {
   public static final PotionCow INSTANCE = new PotionCow();

   public PotionCow() {
      super("cow", false, 15987961);
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(EntityLivingBase entityLivingBase, int amplifier) {
      if (!entityLivingBase.world.isRemote) {
         if (entityLivingBase.ticksExisted % 40 == 0 && entityLivingBase.world.rand.nextFloat() < 0.3F) {
            entityLivingBase.world.playSound((EntityPlayer)null, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, ModRegistry.COW, SoundCategory.PLAYERS, 0.9F, (entityLivingBase.world.rand.nextFloat() - entityLivingBase.world.rand.nextFloat()) * 0.1F + 1.0F);
         }

      }
   }
}
