package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.math.AxisAlignedBB;

public class FlyingAITarget extends EntityAINearestAttackableTarget {
   public FlyingAITarget(EntityCreature creature, Class classTarget, boolean checkSight) {
      super(creature, classTarget, checkSight);
   }

   public FlyingAITarget(EntityCreature creature, Class classTarget, boolean checkSight, boolean onlyNearby) {
      super(creature, classTarget, checkSight, onlyNearby);
   }

   public FlyingAITarget(EntityCreature creature, Class classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate targetSelector) {
      super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.taskOwner.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
   }

   public boolean shouldExecute() {
      return !(this.taskOwner instanceof EntitySeaSerpent) || !((EntitySeaSerpent)this.taskOwner).isJumpingOutOfWater() && this.taskOwner.isInWater() ? super.shouldExecute() : false;
   }
}
