package de.Whitedraco.switchbow.entity.arrow;

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

public class EntityArrowFire extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowFire.getDoub();

   public EntityArrowFire(World worldIn) {
      super(worldIn);
      this.setFire(100);
      this.damage = this.damagesetter;
   }

   public EntityArrowFire(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.setFire(100);
      this.damage = this.damagesetter;
   }

   public EntityArrowFire(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.setFire(100);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowFire);
   }

   protected void arrowHit(EntityLivingBase living) {
      this.world.spawnEntity(new EntityFire(this.world, living.posX, living.posY, living.posZ));
      this.setDead();
   }

   protected void arrowHitGround(BlockPos blockpos) {
      this.world.spawnEntity(new EntityFire(this.world, this.posX, this.posY, this.posZ));
      this.setDead();
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
