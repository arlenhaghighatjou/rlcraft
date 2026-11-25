package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityConcapedeHead extends AgeableCreatureEntity {
   public static int CONCAPEDE_SIZE_MAX = 10;
   public BaseCreatureEntity backSegment;
   public boolean isHungry = true;

   public EntityConcapedeHead(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.canGrow = true;
      this.babySpawnChance = 0.0D;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityConcapedeSegment.class));
   }

   public void loadCreatureFlags() {
      CONCAPEDE_SIZE_MAX = this.creatureInfo.getFlag("sizeMax", CONCAPEDE_SIZE_MAX);
   }

   public void onFirstSpawn() {
      if (!this.getEntityWorld().isRemote && this.backSegment == null) {
         this.setGrowingAge(-this.growthTime / 4);
         int segmentCount = this.getRNG().nextInt(CONCAPEDE_SIZE_MAX);
         AgeableCreatureEntity parentSegment = this;

         for(int segment = 0; segment < segmentCount; ++segment) {
            EntityConcapedeSegment segmentEntity = (EntityConcapedeSegment)CreatureManager.getInstance().getCreature("concapedesegment").createEntity(parentSegment.getEntityWorld());
            segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
            segmentEntity.setParentTarget(parentSegment);
            segmentEntity.applyVariant(this.getVariantIndex());
            segmentEntity.setSizeScale(this.sizeScale);
            segmentEntity.spawnEventType = this.spawnEventType;
            segmentEntity.firstSpawn = false;
            parentSegment.getEntityWorld().spawnEntity(segmentEntity);
            parentSegment = segmentEntity;
         }
      }

      super.onFirstSpawn();
   }

   public boolean canBePushed() {
      return false;
   }

   public boolean shouldFollowParent() {
      return false;
   }

   public boolean rollLookChance() {
      return false;
   }

   public boolean rollWanderChance() {
      return false;
   }

   public void setGrowingAge(int age) {
      if (!this.getEntityWorld().isRemote && !this.firstSpawn && age == 0 && !this.isHungry && CreatureManager.getInstance().getCreature("concapedesegment") != null) {
         age = -(this.growthTime / 4);
         this.isHungry = true;
         int size = 0;

         BaseCreatureEntity lastSegment;
         BaseCreatureEntity trailingSegment;
         for(lastSegment = this; size <= CONCAPEDE_SIZE_MAX; lastSegment = trailingSegment) {
            ++size;
            trailingSegment = null;
            if (lastSegment instanceof EntityConcapedeHead) {
               trailingSegment = ((EntityConcapedeHead)lastSegment).backSegment;
            } else if (lastSegment instanceof EntityConcapedeSegment) {
               trailingSegment = ((EntityConcapedeSegment)lastSegment).backSegment;
            }

            if (trailingSegment == null || trailingSegment == lastSegment) {
               break;
            }
         }

         if (size < CONCAPEDE_SIZE_MAX) {
            EntityConcapedeSegment segmentEntity = (EntityConcapedeSegment)CreatureManager.getInstance().getCreature("concapedesegment").createEntity(lastSegment.getEntityWorld());
            segmentEntity.setLocationAndAngles(lastSegment.posX, lastSegment.posY, lastSegment.posZ, 0.0F, 0.0F);
            segmentEntity.setParentTarget(lastSegment);
            segmentEntity.applyVariant(this.getVariantIndex());
            segmentEntity.setSizeScale(this.sizeScale);
            segmentEntity.spawnEventType = this.spawnEventType;
            segmentEntity.firstSpawn = false;
            lastSegment.getEntityWorld().spawnEntity(segmentEntity);
         }
      }

      super.setGrowingAge(age);
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
      return true;
   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return target instanceof EntityConcapedeSegment ? false : super.canAttackEntity(target);
   }

   public boolean isAggressive() {
      if (this.isInLove()) {
         return false;
      } else {
         this.getEntityWorld();
         if (this.isDaytime()) {
            return this.testLightLevel() < 2;
         } else {
            return super.isAggressive();
         }
      }
   }

   public boolean isProtective(Entity entity) {
      if (this.isInLove()) {
         return false;
      } else {
         if (entity instanceof EntityConcapedeSegment) {
            for(BaseCreatureEntity checkCreature = (BaseCreatureEntity)entity; checkCreature.hasParent(); checkCreature = (BaseCreatureEntity)checkCreature.getParentTarget()) {
               if (checkCreature.getParentTarget() == this) {
                  return true;
               }

               if (!(checkCreature.getParentTarget() instanceof BaseCreatureEntity)) {
                  break;
               }
            }
         }

         return false;
      }
   }

   public boolean canClimb() {
      return true;
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
      return null;
   }

   public boolean canBreed() {
      return this.getGrowingAge() >= 0;
   }

   public boolean breed() {
      if (super.breed()) {
         this.isHungry = false;
         if (this.getAge() == 0) {
            this.setGrowingAge(0);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canMate() {
      return false;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("IsHungry")) {
         this.isHungry = nbtTagCompound.getBoolean("IsHungry");
      }

      super.readEntityFromNBT(nbtTagCompound);
   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      nbtTagCompound.setBoolean("IsHungry", this.isHungry);
   }
}
