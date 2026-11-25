package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityConcapedeSegment extends AgeableCreatureEntity {
   UUID parentUUID = null;
   public BaseCreatureEntity backSegment;

   public EntityConcapedeSegment(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.hasStepSound = false;
      this.canGrow = true;
      this.babySpawnChance = 0.0D;
      this.isAggressiveByDefault = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextTravelGoalIndex++, (new FollowParentGoal(this)).setSpeed(4.0D).setStrayDistance(4.0D));
      super.initEntityAI();
   }

   public boolean environmentSpawnCheck(World world, BlockPos pos) {
      return this.getNearbyEntities(EntityConcapedeHead.class, (Predicate)null, CreatureManager.getInstance().spawnConfig.spawnLimitRange).size() <= 0 ? false : super.environmentSpawnCheck(world, pos);
   }

   public void getRandomVariant() {
      if (this.subspecies == null && !this.hasParent()) {
         this.subspecies = this.creatureInfo.getRandomSubspecies(this);
      }

      if (this.hasParent() && this.getParentTarget() instanceof BaseCreatureEntity) {
         this.applyVariant(((BaseCreatureEntity)this.getParentTarget()).getSubspeciesIndex());
      }

   }

   protected boolean canDespawn() {
      if (!super.canDespawn()) {
         return false;
      } else {
         return !this.hasParent();
      }
   }

   public void onUpdate() {
      if (!this.getEntityWorld().isRemote && !this.hasParent() && this.parentUUID != null && this.updateTick > 0L && this.updateTick % 40L == 0L) {
         double range = 64.0D;

         for(AgeableCreatureEntity possibleConnection : this.getEntityWorld().getEntitiesWithinAABB(AgeableCreatureEntity.class, this.getEntityBoundingBox().grow(range, range, range))) {
            if (possibleConnection != this && possibleConnection.getUniqueID().equals(this.parentUUID)) {
               this.setParentTarget(possibleConnection);
               break;
            }
         }

         this.parentUUID = null;
      }

      super.onUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.backSegment != null && !this.backSegment.isEntityAlive()) {
            this.backSegment = null;
         }

         if (this.hasParent() && !this.getParentTarget().isEntityAlive()) {
            this.setParentTarget((EntityLivingBase)null);
         }

         if (this.hasParent()) {
            this.getLookHelper().setLookPositionWithEntity(this.getParentTarget(), 360.0F, 360.0F);
            this.faceEntity(this.getParentTarget(), 360.0F, 360.0F);
            Vec3d parentPos = this.getFacingPositionDouble(this.getParentTarget().posX, this.getParentTarget().posY, this.getParentTarget().posZ, -0.65D, (double)this.getParentTarget().rotationYaw);
            double segmentPullThreshold = 0.15D;
            double segmentDistance = Math.sqrt(this.getDistanceSq(parentPos.x, parentPos.y, parentPos.z));
            if (segmentDistance > segmentPullThreshold) {
               double dragAmount = segmentPullThreshold / 2.0D;
               Vec3d dragPos = this.getFacingPositionDouble(parentPos.x, parentPos.y, parentPos.z, dragAmount, this.getPositionVector().dotProduct(parentPos));
               double distY = parentPos.y - this.posY;
               double dragY = this.posY + distY / 2.0D;
               this.setPosition(dragPos.x, dragY, dragPos.z);
            }
         }

         if (!this.getEntityWorld().isRemote && this.getGrowingAge() <= 0) {
            this.setGrowingAge(-this.growthTime);
         }
      }

   }

   public boolean rollLookChance() {
      return this.hasParent() ? false : super.rollLookChance();
   }

   public boolean rollWanderChance() {
      return this.hasParent() ? false : super.rollWanderChance();
   }

   public boolean isFlying() {
      return false;
   }

   public boolean canEntityBeSeen(Entity target) {
      return this.hasParent() && target == this.getParentTarget();
   }

   public boolean canBePushed() {
      return !this.hasParent();
   }

   public void setGrowingAge(int age) {
      if (this.hasParent()) {
         age = -this.growthTime;
      }

      super.setGrowingAge(age);
      if (age == 0 && !this.getEntityWorld().isRemote) {
         EntityConcapedeHead concapedeHead = (EntityConcapedeHead)CreatureManager.getInstance().getCreature("concapede").createEntity(this.getEntityWorld());
         concapedeHead.copyLocationAndAnglesFrom(this);
         concapedeHead.firstSpawn = false;
         concapedeHead.setGrowingAge(-this.growthTime / 4);
         concapedeHead.setSizeScale(this.sizeScale);
         concapedeHead.applyVariant(this.getVariantIndex());
         this.getEntityWorld().spawnEntity(concapedeHead);
         if (this.backSegment != null) {
            this.backSegment.setParentTarget(concapedeHead);
         }

         this.setDead();
      }

   }

   public boolean shouldFollowParent() {
      return true;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
      Block block = blockState.getBlock();
      if (block != Blocks.AIR) {
         if (blockState.getMaterial() == Material.GRASS) {
            return 10.0F;
         }

         if (blockState.getMaterial() == Material.GROUND) {
            return 7.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return !this.hasParent();
   }

   public double getFallingMod() {
      if (this.getEntityWorld().isRemote) {
         return 0.0D;
      } else {
         return this.hasParent() && this.getParentTarget().posY > this.posY ? 0.0D : super.getFallingMod();
      }
   }

   public void setParentTarget(EntityLivingBase setTarget) {
      if (setTarget != this) {
         if (setTarget instanceof EntityConcapedeSegment) {
            ((EntityConcapedeSegment)setTarget).backSegment = this;
         }

         if (setTarget instanceof EntityConcapedeHead) {
            ((EntityConcapedeHead)setTarget).backSegment = this;
         }
      }

      super.setParentTarget(setTarget);
   }

   public boolean renderVariantNameTag() {
      return !this.hasParent();
   }

   public boolean canClimb() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
      return null;
   }

   public boolean breed() {
      if (!this.canBreed()) {
         return false;
      } else {
         this.setGrowingAge(0);
         return true;
      }
   }

   public boolean canBreed() {
      return !this.hasParent();
   }

   public boolean shouldFindParent() {
      return false;
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasUniqueId("ParentUUID")) {
         this.parentUUID = nbtTagCompound.getUniqueId("ParentUUID");
      }

      super.readEntityFromNBT(nbtTagCompound);
   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      if (this.hasParent()) {
         nbtTagCompound.setUniqueId("ParentUUID", this.getParentTarget().getUniqueID());
      }

   }
}
