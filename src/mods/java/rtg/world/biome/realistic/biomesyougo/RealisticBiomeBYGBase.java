package rtg.world.biome.realistic.biomesyougo;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.helper.DecoHelperRandomSplit;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.surface.SurfaceGeneric;

public abstract class RealisticBiomeBYGBase extends RealisticBiomeBase {
   public RealisticBiomeBYGBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeBYGBase(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeBYGBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeBYGBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public SurfaceBase initSurface() {
      return new SurfaceGeneric(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public void initDecos() {
   }

   public void fallenTrees(IBlockState[] fallenTreeLogs, int[] fallenTreeChances) {
      if (fallenTreeLogs.length >= 1 && fallenTreeLogs.length == fallenTreeChances.length) {
         DecoBase[] fallenTreeDecos = new DecoBase[fallenTreeLogs.length];

         for(int i = 0; i < fallenTreeLogs.length; ++i) {
            fallenTreeDecos[i] = (new DecoFallenTree()).setLogBlock(fallenTreeLogs[i]).setLogConditionChance(8).setMaxSize(4);
         }

         this.addDeco((new DecoHelperRandomSplit()).setDecos(fallenTreeDecos).setChances(fallenTreeChances));
      }
   }
}
