package leviathan143.loottweaker.common.zenscript;

import crafttweaker.CraftTweakerAPI;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import leviathan143.loottweaker.common.LTConfig;
import leviathan143.loottweaker.common.darkmagic.LootTableManagerAccessors;
import leviathan143.loottweaker.common.lib.LootTableDumper;
import leviathan143.loottweaker.common.lib.LootTableFinder;
import leviathan143.loottweaker.common.mutable_loot.MutableLootTable;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootTableWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableTweakManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, ZenLootTableWrapper> tweakedTables = new HashMap();
   private final Map<ResourceLocation, ZenLootTableWrapper> tableBuilders = new HashMap();
   private final LootTweakerContext context;

   LootTableTweakManager(LootTweakerContext context) {
      this.context = context;
   }

   public ZenLootTableWrapper getTable(String tableName) {
      return this.getTableInternal(tableName);
   }

   public ZenLootTableWrapper getTableUnchecked(String tableName) {
      return this.getTable(tableName);
   }

   private ZenLootTableWrapper getTableInternal(String tableName) {
      ResourceLocation tableId = new ResourceLocation(tableName);
      if (this.tableBuilders.containsKey(tableId)) {
         return (ZenLootTableWrapper)this.tableBuilders.get(tableId);
      } else {
         ZenLootTableWrapper wrapper = (ZenLootTableWrapper)this.tweakedTables.get(tableId);
         if (wrapper == null) {
            wrapper = this.context.wrapLootTable(tableId);
            if (!wrapper.isValid()) {
               this.context.getErrorHandler().error("No loot table with name %s exists!", tableName);
            } else {
               this.tweakedTables.put(tableId, wrapper);
            }
         }

         return wrapper;
      }
   }

   public ZenLootTableWrapper newTable(String id) {
      ResourceLocation tableId = new ResourceLocation(id);
      if (LTConfig.warnings.newTableMinecraftNamespace && tableId.getResourceDomain().equals("minecraft")) {
         if (id.startsWith("minecraft")) {
            this.context.getErrorHandler().warn("Table id '%s' explicitly uses the minecraft namespace, this is discouraged", id);
         } else {
            this.context.getErrorHandler().warn("Table id '%s' implicitly uses the minecraft namespace, this is discouraged", id);
         }
      }

      if (LootTableFinder.DEFAULT.exists(tableId)) {
         this.context.getErrorHandler().error("Table id '%s' already in use", id);
         return this.context.wrapLootTable(tableId);
      } else {
         ZenLootTableWrapper builder = this.context.wrapLootTable(tableId);
         this.tableBuilders.put(tableId, builder);
         CraftTweakerAPI.logInfo("Created new table '" + id + "'");
         return builder;
      }
   }

   public void onServerStart(MinecraftServer server) {
      File worldLootTables = server.getActiveAnvilConverter().getFile(server.getFolderName(), "data/loot_tables");
      LootTableDumper dumper = new LootTableDumper(worldLootTables, LootTableManagerAccessors.getGsonInstance());

      for(ZenLootTableWrapper builder : this.tableBuilders.values()) {
         MutableLootTable mutableTable = new MutableLootTable(builder.getId(), new HashMap());
         builder.applyTweakers(mutableTable);
         dumper.dump(mutableTable.toImmutable(), builder.getId());
      }

   }

   public LootTable tweakTable(ResourceLocation tableId, LootTable table) {
      if (this.tweakedTables.containsKey(tableId)) {
         if (table.isFrozen()) {
            LOGGER.debug("Skipped modifying loot table {} because it is frozen", tableId);
            return table;
         } else {
            MutableLootTable mutableTable = new MutableLootTable(table, tableId);
            ((ZenLootTableWrapper)this.tweakedTables.get(tableId)).applyTweakers(mutableTable);
            return mutableTable.toImmutable();
         }
      } else {
         return table;
      }
   }
}
