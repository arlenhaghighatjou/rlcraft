package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAbtu extends TameableCreatureEntity implements IMob {
   int swarmLimit = 5;

   public EntityAbtu(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.9D;
      this.canGrow = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void loadCreatureFlags() {
      this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
   }

   public void onLivingUpdate() {
      if (this.hasAttackTarget() && this.updateTick % 20L == 0L) {
         this.allyUpdate();
      }

      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.isChild() && (this.isInWater() || this.onGround) && this.getRNG().nextInt(10) == 0) {
         this.leap(4.0F, 0.6D, this.getAttackTarget());
      }

      super.onLivingUpdate();
   }

   public void allyUpdate() {
      if (!this.getEntityWorld().isRemote && !this.isChild()) {
         if (this.swarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64.0D) < this.swarmLimit) {
            float random = this.rand.nextFloat();
            float spawnChance = 0.25F;
            if (random <= spawnChance) {
               this.spawnAlly(this.posX - 2.0D + (double)(random * 4.0F), this.posY, this.posZ - 2.0D + (double)(random * 4.0F));
            }
         }

      }
   }

   public void spawnAlly(double x, double y, double z) {
      AgeableCreatureEntity minion = (AgeableCreatureEntity)this.creatureInfo.createEntity(this.getEntityWorld());
      minion.setGrowingAge(minion.growthTime);
      minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
      minion.setMinion(true);
      minion.applyVariant(this.getVariantIndex());
      this.getEntityWorld().spawnEntity(minion);
      if (this.getAttackTarget() != null) {
         minion.setRevengeTarget(this.getAttackTarget());
      }

   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
      if (block == Blocks.WATER) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z))) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.waterContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean canWalk() {
      return false;
   }

   public void onDeath(DamageSource par1DamageSource) {
      this.allyUpdate();
      super.onDeath(par1DamageSource);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBreatheAir() {
      return false;
   }
}
