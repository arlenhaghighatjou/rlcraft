package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class DragonAITarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
   private EntityDragonBase dragon;

   public DragonAITarget(EntityDragonBase entityIn, Class<T> classTarget, boolean checkSight, Predicate<? super T> targetSelector) {
      super(entityIn, classTarget, 0, checkSight, false, targetSelector);
      this.dragon = entityIn;
   }

   public boolean shouldExecute() {
      if (super.shouldExecute() && this.targetEntity != null && !this.targetEntity.getClass().equals(this.dragon.getClass())) {
         float dragonSize = Math.max(this.dragon.width, this.dragon.width * (this.dragon.getRenderSize() / 3.0F));
         if (dragonSize >= this.targetEntity.width) {
            if (this.targetEntity instanceof EntityDragonBase) {
               EntityDragonBase dragon = (EntityDragonBase)this.targetEntity;
               if (dragon.getOwner() != null && this.dragon.getOwner() != null && this.dragon.isOwner(dragon.getOwner())) {
                  return false;
               }

               return !dragon.isModelDead();
            }

            if (this.targetEntity instanceof EntityPlayer && this.dragon.isTamed()) {
               return false;
            }

            if (!this.dragon.isOwner(this.targetEntity) && FoodUtils.getFoodPoints(this.targetEntity) > 0 && this.dragon.canMove() && (this.dragon.getHunger() < 90 || !this.dragon.isTamed() && this.targetEntity instanceof EntityPlayer)) {
               if (this.dragon.isTamed()) {
                  return DragonUtils.canTameDragonAttack(this.dragon, this.targetEntity);
               }

               return true;
            }
         }
      }

      return false;
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.dragon.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
   }
}
