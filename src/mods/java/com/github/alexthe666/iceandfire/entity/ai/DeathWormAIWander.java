package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.entity.ai.EntityAIWander;

public class DeathWormAIWander extends EntityAIWander {
   private EntityDeathWorm worm;

   public DeathWormAIWander(EntityDeathWorm creatureIn, double speedIn) {
      super(creatureIn, speedIn);
      this.worm = creatureIn;
   }

   public boolean shouldExecute() {
      return !this.worm.isInSand() && super.shouldExecute();
   }

   public boolean shouldContinueExecuting() {
      return !this.worm.isInSand() && super.shouldContinueExecuting();
   }
}
