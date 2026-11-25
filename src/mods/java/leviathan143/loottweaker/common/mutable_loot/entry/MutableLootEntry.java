package leviathan143.loottweaker.common.mutable_loot.entry;

import java.util.List;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public interface MutableLootEntry {
   Logger __LOGGER__ = LogManager.getLogger("loottweaker.mutable_loot");

   static MutableLootEntry from(LootEntry entry) {
      if (entry instanceof LootEntryItem) {
         return new MutableLootEntryItem((LootEntryItem)entry);
      } else if (entry instanceof LootEntryTable) {
         return new MutableLootEntryTable((LootEntryTable)entry);
      } else {
         return (MutableLootEntry)(entry instanceof LootEntryEmpty ? new MutableLootEntryEmpty((LootEntryEmpty)entry) : new GenericMutableLootEmpty(entry));
      }
   }

   MutableLootEntry deepClone();

   LootEntry toImmutable();

   int getWeight();

   void setWeight(int var1);

   int getQuality();

   void setQuality(int var1);

   List<LootCondition> getConditions();

   void setConditions(List<LootCondition> var1);

   void addCondition(LootCondition var1);

   void clearConditions();

   String getName();

   void setName(String var1);
}
