package artifacts.common.init;

import artifacts.Artifacts;
import artifacts.common.entity.EntityHallowStar;
import artifacts.common.entity.EntityMimic;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
   public ModEntities() {
   }

   public static void init() {
      EntityRegistry.registerModEntity(new ResourceLocation("artifacts", "mimic"), EntityMimic.class, "artifacts.mimic", 0, Artifacts.instance, 64, 3, true, 11695909, 2496257);
      EntityRegistry.registerModEntity(new ResourceLocation("artifacts", "hallow_star"), EntityHallowStar.class, "artifacts.hallow_star", 1, Artifacts.instance, 64, 3, true);
   }
}
