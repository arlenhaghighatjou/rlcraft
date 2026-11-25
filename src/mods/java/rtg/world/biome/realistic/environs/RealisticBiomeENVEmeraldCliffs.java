package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeENVEmeraldCliffs extends RealisticBiomeENVBase {
   public RealisticBiomeENVEmeraldCliffs(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVEmeraldCliffs.TerrainBOPOvergrownCliffs(300.0F, 100.0F, 0.0F);
   }

   public static class TerrainBOPOvergrownCliffs extends TerrainBase {
      private float width;
      private float strength;
      private float lakeDepth;
      private float lakeWidth;
      private float terrainHeight;

      public TerrainBOPOvergrownCliffs(float mountainWidth, float mountainStrength, float depthLake) {
         this(mountainWidth, mountainStrength, depthLake, 260.0F, 68.0F);
      }

      public TerrainBOPOvergrownCliffs(float mountainWidth, float mountainStrength, float depthLake, float widthLake, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.lakeDepth = depthLake;
         this.lakeWidth = widthLake;
         this.terrainHeight = height;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainLonelyMountain(x, y, rtgWorld, river, this.strength, this.width, this.terrainHeight);
      }
   }
}
