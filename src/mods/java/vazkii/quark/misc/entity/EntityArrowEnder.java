package vazkii.quark.misc.entity;

import javax.annotation.Nonnull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import vazkii.quark.misc.feature.ExtraArrows;

public class EntityArrowEnder extends EntityArrow {
   public EntityArrowEnder(World worldIn) {
      super(worldIn);
   }

   public EntityArrowEnder(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   public EntityArrowEnder(World worldIn, IPosition pos) {
      super(worldIn, pos.getX(), pos.getY(), pos.getZ());
   }

   @Nonnull
   protected ItemStack getArrowStack() {
      return new ItemStack(ExtraArrows.arrow_ender);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.getEntityWorld().isRemote) {
         if (this.inGround) {
            if (this.timeInGround % 5 == 0) {
               this.spawnPotionParticles(1);
            }
         } else {
            this.spawnPotionParticles(2);
         }
      }

   }

   public void spawnPotionParticles(int particleCount) {
      if (particleCount > 0) {
         for(int i = 0; i < particleCount; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   protected void onHit(RayTraceResult rayTrace) {
      super.onHit(rayTrace);
      if (rayTrace.typeOfHit == Type.BLOCK) {
         BlockPos blockpos = rayTrace.getBlockPos();
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityEndGateway) {
            TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
            if (this.shootingEntity != null) {
               if (this.shootingEntity instanceof EntityPlayerMP) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((EntityPlayerMP)this.shootingEntity, this.world.getBlockState(blockpos));
               }

               tileentityendgateway.teleportEntity(this.shootingEntity);
               this.setDead();
               return;
            }

            tileentityendgateway.teleportEntity(this);
            return;
         }
      }

      if (this.shootingEntity != null) {
         if (this.shootingEntity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.shootingEntity;
            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.getEntityWorld() == this.getEntityWorld() && !entityplayermp.isPlayerSleeping()) {
               EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5.0F);
               if (!MinecraftForge.EVENT_BUS.post(event) && this.rand.nextFloat() < 0.025F && this.getEntityWorld().getGameRules().getBoolean("doMobSpawning")) {
                  EntityEndermite entityendermite = new EntityEndermite(this.getEntityWorld());
                  entityendermite.setSpawnedByPlayer(true);
                  entityendermite.setLocationAndAngles(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ, this.shootingEntity.rotationYaw, this.shootingEntity.rotationPitch);
                  this.getEntityWorld().spawnEntity(entityendermite);
               }

               if (this.shootingEntity.isRiding()) {
                  this.shootingEntity.dismountRidingEntity();
               }

               this.shootingEntity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               this.shootingEntity.fallDistance = 0.0F;
               this.shootingEntity.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
            }
         } else {
            this.shootingEntity.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            this.shootingEntity.fallDistance = 0.0F;
         }

         for(int i = 0; i < 32; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[0]);
         }

         this.setDead();
      }

   }
}
