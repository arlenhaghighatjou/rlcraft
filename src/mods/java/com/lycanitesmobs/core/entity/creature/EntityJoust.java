package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityJoust extends AgeableCreatureEntity {
   public EntityJoust(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.1D;
      this.canGrow = true;
      this.attackCooldownMax = 10;
      this.trueSight = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindMasterGoal(this)).setTargetClass(EntityJoustAlpha.class).setSightCheck(false));
      this.targetTasks.addTask(this.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(this));
   }

   public void onFirstSpawn() {
      CreatureInfo alphaInfo = CreatureManager.getInstance().getCreature("joustalpha");
      if (alphaInfo != null) {
         float alphaChance = (float)alphaInfo.creatureSpawn.spawnWeight / (float)Math.max(this.creatureInfo.creatureSpawn.spawnWeight, 1);
         if (this.getRNG().nextFloat() <= alphaChance) {
            EntityJoustAlpha alpha = (EntityJoustAlpha)CreatureManager.getInstance().getCreature("joustalpha").createEntity(this.getEntityWorld());
            alpha.copyLocationAndAnglesFrom(this);
            this.getEntityWorld().spawnEntity(alpha);
            this.setDead();
         }
      }

      super.onFirstSpawn();
   }

   public float getBlockPathWeight(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
      if (blockState.getBlock() != Blocks.AIR) {
         if (blockState.getMaterial() == Material.SAND) {
            return 10.0F;
         }

         if (blockState.getMaterial() == Material.CLAY) {
            return 7.0F;
         }

         if (blockState.getMaterial() == Material.ROCK) {
            return 5.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return !this.hasAttackTarget() && !this.hasMaster() ? true : super.canBeLeashedTo(player);
   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return target instanceof EntityJoustAlpha ? false : super.canAttackEntity(target);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("cactus") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public void setGrowingAge(int age) {
      if (age == 0 && this.getAge() < 0 && this.getRNG().nextFloat() >= 0.9F) {
         EntityJoustAlpha alpha = (EntityJoustAlpha)CreatureManager.getInstance().getCreature("joustalpha").createEntity(this.getEntityWorld());
         alpha.copyLocationAndAnglesFrom(this);
         this.getEntityWorld().spawnEntity(alpha);
         this.setDead();
      }

      super.setGrowingAge(age);
   }
}
