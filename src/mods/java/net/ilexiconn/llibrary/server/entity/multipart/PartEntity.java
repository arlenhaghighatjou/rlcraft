package net.ilexiconn.llibrary.server.entity.multipart;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartEntity extends Entity {
   protected EntityLiving parent;
   protected float radius;
   protected float angleYaw;
   protected float offsetY;
   protected float damageMultiplier;

   public PartEntity(EntityLiving parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
      super(parent.world);
      this.setSize(sizeX, sizeY);
      this.parent = parent;
      this.radius = radius;
      this.angleYaw = (angleYaw + 90.0F) * 0.017453292F;
      this.offsetY = offsetY;
      this.damageMultiplier = damageMultiplier;
   }

   public void onUpdate() {
      this.setPositionAndUpdate(this.parent.posX + (double)this.radius * Math.cos((double)this.parent.renderYawOffset * 0.017453292519943295D + (double)this.angleYaw), this.parent.posY + (double)this.offsetY, this.parent.posZ + (double)this.radius * Math.sin((double)this.parent.renderYawOffset * 0.017453292519943295D + (double)this.angleYaw));
      if (!this.world.isRemote) {
         this.collideWithNearbyEntities();
      }

      if (this.parent.isDead) {
         this.world.removeEntityDangerously(this);
      }

      super.onUpdate();
   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      return this.parent.attackEntityFrom(source, damage * this.damageMultiplier);
   }

   public boolean isEntityEqual(Entity entity) {
      return this == entity || this.parent == entity;
   }

   public void entityInit() {
   }

   public void readEntityFromNBT(NBTTagCompound nbtTag) {
   }

   public void writeEntityToNBT(NBTTagCompound nbtTag) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public void collideWithNearbyEntities() {
      List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
      entities.stream().filter((entity) -> {
         return entity != this.parent && !(entity instanceof PartEntity) && entity.canBePushed();
      }).forEach((entity) -> {
         entity.applyEntityCollision(this.parent);
      });
   }

   @SideOnly(Side.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
   }
}
