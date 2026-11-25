package artifacts.common.init;

import artifacts.common.loot.functions.AddRandomEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

public class ModLootTables {
   public static final ResourceLocation MIMIC_UNDERGROUND = new ResourceLocation("artifacts", "mimic_underground");
   public static final ResourceLocation CHEST_UNDERGROUND = new ResourceLocation("artifacts", "chest_underground");
   public static final ResourceLocation SACRIFICIAL_REWARD = new ResourceLocation("artifacts", "sacrificial_reward");

   public ModLootTables() {
   }

   public static void init() {
      LootFunctionManager.registerFunction(new AddRandomEffect.Serializer());
   }
}
