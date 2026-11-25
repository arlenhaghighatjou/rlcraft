package de.Whitedraco.switchbow.entity.arrow.upgrade;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.entity.EntityFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowFireUpgrade extends EntityArrowBase implements IThrowableEntity {
   private double rad = 1.0D;
   private double damagesetter = ArrowConfig.DamageArrowFireUpgrade.getDoub();

   public EntityArrowFireUpgrade(World worldIn) {
      super(worldIn);
      this.setFire(100);
      this.damage = this.damagesetter;
   }

   public EntityArrowFireUpgrade(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.setFire(100);
      this.damage = this.damagesetter;
   }

   public EntityArrowFireUpgrade(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.setFire(100);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowFireUpgrade);
   }

   protected void arrowHit(EntityLivingBase living) {
      double h = 1.0D;
      double g = h * 2.0D / Math.sqrt(3.0D) / 2.0D;
      this.world.spawnEntity(new EntityFire(this.world, living.posX - h / 2.0D, living.posY, living.posZ - g));
      this.world.spawnEntity(new EntityFire(this.world, living.posX - h / 2.0D, living.posY, living.posZ + g));
      this.world.spawnEntity(new EntityFire(this.world, living.posX + h / 2.0D, living.posY, living.posZ));
      this.setDead();
   }

   protected void arrowHitGround(BlockPos blockpos) {
      double h = 1.0D;
      double g = h * 2.0D / Math.sqrt(3.0D) / 2.0D;
      this.world.spawnEntity(new EntityFire(this.world, this.posX - h / 2.0D, this.posY, this.posZ - g));
      this.world.spawnEntity(new EntityFire(this.world, this.posX - h / 2.0D, this.posY, this.posZ + g));
      this.world.spawnEntity(new EntityFire(this.world, this.posX + h / 2.0D, this.posY, this.posZ));
      this.setDead();
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
