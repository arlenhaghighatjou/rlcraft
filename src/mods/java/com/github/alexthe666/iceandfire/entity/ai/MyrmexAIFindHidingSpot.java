package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class MyrmexAIFindHidingSpot extends EntityAIBase {
   private static final int RADIUS = 32;
   private final EntityMyrmexSentinel myrmex;
   private BlockPos targetBlock = BlockPos.ORIGIN;
   private int wanderRadius = 32;
   protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super Entity> targetEntitySelector;

   public MyrmexAIFindHidingSpot(EntityMyrmexSentinel myrmex) {
      this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
      this.targetEntitySelector = new Predicate<Entity>() {
         public boolean apply(@Nullable Entity myrmex) {
            return myrmex != null && myrmex instanceof EntityMyrmexSentinel;
         }
      };
      this.myrmex = myrmex;
   }

   public boolean shouldExecute() {
      this.targetBlock = this.getTargetPosition(this.wanderRadius);
      return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.canSeeSky();
   }

   public boolean shouldContinueExecuting() {
      return !this.myrmex.shouldEnterHive() && this.myrmex.getNavigator().noPath();
   }

   public void updateTask() {
      if (!this.areMyrmexNear(32.0D) && !this.myrmex.isOnResin()) {
         if (this.myrmex.getAttackTarget() == null) {
            this.myrmex.setHiding(true);
            this.resetTask();
         }
      } else {
         this.myrmex.getNavigator().tryMoveToXYZ((double)this.targetBlock.getX() + 0.5D, (double)this.targetBlock.getY(), (double)this.targetBlock.getZ() + 0.5D, 1.0D);
         if (this.myrmex.getDistanceSqToCenter(this.targetBlock) < 2.0D) {
            this.wanderRadius += 32;
            this.targetBlock = this.getTargetPosition(this.wanderRadius);
         }
      }

   }

   public void resetTask() {
      this.targetBlock = BlockPos.ORIGIN;
      this.wanderRadius = 32;
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.myrmex.getEntityBoundingBox().grow(targetDistance, 14.0D, targetDistance);
   }

   public BlockPos getTargetPosition(int radius) {
      int x = (int)this.myrmex.posX + this.myrmex.getRNG().nextInt(radius * 2) - radius;
      int z = (int)this.myrmex.posZ + this.myrmex.getRNG().nextInt(radius * 2) - radius;
      return this.myrmex.world.getHeight(new BlockPos(x, 0, z));
   }

   private boolean areMyrmexNear(double distance) {
      List<Entity> sentinels = this.myrmex.world.getEntitiesInAABBexcluding(this.myrmex, this.getTargetableArea(distance), this.targetEntitySelector);
      List<Entity> hiddenSentinels = new ArrayList();

      for(Entity sentinel : sentinels) {
         if (sentinel instanceof EntityMyrmexSentinel && ((EntityMyrmexSentinel)sentinel).isHiding()) {
            hiddenSentinels.add(sentinel);
         }
      }

      return !hiddenSentinels.isEmpty();
   }
}
