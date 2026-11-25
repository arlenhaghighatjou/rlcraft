package rtg.api.world.deco.collection;

import rtg.api.config.BiomeConfig;
import rtg.api.world.deco.DecoCactus;
import rtg.api.world.deco.DecoDeadBush;

public class DecoCollectionDesert extends DecoCollectionBase {
   public DecoCollectionDesert(BiomeConfig config) {
      super(config);
      DecoCactus decoCactus = new DecoCactus();
      decoCactus.setMaxY(90);
      decoCactus.setStrengthFactor(3.0F);
      decoCactus.setChance(2);
      this.addDeco(decoCactus, config.ALLOW_CACTUS.get());
      DecoDeadBush decoDeadBush = new DecoDeadBush();
      decoDeadBush.setMaxY(128);
      decoDeadBush.setStrengthFactor(2.0F);
      decoDeadBush.setChance(3);
      this.addDeco(decoDeadBush);
   }
}
