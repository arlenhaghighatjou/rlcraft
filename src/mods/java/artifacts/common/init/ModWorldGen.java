package artifacts.common.init;

import artifacts.common.ModConfig;
import artifacts.common.worldgen.WorldGenUndergroundChest;
import java.util.Arrays;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModWorldGen {
   public ModWorldGen() {
   }

   public static void init() {
      GameRegistry.registerWorldGenerator(new WorldGenUndergroundChest(Arrays.asList(ModConfig.general.undergroundChestDimensions)), 0);
   }
}
