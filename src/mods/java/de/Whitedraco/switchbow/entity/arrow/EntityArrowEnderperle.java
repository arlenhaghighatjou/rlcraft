package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowEnderperle extends EntityArrowBase implements IThrowableEntity {
   private boolean spawnExplosion = true;
   private double damagesetter = ArrowConfig.DamageArrowEnderperle.getDoub();

   public EntityArrowEnderperle(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowEnderperle(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowEnderperle(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowEnderperle);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         if (this.shootingEntity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.shootingEntity;
            BlockPos lastpos = entityplayermp.getPosition();
            EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, living.posX, living.posY, living.posZ, 2.5F);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
               if (entityplayermp.isRiding()) {
                  entityplayermp.dismountRidingEntity();
               }

               entityplayermp.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               entityplayermp.fallDistance = 0.0F;
               if (SwitchBowConfig.teleportdamage.getBool()) {
                  entityplayermp.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
               }
            }

            EnderTeleportEvent eventliving = new EnderTeleportEvent(living, (double)lastpos.getX(), (double)lastpos.getY(), (double)lastpos.getZ(), 2.5F);
            if (!MinecraftForge.EVENT_BUS.post(eventliving)) {
               if (living.isRiding()) {
                  living.dismountRidingEntity();
               }

               living.setPositionAndUpdate(eventliving.getTargetX(), eventliving.getTargetY(), eventliving.getTargetZ());
               living.fallDistance = 0.0F;
               if (SwitchBowConfig.teleportdamage.getBool()) {
                  living.attackEntityFrom(DamageSource.FALL, eventliving.getAttackDamage());
               }
            }
         }

         this.setDead();
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote) {
         if (this.shootingEntity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.shootingEntity;
            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping()) {
               EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5.0F);
               if (!MinecraftForge.EVENT_BUS.post(event)) {
                  if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
                     EntityEndermite entityendermite = new EntityEndermite(this.world);
                     entityendermite.setSpawnedByPlayer(true);
                     entityendermite.setLocationAndAngles(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
                     this.world.spawnEntity(entityendermite);
                  }

                  if (entityplayermp.isRiding()) {
                     entityplayermp.dismountRidingEntity();
                  }

                  entityplayermp.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                  entityplayermp.fallDistance = 0.0F;
                  if (SwitchBowConfig.teleportdamage.getBool()) {
                     entityplayermp.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
                  }
               }
            }
         }

         this.setDead();
      }

   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
