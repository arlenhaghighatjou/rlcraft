package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeENVStoneFlats extends RealisticBiomeENVBase {
   public RealisticBiomeENVStoneFlats(Biome biome) {
      super(biome, RealisticBiomeBase.BeachType.STONE);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVStoneFlats.TerrainVanillaPlains();
   }

   public static class TerrainVanillaPlains extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainVanillaPlains() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}
