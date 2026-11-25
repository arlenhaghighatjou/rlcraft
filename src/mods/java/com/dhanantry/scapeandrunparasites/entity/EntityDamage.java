package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityDamage extends Entity {
   private int lifeTicks;
   private EntityLivingBase caster;
   private EaglercraftUUID casterUuid;
   private float mobDamage;
   private boolean pulling;
   private float str;
   private boolean follow;
   private Entity ffollow;

   public EntityDamage(World worldIn) {
      super(worldIn);
      this.lifeTicks = 10;
      this.setSize(1.2F, 0.9F);
   }

   public EntityDamage(World worldIn, double x, double y, double z, float rotation, EntityLivingBase casterIn, float damage, boolean pull, float strenght) {
      this(worldIn);
      this.setCaster(casterIn);
      this.rotationYaw = rotation * 57.295776F;
      this.setPosition(x, y, z);
      this.mobDamage = damage;
      this.pulling = pull;
      this.str = strenght;
   }

   public EntityDamage(World worldIn, double x, double y, double z, float rotation, EntityLivingBase casterIn, float damage, boolean pull, float strenght, float w, float h) {
      this(worldIn, x, y, z, rotation, casterIn, damage, pull, strenght);
      this.setSize(w, h);
   }

   protected void entityInit() {
   }

   public void setCaster(@Nullable EntityLivingBase in) {
      this.caster = in;
      this.casterUuid = in == null ? null : in.getUniqueID();
   }

   public void setFollower(Entity in) {
      this.follow = true;
      this.ffollow = in;
   }

   @Nullable
   public EntityLivingBase getCaster() {
      if (this.caster == null && this.casterUuid != null && this.world instanceof WorldServer) {
         Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.casterUuid);
         if (entity instanceof EntityLivingBase) {
            this.caster = (EntityLivingBase)entity;
         }
      }

      return this.caster;
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.casterUuid = compound.getUniqueId("OwnerUUID");
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      if (this.casterUuid != null) {
         compound.setUniqueId("OwnerUUID", this.casterUuid);
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote) {
         if (this.follow) {
            if (this.ffollow == null) {
               if (--this.lifeTicks < 0) {
                  this.setDead();
               }
            } else {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(1.5D);

               for(EntityLivingBase entityIn : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
                  if (this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false) == null) {
                     this.damage(entityIn);
                  }
               }

               int i1 = MathHelper.floor(this.posY + 0.1D);
               double l1 = this.posX;
               double i2 = this.posZ;
               boolean flag = false;
               int offsetT = 0;
               int BGrange = 2;
               int BGheight = 2;

               for(int k2 = -1 * BGrange; k2 <= 1 * BGrange; ++k2) {
                  for(int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                     for(int j = 1 + offsetT; j <= BGheight + offsetT; ++j) {
                        double i3 = l1 + (double)k2;
                        double k = (double)(i1 + j);
                        double l = i2 + (double)l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        IBlockState iblockstate = this.world.getBlockState(blockpos);
                        Block block = iblockstate.getBlock();
                        float bHard = iblockstate.getBlockHardness(this.world, blockpos);
                        if (((double)bHard <= 0.3D && bHard > 0.0F || block instanceof IMetaName) && block != SRPBlocks.BiomeHeart && block != SRPBlocks.ColonyHeart && block != SRPBlocks.ParasiteRubbleDense && block != SRPBlocks.ParasiteCanisterActive && block != Blocks.AIR && block.canEntityDestroy(iblockstate, this.world, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this.getCaster(), blockpos, iblockstate)) {
                           flag = this.world.destroyBlock(blockpos, true) || flag;
                        }
                     }
                  }
               }

               this.posX = this.ffollow.posX;
               this.posY = this.ffollow.posY;
               this.posZ = this.ffollow.posZ;
               if (!this.ffollow.isEntityAlive()) {
                  this.ffollow = null;
               }
            }
         } else {
            for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(0.3D, 0.0D, 0.2D))) {
               this.damage(entitylivingbase);
            }

            if (--this.lifeTicks < 0) {
               this.setDead();
            }
         }
      }

   }

   private void damage(EntityLivingBase in) {
      if (!(in instanceof EntityParasiteBase)) {
         EntityLivingBase entitylivingbase = this.getCaster();
         if (in.isEntityAlive() && !in.getIsInvulnerable() && in != entitylivingbase) {
            if (entitylivingbase == null) {
               in.attackEntityFrom(DamageSource.GENERIC.setDamageBypassesArmor(), this.mobDamage);
            } else {
               if (entitylivingbase.isOnSameTeam(in)) {
                  return;
               }

               if (this.pulling) {
                  if (in instanceof EntityPlayer) {
                     this.knockBack2(in, this.str, in.posX - this.caster.posX, in.posZ - this.caster.posZ);
                  } else {
                     this.knockBack2(in, this.str / 3.0F, in.posX - this.caster.posX, in.posZ - this.caster.posZ);
                  }
               } else if (in instanceof EntityPlayer) {
                  this.knockBack2(in, this.str, this.caster.posX - in.posX, this.caster.posZ - in.posZ);
               } else {
                  this.knockBack2(in, this.str, this.caster.posX - in.posX, this.caster.posZ - in.posZ);
               }

               entitylivingbase.attackEntityAsMob(in);
            }
         }

      }
   }

   private void knockBack2(Entity entityIn, float strength, double xRatio, double zRatio) {
      float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
      entityIn.motionX /= 2.0D;
      entityIn.motionZ /= 2.0D;
      entityIn.motionX -= xRatio / (double)f * (double)strength;
      entityIn.motionZ -= zRatio / (double)f * (double)strength;
      if (entityIn.onGround) {
         entityIn.motionY /= 2.0D;
         entityIn.motionY += (double)strength;
         if (entityIn.motionY > 0.4000000059604645D) {
            entityIn.motionY = 0.4000000059604645D;
         }
      }

   }
}
