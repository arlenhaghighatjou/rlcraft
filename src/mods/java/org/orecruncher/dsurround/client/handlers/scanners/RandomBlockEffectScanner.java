package org.orecruncher.dsurround.client.handlers.scanners;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.fx.BlockEffect;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.lib.scanner.RandomScanner;
import org.orecruncher.dsurround.lib.scanner.ScanLocus;
import org.orecruncher.dsurround.registry.blockstate.BlockStateData;
import org.orecruncher.dsurround.registry.blockstate.BlockStateUtil;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class RandomBlockEffectScanner extends RandomScanner {
   private static final int ITERATION_COUNT = 667;
   public static final int NEAR_RANGE = 16;
   public static final int FAR_RANGE = 32;

   public RandomBlockEffectScanner(@Nonnull ScanLocus locus, int range) {
      super(locus, "RandomBlockScanner: " + range, range, 667);
      this.setLogger(ModBase.log());
   }

   protected boolean interestingBlock(@Nonnull IBlockState state) {
      return BlockStateUtil.getStateData(state).hasSoundsOrEffects();
   }

   public void blockScan(@Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom rand) {
      IBlockAccessEx provider = this.locus.getWorld();
      BlockStateData profile = BlockStateUtil.getStateData(state);
      BlockEffect[] effects = profile.getEffects();
      if (effects != BlockStateData.NO_EFFECTS) {
         for(BlockEffect be : effects) {
            if (be.canTrigger(provider, state, pos, rand)) {
               be.doEffect(provider, state, pos, rand);
            }
         }
      }

      SoundEffect sound = profile.getSoundToPlay(rand);
      if (sound != null) {
         sound.doEffect(provider, state, pos, rand);
      }

   }
}
