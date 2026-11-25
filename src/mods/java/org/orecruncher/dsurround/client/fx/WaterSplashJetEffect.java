package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleJet;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleWaterSplash;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class WaterSplashJetEffect extends JetEffect {
   private static final Vec3i[] cardinal_offsets = new Vec3i[]{new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1)};

   public WaterSplashJetEffect(int chance) {
      super(chance);
   }

   @Nonnull
   public BlockEffectType getEffectType() {
      return BlockEffectType.SPLASH_JET;
   }

   private static boolean isLiquidBlock(IBlockState state) {
      return state.getMaterial().isLiquid();
   }

   private static boolean isUnboundedLiquid(IBlockAccessEx provider, BlockPos pos) {
      for(int i = 0; i < cardinal_offsets.length; ++i) {
         BlockPos tp = pos.add(cardinal_offsets[i]);
         IBlockState state = provider.getBlockState(tp);
         if (state.getMaterial() == Material.AIR) {
            return true;
         }

         if (isLiquidBlock(state) && !WorldUtils.isFullWaterBlock(state) && !provider.getBlockState(tp.up()).getMaterial().isLiquid()) {
            return true;
         }
      }

      return false;
   }

   private int liquidBlockCount(IBlockAccessEx provider, BlockPos pos) {
      MutableBlockPos workBlock = new MutableBlockPos(pos);

      int count;
      for(count = 0; count < 10 && provider.getBlockState(workBlock).getMaterial().isLiquid(); ++count) {
         workBlock.setY(workBlock.getY() + 1);
      }

      return MathStuff.clamp(count, 0, 10);
   }

   public static boolean isValidSpawnBlock(IBlockAccessEx provider, BlockPos pos) {
      return isValidSpawnBlock(provider, provider.getBlockState(pos), pos);
   }

   private static boolean isValidSpawnBlock(IBlockAccessEx provider, IBlockState state, BlockPos pos) {
      if (!state.getMaterial().isLiquid()) {
         return false;
      } else if (isUnboundedLiquid(provider, pos)) {
         BlockPos down = pos.down();
         if (provider.getBlockState(down).getMaterial().isSolid()) {
            return true;
         } else {
            return !isUnboundedLiquid(provider, down);
         }
      } else {
         return provider.getBlockState(pos.up()).getBlock() instanceof BlockDynamicLiquid;
      }
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      return isValidSpawnBlock(provider, state, pos) && super.canTrigger(provider, state, pos, random);
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      int strength = this.liquidBlockCount(provider, pos);
      if (strength > 1) {
         float height = BlockLiquid.getLiquidHeightPercent(state.getBlock().getMetaFromState(state)) + 0.1F;
         double y = (double)(height + (float)pos.getY());
         ParticleJet effect = new ParticleWaterSplash(strength, provider.getWorld(), pos, (double)pos.getX() + 0.5D, y, (double)pos.getZ() + 0.5D);
         this.addEffect(effect);
      }

   }
}
