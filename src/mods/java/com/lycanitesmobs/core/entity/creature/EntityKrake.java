package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityKrake extends AgeableCreatureEntity implements IMob {
   public EntityKrake(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.trueSight = true;
      this.babySpawnChance = 0.1D;
      this.canGrow = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setRange(1.0D));
   }

   public boolean canEntityBeSeen(Entity target) {
      if (target == this.getRevengeTarget() && this.getRevengeTimer() + 60 > this.ticksExisted) {
         return true;
      } else {
         return !(target instanceof EntityPlayer) && !(target instanceof EntityVillager) ? true : target.isSprinting();
      }
   }

   public float getAISpeedModifier() {
      if (this.isInWater()) {
         return 1.5F;
      } else {
         return this.waterContact() ? 1.25F : super.getAISpeedModifier();
      }
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      BlockPos pos = new BlockPos(x, y, z);
      IBlockState blockState = this.getEntityWorld().getBlockState(pos);
      if (blockState.getBlock() == Blocks.WATER) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(pos)) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else {
         return this.getAttackTarget() != null ? super.getBlockPathWeight(x, y, z) : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return !this.hasAttackTarget() ? true : super.canBeLeashedTo(player);
   }

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      BlockPos groundPos;
      for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && !this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid(); groundPos = groundPos.down()) {
         ;
      }

      return groundPos.up();
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
}
