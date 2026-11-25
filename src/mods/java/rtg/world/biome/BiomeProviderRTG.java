package rtg.world.biome;

import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import rtg.api.util.Logger;
import rtg.api.world.RTGWorld;

public class BiomeProviderRTG extends BiomeProvider {
   public BiomeProviderRTG(RTGWorld rtgWorld) {
      super(rtgWorld.world().getWorldInfo());
      this.maybeRemoveRivers();
   }

   private void maybeRemoveRivers() {
      if (this.genBiomes instanceof GenLayerRiverMix) {
         Logger.debug("Removing vanilla river layer");
         ((GenLayerRiverMix)this.genBiomes).riverPatternGeneratorChain = ((GenLayerRiverMix)this.genBiomes).biomePatternGeneratorChain;
      } else {
         Logger.error("Failed to remove the vanilla river layer; Wrong GenLayer type: {}", this.genBiomes.getClass().getName());
      }

   }
}
