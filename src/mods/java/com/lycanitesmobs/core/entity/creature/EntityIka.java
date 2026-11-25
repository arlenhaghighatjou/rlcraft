package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityIka extends AgeableCreatureEntity {
   WanderGoal wanderAI;

   public EntityIka(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.babySpawnChance = 0.01D;
      this.canGrow = true;
      this.fleeHealthPercent = 1.0F;
      this.isAggressiveByDefault = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public float getAISpeedModifier() {
      float waterSpeed = 1.0F;
      if (this.isInWater()) {
         waterSpeed = 2.0F;
      } else if (this.waterContact()) {
         waterSpeed = 1.5F;
      }

      return this.getHealth() > this.getMaxHealth() / 2.0F ? waterSpeed * 0.75F : waterSpeed;
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

   public boolean canBeLeashedTo(EntityPlayer player) {
      return true;
   }

   public boolean canBeTempted() {
      return this.getAir() <= -100 ? false : super.canBeTempted();
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

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBreatheAir() {
      return false;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return this.getHealth() > this.getMaxHealth() / 2.0F ? 0.25F : 1.0F;
   }
}
