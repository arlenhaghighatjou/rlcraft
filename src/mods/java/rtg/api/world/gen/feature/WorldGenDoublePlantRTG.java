package rtg.api.world.gen.feature;

import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;

public class WorldGenDoublePlantRTG extends WorldGenDoublePlant {
   public WorldGenDoublePlantRTG(EnumPlantType type) {
      super.setPlantType(type);
   }
}
