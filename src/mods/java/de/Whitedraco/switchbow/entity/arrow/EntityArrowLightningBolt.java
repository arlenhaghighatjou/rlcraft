package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.entity.EntityLightningBoltSwitchBow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowLightningBolt extends EntityArrowBase implements IThrowableEntity {
   private boolean spawnLightningBolt = true;
   private double damagesetter = ArrowConfig.DamageArrowLighningBolt.getDoub();

   public EntityArrowLightningBolt(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowLightningBolt(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowLightningBolt(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowLightningBolt);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote && this.spawnLightningBolt) {
         living.world.addWeatherEffect(new EntityLightningBoltSwitchBow(living.world, (double)living.getPosition().getX(), (double)living.getPosition().getY(), (double)living.getPosition().getZ(), false, false));
         this.spawnLightningBolt = false;
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote && this.spawnLightningBolt) {
         this.world.addWeatherEffect(new EntityLightningBoltSwitchBow(this.world, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), false, false));
         this.spawnLightningBolt = false;
         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setBoolean("spawnLightningBolt", this.spawnLightningBolt);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.spawnLightningBolt = compound.getBoolean("spawnLightningBolt");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
