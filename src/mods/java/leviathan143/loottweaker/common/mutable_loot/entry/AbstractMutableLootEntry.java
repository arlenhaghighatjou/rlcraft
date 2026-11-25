package leviathan143.loottweaker.common.mutable_loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import leviathan143.loottweaker.common.darkmagic.LootEntryAccessors;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public abstract class AbstractMutableLootEntry implements MutableLootEntry {
   private String name;
   private int weight;
   private int quality;
   private List<LootCondition> conditions;

   protected AbstractMutableLootEntry(LootEntry entry) {
      this.name = entry.getEntryName();
      this.weight = LootEntryAccessors.getWeight(entry);
      this.quality = LootEntryAccessors.getQuality(entry);
      this.conditions = Lists.newArrayList(LootEntryAccessors.getConditions(entry));
   }

   protected AbstractMutableLootEntry(String name, int weight, int quality, LootCondition[] conditions) {
      this(name, weight, quality, Lists.newArrayList(conditions));
   }

   protected AbstractMutableLootEntry(String name, int weight, int quality, List<LootCondition> conditions) {
      this.name = name;
      this.weight = weight;
      this.quality = quality;
      this.conditions = conditions;
   }

   public abstract MutableLootEntry deepClone();

   public abstract LootEntry toImmutable();

   public int getWeight() {
      return this.weight;
   }

   public void setWeight(int weight) {
      this.weight = weight;
   }

   public int getQuality() {
      return this.quality;
   }

   public void setQuality(int quality) {
      this.quality = quality;
   }

   public List<LootCondition> getConditions() {
      return this.conditions;
   }

   public void setConditions(List<LootCondition> conditions) {
      this.conditions = conditions;
   }

   public void addCondition(LootCondition condition) {
      this.conditions.add(condition);
   }

   public void clearConditions() {
      this.conditions.clear();
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
