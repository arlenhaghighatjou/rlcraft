package org.orecruncher.dsurround.client.fx;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.ParticleSteamCloud;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleJet;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleSteamJet;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class SteamJetEffect extends JetEffect {
   public SteamJetEffect(int chance) {
      super(chance);
   }

   protected static int lavaCount(IBlockAccessEx provider, BlockPos pos, boolean fastCheck) {
      int blockCount = 0;

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               IBlockState theBlock = provider.getBlockState(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
               if (ParticleSteamCloud.isHotBlock(theBlock)) {
                  if (fastCheck) {
                     return 1;
                  }

                  ++blockCount;
               }
            }
         }
      }

      return blockCount;
   }

   @Nonnull
   public BlockEffectType getEffectType() {
      return BlockEffectType.STEAM_JET;
   }

   public static boolean isValidSpawnBlock(@Nonnull IBlockAccessEx provider, @Nonnull BlockPos pos) {
      return isValidSpawnBlock(provider.getBlockState(pos), provider, pos);
   }

   public static boolean isValidSpawnBlock(@Nonnull IBlockState state, @Nonnull IBlockAccessEx provider, @Nonnull BlockPos pos) {
      if (state.getMaterial().isLiquid() && provider.isAirBlock(pos.up())) {
         return lavaCount(provider, pos, true) > 0;
      } else {
         return false;
      }
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom random) {
      return isValidSpawnBlock(state, provider, pos) && super.canTrigger(provider, state, pos, random);
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom random) {
      int strength = lavaCount(provider, pos, false);
      if (strength > 0) {
         float spawnHeight = BlockLiquid.getLiquidHeight(state, provider, pos);
         ParticleJet effect = new ParticleSteamJet(strength, provider.getWorld(), (double)pos.getX() + 0.5D, (double)spawnHeight, (double)pos.getZ() + 0.5D);
         this.addEffect(effect);
      }

   }
}
