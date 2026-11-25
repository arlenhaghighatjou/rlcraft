package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMaka extends AgeableCreatureEntity {
   public EntityMaka(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.1D;
      this.attackCooldownMax = 10;
      this.fleeHealthPercent = 1.0F;
      this.isAggressiveByDefault = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindMasterGoal(this)).setTargetClass(EntityMakaAlpha.class).setSightCheck(false));
   }

   public void onFirstSpawn() {
      CreatureInfo alphaInfo = CreatureManager.getInstance().getCreature("makaalpha");
      if (alphaInfo != null) {
         float alphaChance = (float)alphaInfo.creatureSpawn.spawnWeight / (float)Math.max(this.creatureInfo.creatureSpawn.spawnWeight, 1);
         if (this.getRNG().nextFloat() <= alphaChance) {
            EntityMakaAlpha alpha = (EntityMakaAlpha)CreatureManager.getInstance().getCreature("makaalpha").createEntity(this.getEntityWorld());
            alpha.copyLocationAndAnglesFrom(this);
            this.getEntityWorld().spawnEntity(alpha);
            this.setDead();
         }
      }

      super.onFirstSpawn();
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
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
      return !(target instanceof EntityMaka) && !(target instanceof EntityMakaAlpha) ? super.canAttackEntity(target) : false;
   }

   public void setGrowingAge(int age) {
      if (age == 0 && this.getAge() < 0 && this.getRNG().nextFloat() >= 0.9F) {
         EntityMakaAlpha alpha = (EntityMakaAlpha)CreatureManager.getInstance().getCreature("makaalpha").createEntity(this.getEntityWorld());
         alpha.copyLocationAndAnglesFrom(this);
         this.getEntityWorld().spawnEntity(alpha);
         this.setDead();
      }

      super.setGrowingAge(age);
   }
}
