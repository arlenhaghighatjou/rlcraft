package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityGeist extends AgeableCreatureEntity implements IMob {
   public boolean shadowfireDeath = true;

   public EntityGeist(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = true;
      this.spreadFire = true;
      this.canGrow = false;
      this.babySpawnChance = 0.01D;
      this.setupMob();
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextTravelGoalIndex++, new MoveVillageGoal(this));
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
      if (this.getNavigator() instanceof PathNavigateGround) {
         PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
         pathNavigateGround.setBreakDoors(true);
         pathNavigateGround.setAvoidSun(true);
      }

   }

   public void loadCreatureFlags() {
      this.shadowfireDeath = this.creatureInfo.getFlag("shadowfireDeath", this.shadowfireDeath);
   }

   public void onKillEntity(EntityLivingBase entityLivingBase) {
      super.onKillEntity(entityLivingBase);
      if (this.getEntityWorld().getDifficulty().getDifficultyId() >= 2 && entityLivingBase instanceof EntityVillager) {
         if (this.getEntityWorld().getDifficulty().getDifficultyId() == 2 && this.rand.nextBoolean()) {
            return;
         }

         EntityVillager entityvillager = (EntityVillager)entityLivingBase;
         EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.getEntityWorld());
         entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
         this.getEntityWorld().removeEntity(entityvillager);
         entityzombievillager.onInitialSpawn(this.getEntityWorld().getDifficultyForLocation(new BlockPos(entityzombievillager)), new BaseCreatureEntity.GroupData(false));
         entityzombievillager.setProfession(entityvillager.getProfession());
         entityzombievillager.setChild(entityvillager.isChild());
         entityzombievillager.setNoAI(entityvillager.isAIDisabled());
         if (entityvillager.hasCustomName()) {
            entityzombievillager.setCustomNameTag(entityvillager.getCustomNameTag());
            entityzombievillager.setAlwaysRenderNameTag(entityvillager.getAlwaysRenderNameTag());
         }

         this.getEntityWorld().spawnEntity(entityzombievillager);
         this.getEntityWorld().playEvent((EntityPlayer)null, 1016, entityzombievillager.getPosition(), 0);
      }

   }

   public void onDeath(DamageSource damageSource) {
      try {
         int shadowfireWidth = (int)Math.floor((double)this.width) + 1;
         int shadowfireHeight = (int)Math.floor((double)this.height) + 1;
         boolean permanent = false;
         if (damageSource.getTrueSource() == this) {
            permanent = true;
            shadowfireWidth *= 5;
         }

         if (!this.getEntityWorld().isRemote && (permanent || this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.shadowfireDeath)) {
            for(int x = (int)this.posX - shadowfireWidth; x <= (int)this.posX + shadowfireWidth; ++x) {
               for(int y = (int)this.posY - shadowfireHeight; y <= (int)this.posY + shadowfireHeight; ++y) {
                  for(int z = (int)this.posZ - shadowfireWidth; z <= (int)this.posZ + shadowfireWidth; ++z) {
                     Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                     if (block != Blocks.AIR && block != ObjectManager.getBlock("shadowfire")) {
                        BlockPos placePos = new BlockPos(x, y + 1, z);
                        Block upperBlock = this.getEntityWorld().getBlockState(placePos).getBlock();
                        if (upperBlock == Blocks.AIR) {
                           this.getEntityWorld().setBlockState(placePos, ObjectManager.getBlock("shadowfire").getDefaultState().withProperty(BlockFireBase.PERMANENT, Boolean.valueOf(permanent)));
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var11) {
         ;
      }

      super.onDeath(damageSource);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean daylightBurns() {
      return !this.isChild() && !this.isMinion();
   }
}
