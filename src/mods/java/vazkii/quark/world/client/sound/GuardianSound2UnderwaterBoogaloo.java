package vazkii.quark.world.client.sound;

import net.minecraft.client.audio.GuardianSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayer;

public class GuardianSound2UnderwaterBoogaloo extends GuardianSound {
   private final EntityGuardian visibleGuardian;

   public GuardianSound2UnderwaterBoogaloo(EntityGuardian guardian) {
      super(guardian);
      this.visibleGuardian = guardian;
   }

   public void update() {
      EntityLivingBase target = this.visibleGuardian.getTargetedEntity();
      if (!(target instanceof EntityPlayer)) {
         this.donePlaying = true;
      } else {
         super.update();
      }
   }
}
