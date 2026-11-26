package levelup2.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import net.lax1dude.eaglercraft.v1_8.cache.EaglerLoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import levelup2.config.LevelUpConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.RandomValueRange.Serializer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.ForgeHooks;

public class LevelUpLootManager {
   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new Serializer()).registerTypeAdapter(LootPool.class, new net.minecraft.world.storage.loot.LootPool.Serializer()).registerTypeAdapter(LootTable.class, new net.minecraft.world.storage.loot.LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new net.minecraft.world.storage.loot.LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new net.minecraft.world.storage.loot.functions.LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new net.minecraft.world.storage.loot.conditions.LootConditionManager.Serializer()).registerTypeHierarchyAdapter(EntityTarget.class, new net.minecraft.world.storage.loot.LootContext.EntityTarget.Serializer()).create();
   private final EaglerLoadingCache<ResourceLocation, LootTable> registeredLootTables = CacheBuilder.newBuilder().<ResourceLocation, LootTable>build(new LevelUpLootManager.Loader());
   private final VFile2 baseFolder = LevelUpConfig.lootDir;

   public LevelUpLootManager() {
      this.reloadLootTables();
   }

   public LootTable getLootTableFromLocation(ResourceLocation resources) {
      return (LootTable)this.registeredLootTables.getUnchecked(resources);
   }

   public void reloadLootTables() {
      this.registeredLootTables.invalidateAll();

      for(ResourceLocation location : Library.getLootTables()) {
         this.getLootTableFromLocation(location);
      }

   }

   class Loader extends CacheLoader<ResourceLocation, LootTable> {
      private Loader() {
      }

      public LootTable load(ResourceLocation location) throws Exception {
         if (location.getResourcePath().contains(".")) {
            System.out.println("[LEVELUP] ERROR: Cannot load loot table " + location.getResourcePath());
            return LootTable.EMPTY_LOOT_TABLE;
         } else {
            LootTable table = this.loadLootTable(location);
            if (table == null) {
               System.out.println("[LEVELUP] ERROR: Cannot load loot table " + location.getResourcePath());
               table = LootTable.EMPTY_LOOT_TABLE;
            }

            return table;
         }
      }

      private LootTable loadLootTable(ResourceLocation location) {
         VFile2 file = new VFile2(LevelUpLootManager.this.baseFolder, location.getResourcePath() + ".json");
         String s = file.getAllChars();
         if (s != null) {
            return ForgeHooks.loadLootTable(LevelUpLootManager.GSON_INSTANCE, location, s, true, (LootTableManager)null);
         } else {
            return null;
         }
      }
   }
}
