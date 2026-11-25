package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDweller extends TameableCreatureEntity implements IMob {
   WanderGoal wanderAI;

   public EntityDweller(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.01D;
      this.canGrow = false;
      this.setupMob();
      this.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setRange(1.0D));
   }

   public float getAISpeedModifier() {
      if (this.isInWater()) {
         return 2.0F;
      } else {
         return this.waterContact() ? 1.5F : super.getAISpeedModifier();
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
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.waterContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      BlockPos groundPos;
      for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && !this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid(); groundPos = groundPos.down()) {
         ;
      }

      return groundPos.up();
   }

   public boolean isAggressive() {
      return this.getAir() <= -100 ? false : super.isAggressive();
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

   public boolean petControlsEnabled() {
      return true;
   }
}
