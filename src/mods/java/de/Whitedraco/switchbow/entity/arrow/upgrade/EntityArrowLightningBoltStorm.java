package de.Whitedraco.switchbow.entity.arrow.upgrade;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.entity.EntityLightningBoltSwitchBow;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowLightningBoltStorm extends EntityArrowBase implements IThrowableEntity {
   private EaglercraftRandom random = new EaglercraftRandom();
   private boolean spawnLightningBolt = true;
   private double damagesetter = ArrowConfig.DamageArrowLightningStrom.getDoub();

   public EntityArrowLightningBoltStorm(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowLightningBoltStorm(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowLightningBoltStorm(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowLightningBoltStorm);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote && this.spawnLightningBolt) {
         BlockPos pos = new BlockPos(living.getPosition());
         this.spawnLightnigBolt(living.world, pos);
         this.spawnLightningBolt = false;
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote && this.spawnLightningBolt) {
         this.spawnLightnigBolt(this.world, blockpos);
         this.spawnLightningBolt = false;
         this.setDead();
      }

   }

   private void spawnLightnigBolt(World world, BlockPos pos) {
      EntityLightningBoltSwitchBow lightning = new EntityLightningBoltSwitchBow(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), false, true);
      world.addWeatherEffect(lightning);
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
