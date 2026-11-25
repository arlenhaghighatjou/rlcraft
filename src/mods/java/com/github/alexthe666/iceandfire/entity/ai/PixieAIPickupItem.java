package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;

public class PixieAIPickupItem<T extends EntityItem> extends EntityAITarget {
   protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super EntityItem> targetEntitySelector;
   protected EntityItem targetEntity;

   public PixieAIPickupItem(EntityPixie creature, boolean checkSight) {
      this(creature, checkSight, false);
   }

   public PixieAIPickupItem(EntityPixie creature, boolean checkSight, boolean onlyNearby) {
      this(creature, 20, checkSight, onlyNearby, (Predicate)null);
   }

   public PixieAIPickupItem(final EntityPixie creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<? super T> targetSelector) {
      super(creature, checkSight, onlyNearby);
      this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
      this.targetEntitySelector = new Predicate<EntityItem>() {
         public boolean apply(@Nullable EntityItem item) {
            return item instanceof EntityItem && !item.getItem().isEmpty() && (item.getItem().getItem() == Items.CAKE && !creature.isTamed() || item.getItem().getItem() == Items.SUGAR && creature.isTamed() && creature.getHealth() < creature.getMaxHealth());
         }
      };
      this.setMutexBits(3);
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
      this.taskOwner.getMoveHelper().setMoveTo(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, 0.25D);
      if (this.taskOwner.getAttackTarget() == null) {
         this.taskOwner.getLookHelper().setLookPosition(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, 180.0F, 20.0F);
      }

      super.startExecuting();
   }

   public void updateTask() {
      super.updateTask();
      if (this.targetEntity == null || this.targetEntity != null && this.targetEntity.isDead) {
         this.resetTask();
      }

      if (this.targetEntity != null && !this.targetEntity.isDead && this.taskOwner.getDistanceSq(this.targetEntity) < 1.0D) {
         EntityPixie pixie = (EntityPixie)this.taskOwner;
         if (this.targetEntity.getItem() != null && this.targetEntity.getItem().getItem() != null && this.targetEntity.getItem().getItem() == Items.SUGAR) {
            pixie.heal(5.0F);
         }

         if (this.targetEntity.getItem() != null && this.targetEntity.getItem().getItem() != null && this.targetEntity.getItem().getItem() == Items.CAKE && !pixie.isTamed() && this.targetEntity.getThrower() != null && !this.targetEntity.getThrower().isEmpty() && this.taskOwner.world.getPlayerEntityByName(this.targetEntity.getThrower()) != null) {
            EntityPlayer owner = this.taskOwner.world.getPlayerEntityByName(this.targetEntity.getThrower());
            pixie.setTamed(true);
            pixie.setOwnerId(owner.getUniqueID());
            pixie.setSitting(true);
         }

         this.targetEntity.getItem().shrink(1);
         pixie.playSound(ModSounds.PIXIE_TAUNT, 1.0F, 1.0F);
         this.resetTask();
      }

   }

   public boolean shouldContinueExecuting() {
      return this.taskOwner.getMoveHelper().action != Action.WAIT;
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity theEntity;

      public Sorter(Entity theEntityIn) {
         this.theEntity = theEntityIn;
      }

      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.theEntity.getDistanceSq(p_compare_1_);
         double d1 = this.theEntity.getDistanceSq(p_compare_2_);
         return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
      }
   }
}
