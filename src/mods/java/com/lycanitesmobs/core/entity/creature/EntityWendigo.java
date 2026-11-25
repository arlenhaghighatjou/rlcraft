package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityWendigo extends BaseCreatureEntity implements IMob {
   WanderGoal wanderAI;

   public EntityWendigo(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
         int trailHeight = 1;
         int trailWidth = 1;
         if (this.isRareVariant()) {
            trailWidth = 3;
         }

         for(int y = 0; y < trailHeight; ++y) {
            Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
            if (block != null && (block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW || block == Blocks.TALLGRASS || block == ObjectManager.getBlock("scorchfire") || block == ObjectManager.getBlock("doomfire"))) {
               if (trailWidth == 1) {
                  this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("frostfire").getDefaultState());
               } else {
                  for(int x = -(trailWidth / 2); x < trailWidth / 2 + 1; ++x) {
                     for(int z = -(trailWidth / 2); z < trailWidth / 2 + 1; ++z) {
                        this.getEntityWorld().setBlockState(this.getPosition().add(x, y, z), ObjectManager.getBlock("frostfire").getDefaultState());
                     }
                  }
               }
            }
         }
      }

      if (!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
         Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0)).getBlock();
         if (block == Blocks.WATER) {
            this.getEntityWorld().setBlockState(this.getPosition().add(0, -1, 0), Blocks.ICE.getDefaultState());
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         if (this.ticksExisted % 10 == 0) {
            for(int i = 0; i < 2; ++i) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public float getAISpeedModifier() {
      return this.isInWater() ? 2.0F : 1.0F;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      BlockPos pos = new BlockPos(x, y, z);
      if (ObjectManager.getBlock("ooze") != null && this.getEntityWorld().getBlockState(pos).getBlock() == ObjectManager.getBlock("ooze")) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.isInWater() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("tundra", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("ooze") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }
}
