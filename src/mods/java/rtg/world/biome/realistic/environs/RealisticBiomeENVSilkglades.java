package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeENVSilkglades extends RealisticBiomeENVBase {
   public RealisticBiomeENVSilkglades(Biome biome) {
      super(biome);
   }

   public Biome preferredBeach() {
      return this.baseBiome();
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVSilkglades.TerrainVanillaSwampland();
   }

   public static class TerrainVanillaSwampland extends TerrainBase {
      public TerrainVanillaSwampland() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainMarsh(x, y, rtgWorld, 61.5F, river);
      }
   }
}
