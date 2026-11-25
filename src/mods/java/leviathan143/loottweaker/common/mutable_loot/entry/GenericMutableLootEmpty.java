package leviathan143.loottweaker.common.mutable_loot.entry;

import java.util.Arrays;
import java.util.List;
import leviathan143.loottweaker.common.darkmagic.LootEntryAccessors;
import leviathan143.loottweaker.common.lib.LootConditions;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class GenericMutableLootEmpty implements MutableLootEntry {
   private final LootEntry entry;

   GenericMutableLootEmpty(LootEntry entry) {
      this.entry = entry;
   }

   public MutableLootEntry deepClone() {
      throw new UnsupportedOperationException(String.format("Could not deep copy entry '%s' as it is a non-vanilla loot entry type (%s)", this.getName(), this.entry.getClass().getName()));
   }

   public LootEntry toImmutable() {
      return this.entry;
   }

   public int getWeight() {
      return LootEntryAccessors.getWeight(this.entry);
   }

   public void setWeight(int weight) {
      LootEntryAccessors.setWeight(this.entry, weight);
   }

   public int getQuality() {
      return LootEntryAccessors.getQuality(this.entry);
   }

   public void setQuality(int quality) {
      LootEntryAccessors.setQuality(this.entry, quality);
   }

   public List<LootCondition> getConditions() {
      return Arrays.asList(LootEntryAccessors.getConditions(this.entry));
   }

   public void setConditions(List<LootCondition> conditions) {
      LootEntryAccessors.setConditions(this.entry, (LootCondition[])conditions.toArray(LootConditions.NONE));
   }

   public void addCondition(LootCondition condition) {
      LootEntryAccessors.setConditions(this.entry, (LootCondition[])ArrayUtils.add(LootEntryAccessors.getConditions(this.entry), condition));
   }

   public void clearConditions() {
      LootEntryAccessors.setConditions(this.entry, new LootCondition[0]);
   }

   public String getName() {
      return this.entry.getEntryName();
   }

   public void setName(String name) {
      LootEntryAccessors.setName(this.entry, name);
   }
}
