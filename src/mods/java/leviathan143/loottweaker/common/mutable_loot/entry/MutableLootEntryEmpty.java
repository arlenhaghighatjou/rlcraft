package leviathan143.loottweaker.common.mutable_loot.entry;

import java.util.List;
import leviathan143.loottweaker.common.lib.LootConditions;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class MutableLootEntryEmpty extends AbstractMutableLootEntry {
   MutableLootEntryEmpty(LootEntryEmpty entry) {
      super(entry);
   }

   public MutableLootEntryEmpty(String name, int weight, int quality, LootCondition[] conditions) {
      super(name, weight, quality, conditions);
   }

   public MutableLootEntryEmpty(String name, int weight, int quality, List<LootCondition> conditions) {
      super(name, weight, quality, conditions);
   }

   public MutableLootEntryEmpty deepClone() {
      return new MutableLootEntryEmpty(this.getName(), this.getWeight(), this.getQuality(), LootConditions.deepClone(this.getConditions()));
   }

   public LootEntryEmpty toImmutable() {
      return new LootEntryEmpty(this.getWeight(), this.getQuality(), (LootCondition[])this.getConditions().toArray(LootConditions.NONE), this.getName());
   }
}
