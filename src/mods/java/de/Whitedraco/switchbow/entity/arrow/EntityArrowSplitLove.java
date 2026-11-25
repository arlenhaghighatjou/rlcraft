package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.PotionInit;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.proxy.packets.ParticleHeartPacket;
import de.Whitedraco.switchbow.proxy.packets.ParticleSplittingPacket;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowSplitLove extends EntityArrowBase implements IThrowableEntity {
   public EntityArrowSplitLove(World worldIn) {
      super(worldIn);
   }

   public EntityArrowSplitLove(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityArrowSplitLove(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         if (this.shootingEntity == null) {
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this);
         } else {
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
         }

         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            this.arrowHit(entitylivingbase);
            if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
               ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
            }
         }

         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         if (!(entity instanceof EntityEnderman)) {
            this.setDead();
         } else {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
               if (this.pickupStatus == PickupStatus.ALLOWED) {
                  this.entityDropItem(this.getArrowStack(), 0.1F);
               }

               this.setDead();
            }
         }
      } else {
         BlockPos blockpos = raytraceResultIn.getBlockPos();
         this.xTile = blockpos.getX();
         this.yTile = blockpos.getY();
         this.zTile = blockpos.getZ();
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         this.inTile = iblockstate.getBlock();
         this.inData = this.inTile.getMetaFromState(iblockstate);
         this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
         this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
         this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
         float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
         this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
         this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.arrowShake = 7;
         this.setIsCritical(false);
         if (iblockstate.getMaterial() != Material.AIR) {
            this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
         }
      }

   }

   protected void arrowHit(EntityLivingBase living) {
      this.AOEEffekt(living.posX, living.posY, living.posZ);
   }

   protected void arrowHitGround(BlockPos blockpos) {
      this.AOEEffekt(this.posX, this.posY, this.posZ);
   }

   protected void particleOnFlyArrow() {
      for(int k = 0; k < 4; ++k) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
      }

   }

   private void AOEEffekt(double posx, double posy, double posz) {
      double Umkreis = 1.7D;
      int Count = 0;
      int maxCount = 6;
      int sec = 110;
      List Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posx - Umkreis, posy - 1.0D, posz - Umkreis, posx + Umkreis, posy + 1.5D, posz + Umkreis));
      List Effektlist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
      Effektlist.clear();
      if (Mobliste.size() > 0) {
         for(int first = 0; first < Mobliste.size() && Count != maxCount && Count < maxCount; ++first) {
            if (Mobliste.get(first) instanceof EntityAnimal) {
               EntityAnimal animal = (EntityAnimal)Mobliste.get(first);
               if (!animal.isChild() && animal.getGrowingAge() < 4000) {
                  Effektlist.add(Mobliste.get(first));
                  ++Count;
               }
            } else if (Mobliste.get(first) instanceof EntityPlayer) {
               Effektlist.add(Mobliste.get(first));
               ++Count;
            } else if (Mobliste.get(first) instanceof EntityLiving && !(Mobliste.get(first) instanceof EntityVillager)) {
               Effektlist.add(Mobliste.get(first));
               ++Count;
            }
         }

         for(int a = 0; a < Effektlist.size(); ++a) {
            if (Effektlist.get(a) instanceof EntityAnimal) {
               EntityAnimal animal = (EntityAnimal)Effektlist.get(a);
               if (!animal.isChild() && animal.getGrowingAge() < 4000) {
                  animal.setGrowingAge(0);
                  animal.setInLove((EntityPlayer)null);
               }
            } else if (Effektlist.get(a) instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)Effektlist.get(a);
               player.addPotionEffect(new PotionEffect(PotionInit.PotionLove, sec, 0, true, true));
            } else if (Effektlist.get(a) instanceof EntityLiving && !(Effektlist.get(a) instanceof EntityVillager)) {
               EntityLiving mob = (EntityLiving)Effektlist.get(a);
               mob.addPotionEffect(new PotionEffect(PotionInit.PotionLove, sec, 0, true, true));
            }
         }
      }

      if (!this.world.isRemote) {
         for(int x = 0; x < 2; ++x) {
            this.SpawnHeart(this.world, posx, posy + 0.5D, posz);
            this.SpawnHeart(this.world, posx - 1.0D, posy + 0.5D, posz - 1.0D);
            this.SpawnHeart(this.world, posx, posy + 0.5D, posz - 1.0D);
            this.SpawnHeart(this.world, posx + 1.0D, posy + 0.5D, posz - 1.0D);
            this.SpawnHeart(this.world, posx - 1.0D, posy + 0.5D, posz);
            this.SpawnHeart(this.world, posx + 1.0D, posy + 0.5D, posz);
            this.SpawnHeart(this.world, posx - 1.0D, posy + 0.5D, posz + 1.0D);
            this.SpawnHeart(this.world, posx, posy + 0.5D, posz + 0.5D);
            this.SpawnHeart(this.world, posx + 1.0D, posy + 0.5D, posz + 1.0D);
         }

         for(int x = 0; x < 10; ++x) {
            this.Spawn(this.world, posx, posy + 0.5D, posz);
            this.Spawn(this.world, posx - 1.0D, posy + 0.5D, posz - 1.0D);
            this.Spawn(this.world, posx, posy + 0.5D, posz - 1.0D);
            this.Spawn(this.world, posx + 1.0D, posy + 0.5D, posz - 1.0D);
            this.Spawn(this.world, posx - 1.0D, posy + 0.5D, posz);
            this.Spawn(this.world, posx + 1.0D, posy + 0.5D, posz);
            this.Spawn(this.world, posx - 1.0D, posy + 0.5D, posz + 1.0D);
            this.Spawn(this.world, posx, posy + 0.5D, posz + 0.5D);
            this.Spawn(this.world, posx + 1.0D, posy + 0.5D, posz + 1.0D);
         }
      }

      this.setDead();
   }

   private void SpawnHeart(World worldObj, double posX, double posY, double posZ) {
      double motionX = this.rand.nextDouble() * 0.02D;
      double motionY = this.rand.nextDouble() * 0.02D;
      double motionZ = this.rand.nextDouble() * 0.02D;
      Initial.network.sendToAll(new ParticleHeartPacket(posX, posY - 0.2D, posZ, motionX, motionY, motionZ));
   }

   private void Spawn(World worldObj, double posX, double posY, double posZ) {
      double motionX = this.rand.nextDouble() * 0.02D;
      double motionY = this.rand.nextDouble() * 0.02D;
      double motionZ = this.rand.nextDouble() * 0.02D;
      Initial.network.sendToAll(new ParticleSplittingPacket(posX, posY - 0.2D, posZ, motionX, motionY, motionZ));
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowSplitLove);
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
