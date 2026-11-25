package levelup2.util;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
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
   private final LoadingCache<ResourceLocation, LootTable> registeredLootTables = CacheBuilder.newBuilder().build(new LevelUpLootManager.Loader());
   private final File baseFolder = LevelUpConfig.lootDir.toFile();

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
         File file = new File(LevelUpLootManager.this.baseFolder, location.getResourcePath() + ".json");
         if (file.exists()) {
            if (file.isFile()) {
               String s;
               try {
                  s = Files.toString(file, Charsets.UTF_8);
               } catch (IOException var5) {
                  System.out.println("[LEVELUP] ERROR: Cannot load loot table " + file.toString());
                  return LootTable.EMPTY_LOOT_TABLE;
               }

               return ForgeHooks.loadLootTable(LevelUpLootManager.GSON_INSTANCE, location, s, true, (LootTableManager)null);
            } else {
               return LootTable.EMPTY_LOOT_TABLE;
            }
         } else {
            return null;
         }
      }
   }
}
