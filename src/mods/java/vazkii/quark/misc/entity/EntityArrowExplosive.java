package vazkii.quark.misc.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.quark.misc.feature.ExtraArrows;

public class EntityArrowExplosive extends EntityArrow {
   public EntityArrowExplosive(World worldIn) {
      super(worldIn);
   }

   public EntityArrowExplosive(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   public EntityArrowExplosive(World worldIn, IPosition pos) {
      super(worldIn, pos.getX(), pos.getY(), pos.getZ());
   }

   @Nonnull
   protected ItemStack getArrowStack() {
      return new ItemStack(ExtraArrows.arrow_explosive);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.getEntityWorld().isRemote) {
         this.spawnPotionParticles(4);
      }

   }

   public void spawnPotionParticles(int particleCount) {
      if (particleCount > 0) {
         for(int i = 0; i < particleCount; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   protected void onHit(RayTraceResult rayTrace) {
      super.onHit(rayTrace);
      if (!this.world.isRemote) {
         Explosion explosion = new Explosion(this.world, this, rayTrace.hitVec.x, rayTrace.hitVec.y, rayTrace.hitVec.z, (float)ExtraArrows.explosiveArrowPower, false, ExtraArrows.explosiveArrowDestroysBlocks) {
            @Nullable
            public EntityLivingBase getExplosivePlacedBy() {
               return EntityArrowExplosive.this.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)EntityArrowExplosive.this.shootingEntity : null;
            }
         };
         if (!ForgeEventFactory.onExplosionStart(this.world, explosion)) {
            explosion.doExplosionA();
            explosion.doExplosionB(true);
         }
      }

      this.setDead();
   }
}
