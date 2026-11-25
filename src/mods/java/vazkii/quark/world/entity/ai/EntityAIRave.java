package vazkii.quark.world.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import vazkii.quark.world.entity.EntityCrab;

public class EntityAIRave extends EntityAIBase {
   private final EntityCrab crab;

   public EntityAIRave(EntityCrab crab) {
      this.crab = crab;
      this.setMutexBits(5);
   }

   public boolean shouldExecute() {
      return this.crab.isRaving();
   }

   public void startExecuting() {
      this.crab.getNavigator().clearPath();
   }
}
