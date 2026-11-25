package rustic.common.potions;

import net.minecraft.entity.EntityLivingBase;

public class PotionFeather extends PotionBase {
   protected PotionFeather() {
      super(false, 14474460, "feather");
      this.setIconIndex(1, 0);
   }

   public void performEffect(EntityLivingBase entity, int p_76394_2_) {
      if (!entity.onGround && entity.motionY < -0.4D) {
         entity.motionY += 0.1D;
         entity.velocityChanged = true;
         entity.fallDistance = 0.0F;
      }

   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }
}
