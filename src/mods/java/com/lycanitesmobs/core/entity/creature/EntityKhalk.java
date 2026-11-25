package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityKhalk extends TameableCreatureEntity implements IMob, IGroupHeavy {
   public boolean lavaDeath = true;

   public EntityKhalk(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.isLavaCreature = true;
      this.hasAttackSound = true;
      this.canGrow = true;
      this.babySpawnChance = 0.01D;
      this.solidCollision = true;
      this.setupMob();
      this.setPathPriority(PathNodeType.LAVA, 0.0F);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void loadCreatureFlags() {
      this.lavaDeath = this.creatureInfo.getFlag("lavaDeath", this.lavaDeath);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.getEntityWorld().isRemote && this.hasAttackTarget() && this.rand.nextInt(10) == 0) {
         this.leap(6.0F, 0.1D, this.getAttackTarget());
      }

   }

   public float getAISpeedModifier() {
      return this.lavaContact() ? 2.0F : 1.0F;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      BlockPos pos = new BlockPos(x, y, z);
      if (this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.lavaContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void onDeath(DamageSource damageSource) {
      if (!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.lavaDeath && !this.isTamed()) {
         int lavaWidth = (int)Math.floor((double)this.width) - 1;
         int lavaHeight = (int)Math.floor((double)this.height) - 1;

         for(int x = (int)this.posX - lavaWidth; x <= (int)this.posX + lavaWidth; ++x) {
            for(int y = (int)this.posY; y <= (int)this.posY + lavaHeight; ++y) {
               for(int z = (int)this.posZ - lavaWidth; z <= (int)this.posZ + lavaWidth; ++z) {
                  Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                  if (block == Blocks.AIR) {
                     IBlockState blockState = Blocks.FLOWING_LAVA.getStateFromMeta(11);
                     if (x == (int)this.posX && y == (int)this.posY && z == (int)this.posZ) {
                        blockState = Blocks.FLOWING_LAVA.getStateFromMeta(12);
                     }

                     this.getEntityWorld().setBlockState(new BlockPos(x, y, z), blockState, 3);
                  }
               }
            }
         }
      }

      super.onDeath(damageSource);
   }

   public boolean canBurn() {
      return false;
   }

   public boolean waterDamage() {
      return true;
   }

   public boolean canBreatheUnderlava() {
      return true;
   }

   public boolean canBreatheAir() {
      return true;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public void applyDropEffects(EntityItemCustom entityitem) {
      entityitem.setCanBurn(false);
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
