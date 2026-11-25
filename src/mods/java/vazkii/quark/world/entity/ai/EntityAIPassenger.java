package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIPassenger extends EntityAIBase {
   private final EntityLiving entity;

   public EntityAIPassenger(EntityLiving entity) {
      this.entity = entity;
      this.setMutexBits(7);
   }

   public boolean shouldExecute() {
      return this.entity.isRiding();
   }
}
