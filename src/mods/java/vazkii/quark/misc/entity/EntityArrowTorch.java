package vazkii.quark.misc.entity;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import vazkii.quark.misc.feature.ExtraArrows;

public class EntityArrowTorch extends EntityArrow {
   public EntityArrowTorch(World worldIn) {
      super(worldIn);
   }

   public EntityArrowTorch(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   public EntityArrowTorch(World worldIn, IPosition pos) {
      super(worldIn, pos.getX(), pos.getY(), pos.getZ());
   }

   @Nonnull
   protected ItemStack getArrowStack() {
      return new ItemStack(ExtraArrows.arrow_torch);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.getEntityWorld().isRemote) {
         this.spawnPotionParticles(1);
      }

   }

   public void spawnPotionParticles(int particleCount) {
      if (particleCount > 0) {
         for(int i = 0; i < particleCount; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   protected void onHit(RayTraceResult rayTrace) {
      super.onHit(rayTrace);
      if (!this.isDead) {
         if (!this.getEntityWorld().isRemote) {
            if (rayTrace.typeOfHit != Type.BLOCK) {
               return;
            }

            BlockPos pos = rayTrace.getBlockPos();
            BlockPos finalPos = pos.offset(rayTrace.sideHit);
            IBlockState state = this.getEntityWorld().getBlockState(finalPos);
            Block block = state.getBlock();
            if ((block.isAir(state, this.getEntityWorld(), finalPos) || block.isReplaceable(this.getEntityWorld(), finalPos)) && rayTrace.sideHit != EnumFacing.DOWN) {
               this.getEntityWorld().setBlockState(finalPos, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, rayTrace.sideHit));
               this.setDead();
            }
         }

      }
   }
}
