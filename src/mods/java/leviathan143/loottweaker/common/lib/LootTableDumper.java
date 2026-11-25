package leviathan143.loottweaker.common.lib;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileWriter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.RandomValueRange.Serializer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableDumper {
   public static final LootTableDumper DEFAULT = new LootTableDumper(new File("dumps/loot_tables"), (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new Serializer()).registerTypeAdapter(LootPool.class, new net.minecraft.world.storage.loot.LootPool.Serializer()).registerTypeAdapter(LootTable.class, new net.minecraft.world.storage.loot.LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new RobustLootEntrySerialiser()).registerTypeHierarchyAdapter(LootFunction.class, new RobustLootFunctionSerialiser()).registerTypeHierarchyAdapter(LootCondition.class, new RobustLootConditionSerialiser()).registerTypeHierarchyAdapter(EntityTarget.class, new net.minecraft.world.storage.loot.LootContext.EntityTarget.Serializer()).create());
   private static final Logger LOGGER = LogManager.getLogger();
   private final File dumpFolder;
   private final Gson gsonInstance;

   public LootTableDumper(File dumpFolder, Gson gsonInstance) {
      assert dumpFolder.isDirectory() : "Dump folder must be a directory";

      this.dumpFolder = dumpFolder;
      this.dumpFolder.mkdirs();
      this.gsonInstance = gsonInstance;
   }

   public File dump(World world, ResourceLocation tableId) {
      return this.dump(world.getLootTableManager().getLootTableFromLocation(tableId), tableId);
   }

   public File dump(LootTable lootTable, ResourceLocation tableId) {
      Preconditions.checkNotNull(lootTable);
      File dump = new File(this.dumpFolder, tableId.getResourceDomain() + '/' + tableId.getResourcePath() + ".json");

      try {
         dump.getParentFile().mkdirs();
         dump.createNewFile();
         FileWriter writer = new FileWriter(dump);
         Throwable var5 = null;

         try {
            JsonWriter dumper = this.gsonInstance.newJsonWriter(writer);
            dumper.setIndent("  ");
            this.gsonInstance.toJson(lootTable, lootTable.getClass(), dumper);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (writer != null) {
               if (var5 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  writer.close();
               }
            }

         }

         LOGGER.info("Loot table {} saved to {}", tableId, dump.getCanonicalPath());
         return dump;
      } catch (Throwable var17) {
         LOGGER.warn("Failed to dump loot table {}", tableId, var17);
         return null;
      }
   }
}
