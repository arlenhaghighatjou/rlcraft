package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyHit;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBody extends Entity {
   protected EntityParasiteBase parent;
   protected float damageMultiplier;
   private float eye;
   private float offx;
   private float offy;
   private float offz;
   private int inverted;
   private int partId;
   private boolean logicSide;

   public EntityBody(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side) {
      super(parent.world);
      this.setSize(width, height);
      this.parent = parent;
      this.damageMultiplier = damageMultiplier;
      this.eye = height * 0.8F;
      this.offx = offset;
      this.offy = offheight;
      this.offz = offset;
      this.inverted = inv;
      this.partId = id;
      this.logicSide = side;
   }

   public EntityBody(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side, float eyes) {
      this(parent, width, height, damageMultiplier, offset, offheight, inv, id, side);
      this.eye = eyes;
   }

   public void onUpdate() {
      if (this.parent == null) {
         this.world.removeEntityDangerously(this);
      } else if (this.parent.isDead) {
         this.world.removeEntityDangerously(this);
      } else {
         if (this.logicSide) {
            this.updatePositionWithParentSides();
         } else {
            this.updatePositionWithParentFront();
         }

         super.onUpdate();
      }
   }

   private void updatePositionWithParentSides() {
      float f17 = this.parent.rotationYaw * 0.017453292F;
      float f3 = MathHelper.sin(f17);
      float f18 = MathHelper.cos(f17);
      this.rotationYaw = this.parent.rotationYaw;
      this.setPositionAndUpdate(this.parent.posX + (double)this.inverted * (double)(f18 * this.offx), this.parent.posY + (double)this.offy, this.parent.posZ + (double)this.inverted * (double)(f3 * this.offz));
   }

   private void updatePositionWithParentFront() {
      float f19 = MathHelper.sin(this.parent.rotationYaw * 0.017453292F - this.parent.randomYawVelocity * 0.01F);
      float f14 = 0.17453292F;
      float f16 = MathHelper.cos(f14);
      float f4 = MathHelper.cos(this.parent.rotationYaw * 0.017453292F - this.parent.randomYawVelocity * 0.01F);
      this.rotationYaw = this.parent.rotationYaw;
      this.setPositionAndUpdate(this.parent.posX + (double)this.inverted * (double)(f19 * this.offx * f16), this.parent.posY + (double)this.offy, this.parent.posZ - (double)this.inverted * (double)(f4 * this.offx * f16));
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote && source.getImmediateSource() instanceof EntityPlayer) {
         SRPMain.network.sendToServer(new SRPPacketEntityBodyHit(this.parent.getEntityId(), this.partId));
         return false;
      } else {
         EntityBodyParts rTarget = (EntityBodyParts)this.parent;
         return rTarget.attackEntityBodyFrom(source, amount, this.partId, false);
      }
   }

   public void collideWithNearbyEntities() {
      List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(1.0D, 1.0D, 1.0D).offset(0.0D, 0.0D, 0.0D));
      double d0 = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
      double d1 = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;

      for(Entity entity : entities) {
         if (entity instanceof EntityLivingBase && entity != this.parent && !(entity instanceof EntityParasiteBase)) {
            double d2 = entity.posX - d0;
            double d3 = entity.posZ - d1;
            double d4 = d2 * d2 + d3 * d3;
            entity.addVelocity(d2 / d4 * 1.0D, 0.20000000298023224D, d3 / d4 * 1.0D);
            entity.attackEntityFrom(DamageSource.causeMobDamage(this.parent), 5.0F);
         }
      }

   }

   public int getId() {
      return this.partId;
   }

   public EntityParasiteBase getFather() {
      return this.parent;
   }

   public float getEyeHeight() {
      return this.eye;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return super.getCollisionBoundingBox();
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
   }

   protected void entityInit() {
   }

   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn || this.parent == entityIn;
   }

   @SideOnly(Side.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
   }
}
