package lykrast.defiledlands.common.entity.projectile;

import lykrast.defiledlands.common.item.IPellet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityRavagerProjectile extends EntitySmallFireball {
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityRavagerProjectile.class, DataSerializers.ITEM_STACK);
   private float damage;
   private float speed;

   public EntityRavagerProjectile(World worldIn) {
      super(worldIn);
      this.damage = 12.0F;
      this.speed = 1.0F;
   }

   public EntityRavagerProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, float speed) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      double d0 = (double)MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
      this.accelerationX = accelX / d0 * 0.2D * (double)speed;
      this.accelerationY = accelY / d0 * 0.2D * (double)speed;
      this.accelerationZ = accelZ / d0 * 0.2D * (double)speed;
      this.damage = 12.0F;
      float f = 1.0F - 0.05F / speed;
      this.speed = f;
   }

   public EntityRavagerProjectile(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ, float speed) {
      super(worldIn, x, y, z, accelX, accelY, accelZ);
      this.accelerationX *= (double)speed;
      this.accelerationY *= (double)speed;
      this.accelerationZ *= (double)speed;
      this.damage = 12.0F;
      float f = 1.0F - 0.05F / speed;
      this.speed = f;
   }

   protected void entityInit() {
      super.entityInit();
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entityHit != null) {
            boolean flag = result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), this.getDamage());
            if (flag) {
               this.applyEnchantments(this.shootingEntity, result.entityHit);
               if (result.entityHit instanceof EntityLivingBase) {
                  ItemStack stack = this.getItem();
                  if (stack.getItem() instanceof IPellet) {
                     ((IPellet)stack.getItem()).onHit((EntityLivingBase)result.entityHit, this.shootingEntity, stack);
                  }
               }
            }
         }

         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("Damage", this.damage);
      compound.setFloat("Speed", this.speed);
      ItemStack itemstack = this.getItem();
      if (!itemstack.isEmpty()) {
         compound.setTag("Item", itemstack.writeToNBT(new NBTTagCompound()));
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.damage = compound.getFloat("Damage");
      this.speed = compound.getFloat("Speed");
      ItemStack itemstack = new ItemStack(compound.getCompoundTag("Item"));
      if (itemstack.isEmpty()) {
         this.setDead();
      } else {
         this.setItem(itemstack);
      }

   }

   protected float getMotionFactor() {
      return this.getSpeed();
   }

   public float getDamage() {
      return this.damage;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getSpeed() {
      return this.speed;
   }

   public void setSpeed(float speed) {
      this.speed = speed;
   }

   public ItemStack getItem() {
      return (ItemStack)this.getDataManager().get(ITEM);
   }

   public void setItem(ItemStack stack) {
      this.getDataManager().set(ITEM, stack);
      this.getDataManager().setDirty(ITEM);
   }

   protected boolean isFireballFiery() {
      return false;
   }
}
