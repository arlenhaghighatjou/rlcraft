package shadows.placebo.loot;

import net.lax1dude.eaglercraft.v1_8.cache.EaglerLoadingCache;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import shadows.placebo.Placebo;

public class PlaceboLootSystem {
   public static final Map<ResourceLocation, LootTable> PLACEBO_TABLES = new HashMap();

   public PlaceboLootSystem() {
   }

   public void register(EventBus bus) {
      bus.register(LootTableLoadEvent.class, this::loadTables);
   }

   public void loadTables(LootTableLoadEvent e) {
      if (e.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
         EaglerLoadingCache<ResourceLocation, LootTable> cache = e.getLootTableManager().registeredLootTables;

         for(Entry<ResourceLocation, LootTable> et : PLACEBO_TABLES.entrySet()) {
            if (cache.getIfPresent(et.getKey()) != et.getValue()) {
               cache.put(et.getKey(), et.getValue());
            }

            if (e.getLootTableManager().getLootTableFromLocation((ResourceLocation)et.getKey()) == LootTable.EMPTY_LOOT_TABLE) {
               Placebo.LOG.error("Failed to register Loot Table {}", et.getKey());
            }
         }

      }
   }

   public static void registerLootTable(ResourceLocation key, LootTable table) {
      if (!PLACEBO_TABLES.containsKey(key)) {
         PLACEBO_TABLES.put(key, table);
      } else {
         Placebo.LOG.warn("Duplicate loot entry detected, this is not allowed!  Key: " + key);
      }

   }
}
