package rtg.world.biome.realistic.fyrecraft;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.MountainsWithPassesEffect;

public class RealisticBiomeFYREMegaMountains extends RealisticBiomeFYREBase {
   public RealisticBiomeFYREMegaMountains(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeFYREMegaMountains.TerrainBOPMountainPeaks(120.0F, 100.0F);
   }

   public static class TerrainBOPMountainPeaks extends TerrainBase {
      private float width;
      private float strength;
      private float terrainHeight;
      private float spikeWidth;
      private float spikeHeight;
      private HeightEffect heightEffect;

      public TerrainBOPMountainPeaks(float mountainWidth, float mountainStrength) {
         this(mountainWidth, mountainStrength, 90.0F);
      }

      public TerrainBOPMountainPeaks(float mountainWidth, float mountainStrength, float height) {
         this.spikeWidth = 30.0F;
         this.spikeHeight = 50.0F;
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.terrainHeight = height;
         MountainsWithPassesEffect mountainEffect = new MountainsWithPassesEffect();
         mountainEffect.mountainHeight = this.strength;
         mountainEffect.mountainWavelength = this.width;
         mountainEffect.spikeHeight = this.spikeHeight;
         mountainEffect.spikeWavelength = this.spikeWidth;
         this.heightEffect = new JitterEffect(7.0F, 10.0F, mountainEffect);
         this.heightEffect = new JitterEffect(3.0F, 6.0F, this.heightEffect);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.heightEffect.added(rtgWorld, (float)x, (float)y) + this.terrainHeight, river);
      }
   }
}
