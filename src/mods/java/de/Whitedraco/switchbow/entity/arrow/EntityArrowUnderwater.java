package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowUnderwater extends EntityArrowBase implements IThrowableEntity {
   private double olddamage = ArrowConfig.DamageArrowUnderwater.getDoub();
   private boolean savedamage = false;

   public EntityArrowUnderwater(World worldIn) {
      super(worldIn);
      this.damage = this.olddamage;
      this.setUnderwatertotrue();
   }

   public EntityArrowUnderwater(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.olddamage;
      this.setUnderwatertotrue();
   }

   public EntityArrowUnderwater(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.olddamage;
      this.setUnderwatertotrue();
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowUnderwater);
   }

   protected void arrowHit(EntityLivingBase living) {
   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote) {
         if (!this.savedamage) {
            this.olddamage = this.damage;
            this.savedamage = true;
         }

         if (this.isInWater()) {
            this.setDamage(this.olddamage + 3.0D);
         } else {
            this.setDamage(this.olddamage);
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setDouble("olddamage", this.olddamage);
      compound.setBoolean("savedamage", this.savedamage);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.olddamage = compound.getDouble("olddamage");
      this.savedamage = compound.getBoolean("savedamage");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
