package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectilePullball extends EntitySRPProjectile {
   private EntityLivingBase ranrac;
   private byte type;

   public EntityProjectilePullball(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectilePullball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, byte t) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
      this.ranrac = shooter;
      this.type = t;
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.EXPLOSION_NORMAL;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entityHit != null) {
            if (result.entityHit instanceof EntityLivingBase) {
               ;
            }
         } else {
            this.setWebsAround();
         }

         this.setDead();
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (this.ticksExisted == 5) {
         switch(this.type) {
         case 1:
            this.accelerationX *= 2.0D;
            this.accelerationZ *= 2.0D;
            break;
         case 2:
            this.accelerationX *= 4.0D;
            this.accelerationZ *= 4.0D;
         }
      }

      if (!this.world.isRemote) {
         switch(this.type) {
         case 1:
            this.pRanrac();
            break;
         case 2:
            this.aRanrac();
         }
      }

   }

   private void pRanrac() {
      if (this.shootingEntity instanceof EntityRanrac) {
         if (this.shootingEntity != null && !((EntityRanrac)this.shootingEntity).hasTargetedEntity()) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(2.0D);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (!(mob instanceof EntityParasiteBase) && this.shootingEntity.canEntityBeSeen(mob)) {
                  EntityRanrac ra = (EntityRanrac)this.shootingEntity;
                  if (mob == ra.getAttackTarget()) {
                     if (!mob.isEntityAlive()) {
                        this.setDead();
                        return;
                     }

                     ra.setParasiteStatus(3);
                     mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 2, false, false));
                     ra.setTargetedEntity(mob.getEntityId());
                     ra.getNavigator().tryMoveToEntityLiving(mob, 0.0D);
                     ra.resetPullSkill();
                  }
               }
            }

         } else {
            this.setDead();
         }
      }
   }

   private void aRanrac() {
      if (this.shootingEntity instanceof EntityRanracAdapted) {
         if (this.shootingEntity != null && !((EntityRanracAdapted)this.shootingEntity).hasTargetedEntity()) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(2.5D);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (!(mob instanceof EntityParasiteBase) && this.shootingEntity.canEntityBeSeen(mob)) {
                  EntityRanracAdapted ra = (EntityRanracAdapted)this.shootingEntity;
                  if (mob == ra.getAttackTarget()) {
                     if (!mob.isEntityAlive()) {
                        this.setDead();
                        return;
                     }

                     ra.setParasiteStatus(3);
                     mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 2, false, false));
                     ra.setTargetedEntity(mob.getEntityId());
                     ra.getNavigator().tryMoveToEntityLiving(mob, 0.0D);
                     ra.resetPullSkill();
                  }
               }
            }

         } else {
            this.setDead();
         }
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
            this.world.setBlockState(new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), SRPBlocks.SRPWeb.getDefaultState());
         }
      }

   }
}
