package rustic.common.potions;

import net.minecraft.entity.EntityLivingBase;
import rustic.common.network.MessageShameFX;
import rustic.common.network.PacketHandler;

public class PotionShame extends PotionBase {
   protected PotionShame() {
      super(true, 16409650, "shame");
      this.setIconIndex(3, 0);
   }

   public void performEffect(EntityLivingBase entity, int amp) {
      if (entity != null && !entity.world.isRemote) {
         PacketHandler.INSTANCE.sendToAll(new MessageShameFX(entity.world.rand.nextInt(6) + 6, entity.posX, entity.posY, entity.posZ, entity.motionX, entity.motionY, entity.motionZ, (double)entity.width, (double)entity.height));
      }

   }

   public boolean isReady(int duration, int amplifier) {
      return duration % 5 == 0;
   }
}
