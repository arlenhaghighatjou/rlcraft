package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.ForgeHooks;

public class EntityAINearestAttackableTargetStatus<T extends EntityLivingBase> extends EntityAITarget {
   protected final Class<T> targetClass;
   private final int targetChance;
   protected final net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter sorter;
   protected final Predicate<? super T> targetEntitySelector;
   protected T targetEntity;
   protected double sneakPPP;
   protected float inviPPP;

   public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, boolean checkSight) {
      this(creature, classTarget, checkSight, false);
   }

   public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
      this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate)null, 0.800000011920929D, 0.7F);
   }

   public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector, double sneakP, float inviP) {
      super(creature, checkSight, onlyNearby);
      this.targetClass = classTarget;
      this.targetChance = chance;
      this.sorter = new net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter(creature);
      this.setMutexBits(1);
      this.targetEntitySelector = new Predicate<T>() {
         public boolean apply(@Nullable T p_apply_1_) {
            if (p_apply_1_ == null) {
               return false;
            } else if (targetSelector != null && !targetSelector.apply(p_apply_1_)) {
               return false;
            } else {
               return !EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) ? false : EntityAINearestAttackableTargetStatus.this.isSuitableTarget(p_apply_1_, true);
            }
         }
      };
      this.sneakPPP = sneakP;
      this.inviPPP = inviP;
   }

   public boolean shouldExecute() {
      if ((this.targetChance <= 0 || this.taskOwner.getRNG().nextInt(this.targetChance) == 0) && ((EntityParasiteBase)this.taskOwner).getParasiteStatus() == 0) {
         if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
            List<T> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.sorter);
               this.targetEntity = list.get(0);
               return true;
            }
         } else {
            this.targetEntity = (T)this.getNearest(this.taskOwner.posX, this.taskOwner.posY + (double)this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), this.targetEntitySelector);
            return this.targetEntity != null;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting();
   }

   private EntityPlayer getNearest(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Predicate<EntityPlayer> predicate) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int j2 = 0; j2 < this.taskOwner.world.playerEntities.size(); ++j2) {
         EntityPlayer entityplayer1 = (EntityPlayer)this.taskOwner.world.playerEntities.get(j2);
         if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive() && !entityplayer1.isSpectator() && (predicate == null || predicate.apply(entityplayer1))) {
            double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
            double d2 = maxXZDistance;
            if (entityplayer1.isSneaking()) {
               d2 = maxXZDistance * this.sneakPPP;
            }

            if (entityplayer1.isInvisible()) {
               float f = entityplayer1.getArmorVisibility();
               if (f < 0.1F && this.inviPPP != 1.0F) {
                  f = 0.1F;
               }

               d2 *= (double)(this.inviPPP * f);
            }

            d2 = ForgeHooks.getPlayerVisibilityDistance(entityplayer1, d2, maxYDistance);
            if ((maxYDistance < 0.0D || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.targetEntity);
      if (SRPConfigSystems.useOneMind && ParasiteEventEntity.alertOthers((EntityParasiteBase)this.taskOwner, this.targetEntity, this.taskOwner.world, 4)) {
         this.taskOwner.addPotionEffect(new PotionEffect(SRPPotions.SENS_E, 300, 3, false, false));
      }

      super.startExecuting();
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity entity;

      public Sorter(Entity entityIn) {
         this.entity = entityIn;
      }

      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.entity.getDistanceSq(p_compare_1_);
         double d1 = this.entity.getDistanceSq(p_compare_2_);
         if (d0 < d1) {
            return -1;
         } else {
            return d0 > d1 ? 1 : 0;
         }
      }
   }
}
