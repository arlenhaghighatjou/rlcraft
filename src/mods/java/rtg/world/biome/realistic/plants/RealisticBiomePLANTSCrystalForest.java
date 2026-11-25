package rtg.world.biome.realistic.plants;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.surface.SurfaceGeneric;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomePLANTSCrystalForest extends RealisticBiomePLANTSBase {
   public RealisticBiomePLANTSCrystalForest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomePLANTSCrystalForest.TerrainPLANTSCrystalForest();
   }

   public SurfaceBase initSurface() {
      return new SurfaceGeneric(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public static class TerrainPLANTSCrystalForest extends TerrainBase {
      private float baseHeight = 72.0F;
      private float peakyHillWavelength = 40.0F;
      private float peakyHillStrength = 20.0F;
      private float smoothHillWavelength = 20.0F;
      private float smoothHillStrength = 10.0F;

      public TerrainPLANTSCrystalForest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundNoiseAmplitudeHills, rtgWorld);
         float h = terrainGrasslandHills(x, y, rtgWorld, river, this.peakyHillWavelength, this.peakyHillStrength, this.smoothHillWavelength, this.smoothHillStrength, this.baseHeight);
         return riverized(this.groundNoise + h, river);
      }
   }
}
