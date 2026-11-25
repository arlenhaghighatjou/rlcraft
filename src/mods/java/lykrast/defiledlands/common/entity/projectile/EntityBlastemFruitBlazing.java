package lykrast.defiledlands.common.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBlastemFruitBlazing extends EntityBlastemFruit {
   public EntityBlastemFruitBlazing(World worldIn) {
      super(worldIn);
      this.damage = 10.0F;
      this.explosion = 1.5F;
   }

   public EntityBlastemFruitBlazing(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
      this.damage = 10.0F;
      this.explosion = 1.5F;
   }

   public EntityBlastemFruitBlazing(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
   }

   protected void onImpact(RayTraceResult result) {
      if (result.entityHit != null) {
         result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.damage);
         result.entityHit.setFire(8);
      }

      if (!this.world.isRemote) {
         this.explosion(true);
         this.setDead();
      }

   }
}
