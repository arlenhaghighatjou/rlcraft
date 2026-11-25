package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.block.BlockWebBase;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityProjectileWebball extends EntitySRPProjectile {
   private byte type;

   public EntityProjectileWebball(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
      this.type = 1;
   }

   public EntityProjectileWebball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
      this.type = 1;
   }

   public EntityProjectileWebball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, byte t) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
      this.type = t;
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.EXPLOSION_NORMAL;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (SRPConfigMobs.dorpaWeb) {
            this.setWebsAround();
         } else if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)result.entityHit;
            if (target instanceof EntityParasiteBase) {
               this.setDead();
               return;
            }

            int i = 3;
            if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
               i = 6;
            } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
               i = 9;
            }

            DamageSource damagesource;
            if (this.shootingEntity == null) {
               damagesource = DamageSource.causeThrownDamage(this, this);
            } else {
               damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
            }

            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, i * 20, 1));
            target.attackEntityFrom(damagesource, SRPAttributes.DORPA_RANGED_DAMAGE);
            this.attackEntityAsMobMinimum(target, (EntityParasiteBase)this.shootingEntity);
         }

         this.setDead();
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (this.ticksExisted > 60) {
         this.setWebsAround();
         this.setDead();
      }

   }

   private void setWebsAround() {
      int totalWebs = this.rand.nextInt(3) + 1;
      int[] positionss = new int[]{-1, 0, 1};

      for(int i = 1; i <= totalWebs; ++i) {
         int pox = positionss[this.rand.nextInt(3)];
         int poy = positionss[this.rand.nextInt(3)];
         int poz = positionss[this.rand.nextInt(3)];
         if (this.world.getBlockState(new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz)).getBlock() == Blocks.AIR) {
            switch(this.type) {
            case 1:
               this.world.setBlockState(new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), SRPBlocks.SRPWeb.getDefaultState());
               break;
            case 2:
               this.world.setBlockState(new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), SRPBlocks.SRPWeb.getDefaultState().withProperty(BlockWebBase.VARIANT, BlockWebBase.EnumType.TWO));
               break;
            case 3:
               this.world.setBlockState(new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), SRPBlocks.SRPWeb.getDefaultState().withProperty(BlockWebBase.VARIANT, BlockWebBase.EnumType.THREE));
            }
         }
      }

   }
}
