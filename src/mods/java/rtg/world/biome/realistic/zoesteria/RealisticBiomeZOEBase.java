package rtg.world.biome.realistic.zoesteria;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.surface.SurfaceGeneric;

public abstract class RealisticBiomeZOEBase extends RealisticBiomeBase {
   public RealisticBiomeZOEBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeZOEBase(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeZOEBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeZOEBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public SurfaceBase initSurface() {
      return new SurfaceGeneric(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public void initDecos() {
   }

   protected IBlockState getMixBlock1() {
      return this.baseBiome().topBlock;
   }

   protected IBlockState getMixBlock2() {
      return this.baseBiome().topBlock;
   }
}
