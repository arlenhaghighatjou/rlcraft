package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class EntitySpriggan extends TameableCreatureEntity implements IMob {
   public int farmingRate = 20;
   private int farmingTick = 0;
   BaseProjectileEntity projectile = null;

   public EntitySpriggan(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F));
   }

   public void loadCreatureFlags() {
      this.farmingRate = this.creatureInfo.getFlag("farmingRate", this.farmingRate);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAir() >= 0) {
         if (this.isInWater()) {
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 2));
         } else if (this.waterContact()) {
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
         }
      }

      int currentFarmingRate = this.farmingRate;
      if (this.isTamed() && currentFarmingRate > 0) {
         if (this.isPetType("familiar")) {
            currentFarmingRate = this.farmingRate * 10;
         }

         if (this.subspecies != null) {
            currentFarmingRate = Math.max(1, Math.round((float)this.farmingRate / 3.0F));
         }

         ++this.farmingTick;
         int farmingRange = 16;
         int farmingHeight = 4;

         for(int x = (int)this.posX - farmingRange; x <= (int)this.posX + farmingRange; ++x) {
            for(int y = (int)this.posY - farmingHeight; y <= (int)this.posY + farmingHeight; ++y) {
               for(int z = (int)this.posZ - farmingRange; z <= (int)this.posZ + farmingRange; ++z) {
                  BlockPos pos = new BlockPos(x, y, z);
                  Block farmingBlock = this.getEntityWorld().getBlockState(pos).getBlock();
                  if (farmingBlock instanceof IPlantable && farmingBlock instanceof IGrowable && farmingBlock != Blocks.TALLGRASS && farmingBlock != Blocks.DOUBLE_PLANT) {
                     if (!this.getEntityWorld().isRemote && this.farmingTick % currentFarmingRate == 0 && farmingBlock.getTickRandomly()) {
                        this.getEntityWorld().scheduleBlockUpdate(pos, farmingBlock, currentFarmingRate, 1);
                     }

                     if (this.getEntityWorld().isRemote && this.farmingTick % 40 == 0) {
                        double d0 = this.getRNG().nextGaussian() * 0.02D;
                        double d1 = this.getRNG().nextGaussian() * 0.02D;
                        double d2 = this.getRNG().nextGaussian() * 0.02D;
                        this.getEntityWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (double)((float)x + this.getRNG().nextFloat()), (double)y + (double)this.getRNG().nextFloat(), (double)((float)z + this.getRNG().nextFloat()), d0, d1, d2, new int[0]);
                     }
                  }
               }
            }
         }
      }

      if (this.getEntityWorld().isRemote && !CreatureManager.getInstance().config.disableBlockParticles) {
         for(int i = 0; i < 2; ++i) {
            World var10000 = this.getEntityWorld();
            EnumParticleTypes var10001 = EnumParticleTypes.BLOCK_CRACK;
            double var10002 = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
            double var10003 = this.posY + this.rand.nextDouble() * (double)this.height;
            double var10004 = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;
            int[] var10008 = new int[1];
            BlockTallGrass var10011 = Blocks.TALLGRASS;
            var10008[0] = BlockTallGrass.getStateId(Blocks.TALLGRASS.getDefaultState());
            var10000.spawnParticle(var10001, var10002, var10003, var10004, 0.0D, 0.0D, 0.0D, var10008);
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("lifedrain");
      if (projectileInfo != null) {
         if (this.projectile != null && this.projectile.isEntityAlive()) {
            this.projectile.projectileLife = 20;
         } else {
            this.projectile = null;
         }

         if (this.projectile == null) {
            this.projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
            this.playSound(this.projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(this.projectile);
         }

         super.attackRanged(target, range);
      }
   }

   public float getDamageModifier(DamageSource damageSrc) {
      if (damageSrc.isFireDamage()) {
         return 2.0F;
      } else {
         if (damageSrc.getTrueSource() != null) {
            ItemStack heldItem = ItemStack.EMPTY;
            if (damageSrc.getTrueSource() instanceof EntityLivingBase) {
               EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
               if (!entityLiving.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
                  heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
               }
            }

            if (ObjectLists.isAxe(heldItem)) {
               return 2.0F;
            }
         }

         return super.getDamageModifier(damageSrc);
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
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
