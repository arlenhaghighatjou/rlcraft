package ivorius.reccomplex.world.storage.loot;

import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.util.Collection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class RCLoot {
   public RCLoot() {
   }

   public static void registerVanillaLootTables() {
      registerVanillaLootTables(LootTableList.getAll());
   }

   private static void registerVanillaLootTables(Collection<ResourceLocation> keys) {
      for(ResourceLocation key : keys) {
         WeightedItemCollectionRegistry.INSTANCE.register(key.toString(), "minecraft", new VanillaLootTable(key), true, LeveledRegistry.Level.INTERNAL);
      }

   }
}
