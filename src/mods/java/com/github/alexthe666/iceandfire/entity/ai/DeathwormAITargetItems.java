package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;

public class DeathwormAITargetItems<T extends EntityItem> extends EntityAITarget {
   protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super EntityItem> targetEntitySelector;
   private final int targetChance;
   protected EntityItem targetEntity;

   public DeathwormAITargetItems(EntityDeathWorm creature, boolean checkSight) {
      this(creature, checkSight, false);
   }

   public DeathwormAITargetItems(EntityDeathWorm creature, boolean checkSight, boolean onlyNearby) {
      this(creature, 0, checkSight, onlyNearby, (Predicate)null);
   }

   public DeathwormAITargetItems(EntityDeathWorm creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<? super T> targetSelector) {
      super(creature, checkSight, onlyNearby);
      this.targetChance = chance;
      this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
      this.targetEntitySelector = new Predicate<EntityItem>() {
         public boolean apply(@Nullable EntityItem item) {
            return item instanceof EntityItem && !item.getItem().isEmpty() && item.getItem().getItem() == Item.getItemFromBlock(Blocks.TNT);
         }
      };
   }

   public boolean shouldExecute() {
      List<EntityItem> list = this.taskOwner.world.getEntitiesWithinAABB(EntityItem.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
      if (list.isEmpty()) {
         return false;
      } else {
         Collections.sort(list, this.theNearestAttackableTargetSorter);
         this.targetEntity = (EntityItem)list.get(0);
         return true;
      }
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0D, targetDistance);
   }

   public void startExecuting() {
      this.taskOwner.getNavigator().tryMoveToXYZ(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, 1.0D);
      super.startExecuting();
   }

   public void updateTask() {
      super.updateTask();
      if (this.targetEntity == null || this.targetEntity != null && this.targetEntity.isDead) {
         this.resetTask();
      }

      if (this.targetEntity != null && !this.targetEntity.isDead && this.taskOwner.getDistanceSq(this.targetEntity) < 1.0D) {
         EntityDeathWorm deathWorm = (EntityDeathWorm)this.taskOwner;
         this.targetEntity.getItem().shrink(1);
         this.taskOwner.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
         deathWorm.setAnimation(EntityDeathWorm.ANIMATION_BITE);
         deathWorm.setExplosive(true);
         this.resetTask();
      }

   }

   public boolean shouldContinueExecuting() {
      return !this.taskOwner.getNavigator().noPath();
   }
}
