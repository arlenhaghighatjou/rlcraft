package leviathan143.loottweaker.common.mutable_loot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import leviathan143.loottweaker.common.darkmagic.LootPoolAccessors;
import leviathan143.loottweaker.common.lib.LootConditions;
import leviathan143.loottweaker.common.mutable_loot.entry.MutableLootEntry;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class MutableLootPool {
   private static final Logger SANITY_LOGGER = LogManager.getLogger("loottweaker.sanity_checks");
   private String name;
   private Map<String, MutableLootEntry> entries;
   private List<LootCondition> conditions;
   private RandomValueRange rolls;
   private RandomValueRange bonusRolls;

   MutableLootPool(LootPool pool) {
      this.name = pool.getName();
      List<LootEntry> immutableEntries = LootPoolAccessors.getEntries(pool);
      this.entries = new HashMap(immutableEntries.size());
      int uniqueSuffix = 0;

      for(LootEntry entry : immutableEntries) {
         MutableLootEntry mutableEntry = MutableLootEntry.from(entry);
         MutableLootEntry existing = (MutableLootEntry)this.entries.get(mutableEntry.getName());
         if (existing != null) {
            String newName = mutableEntry.getName() + uniqueSuffix++;
            SANITY_LOGGER.error("Unexpected duplicate entry name '{}' in pool '{}'. Duplicate added as '{}'.\nReport this to the loot adder.", mutableEntry.getName(), this.getName(), newName);
            mutableEntry.setName(newName);
            this.entries.put(newName, mutableEntry);
         } else {
            this.entries.put(mutableEntry.getName(), mutableEntry);
         }
      }

      this.conditions = LootPoolAccessors.getConditions(pool);
      this.rolls = pool.getRolls();
      this.bonusRolls = pool.getBonusRolls();
   }

   public MutableLootPool(String name, Map<String, MutableLootEntry> entries, List<LootCondition> conditions, RandomValueRange rolls, RandomValueRange bonusRolls) {
      this.name = name;
      this.entries = entries;
      this.conditions = conditions;
      this.rolls = rolls;
      this.bonusRolls = bonusRolls;
   }

   public MutableLootPool deepClone() {
      BinaryOperator<MutableLootEntry> mergeFunction = (a, b) -> {
         throw new IllegalStateException(String.format("Unexpected duplicate entry '%s' while deep cloning mutable pool '%s'. Report this to the mod author", a.getName(), this.getName()));
      };
      Map<String, MutableLootEntry> entriesDeepClone = (Map)this.entries.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
         return ((MutableLootEntry)e.getValue()).deepClone();
      }, mergeFunction, HashMap::new));
      return new MutableLootPool(this.name, entriesDeepClone, LootConditions.deepClone(this.conditions), this.rolls, this.bonusRolls);
   }

   public LootPool toImmutable() {
      LootEntry[] entriesArray = (LootEntry[])this.entries.values().stream().map(MutableLootEntry::toImmutable).toArray((x$0) -> {
         return new LootEntry[x$0];
      });
      return new LootPool(entriesArray, (LootCondition[])this.conditions.toArray(LootConditions.NONE), this.rolls, this.bonusRolls, this.name);
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

   public void addConditions(List<LootCondition> newConditions) {
      this.conditions.addAll(newConditions);
   }

   public void clearConditions() {
      this.conditions.clear();
   }

   public RandomValueRange getRolls() {
      return this.rolls;
   }

   public void setRolls(RandomValueRange rolls) {
      this.rolls = rolls;
   }

   public RandomValueRange getBonusRolls() {
      return this.bonusRolls;
   }

   public void setBonusRolls(RandomValueRange bonusRolls) {
      this.bonusRolls = bonusRolls;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Map<String, MutableLootEntry> getEntries() {
      return this.entries;
   }

   public MutableLootEntry getEntry(String name) {
      return (MutableLootEntry)this.entries.get(name);
   }

   public void addEntry(MutableLootEntry entry) {
      if (this.entries.putIfAbsent(entry.getName(), entry) != null) {
         throw new IllegalArgumentException(String.format("Duplicate entry name '%s' in pool '%s'", entry.getName(), this.getName()));
      }
   }

   public MutableLootEntry removeEntry(String name) {
      return (MutableLootEntry)this.entries.remove(name);
   }

   public void clearEntries() {
      this.entries.clear();
   }
}
