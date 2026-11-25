package leviathan143.loottweaker.common.mutable_loot.entry;

import java.util.List;
import leviathan143.loottweaker.common.darkmagic.LootEntryTableAccessors;
import leviathan143.loottweaker.common.lib.LootConditions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class MutableLootEntryTable extends AbstractMutableLootEntry {
   private ResourceLocation delegateTableId;

   MutableLootEntryTable(LootEntryTable entry) {
      super(entry);
      this.delegateTableId = LootEntryTableAccessors.getTable(entry);
   }

   public MutableLootEntryTable(String name, int weight, int quality, LootCondition[] conditions, ResourceLocation delegateTableId) {
      super(name, weight, quality, conditions);
      this.delegateTableId = delegateTableId;
   }

   public MutableLootEntryTable(String name, int weight, int quality, List<LootCondition> conditions, ResourceLocation delegateTableId) {
      super(name, weight, quality, conditions);
      this.delegateTableId = delegateTableId;
   }

   public MutableLootEntryTable deepClone() {
      return new MutableLootEntryTable(this.getName(), this.getWeight(), this.getQuality(), LootConditions.deepClone(this.getConditions()), this.delegateTableId);
   }

   public LootEntryTable toImmutable() {
      return new LootEntryTable(this.delegateTableId, this.getWeight(), this.getQuality(), (LootCondition[])this.getConditions().toArray(LootConditions.NONE), this.getName());
   }

   public ResourceLocation getDelegateTableId() {
      return this.delegateTableId;
   }

   public void setDelegateTableId(ResourceLocation delegateTableId) {
      this.delegateTableId = delegateTableId;
   }
}
