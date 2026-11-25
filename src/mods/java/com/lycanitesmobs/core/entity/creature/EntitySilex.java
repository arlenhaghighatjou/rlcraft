package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySilex extends AgeableCreatureEntity {
   public EntitySilex(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.babySpawnChance = 0.01D;
      this.canGrow = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
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

   public boolean canBurn() {
      return false;
   }
}
