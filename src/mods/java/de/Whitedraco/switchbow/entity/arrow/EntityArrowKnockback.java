package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowKnockback extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowKnockback.getDoub();

   public EntityArrowKnockback(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowKnockback(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowKnockback(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowKnockback);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         double knockback = 5.0D;
         if (f1 > 0.0F) {
            living.addVelocity(this.motionX * knockback * 0.6000000238418579D / (double)f1, 1.0D, this.motionZ * knockback * 0.6000000238418579D / (double)f1);
         }
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
