package leviathan143.loottweaker.common.mutable_loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import leviathan143.loottweaker.common.darkmagic.LootEntryItemAccessors;
import leviathan143.loottweaker.common.lib.LootConditions;
import leviathan143.loottweaker.common.lib.LootFunctions;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class MutableLootEntryItem extends AbstractMutableLootEntry {
   private Item item;
   private List<LootFunction> functions;

   MutableLootEntryItem(LootEntryItem entry) {
      super(entry);
      this.item = LootEntryItemAccessors.getItem(entry);
      this.functions = Lists.newArrayList(LootEntryItemAccessors.getFunctions(entry));
   }

   public MutableLootEntryItem(String name, int weight, int quality, List<LootCondition> conditions, Item item, List<LootFunction> functions) {
      super(name, weight, quality, conditions);
      this.item = item;
      this.functions = functions;
   }

   public MutableLootEntryItem deepClone() {
      return new MutableLootEntryItem(this.getName(), this.getWeight(), this.getQuality(), LootConditions.deepClone(this.getConditions()), this.item, LootFunctions.deepClone(this.functions));
   }

   public LootEntryItem toImmutable() {
      return new LootEntryItem(this.item, this.getWeight(), this.getQuality(), (LootFunction[])this.functions.toArray(LootFunctions.NONE), (LootCondition[])this.getConditions().toArray(LootConditions.NONE), this.getName());
   }

   public Item getItem() {
      return this.item;
   }

   public void setItem(Item item) {
      this.item = item;
   }

   public List<LootFunction> getFunctions() {
      return this.functions;
   }

   public void setFunctions(List<LootFunction> functions) {
      this.functions = functions;
   }

   public void addFunction(LootFunction function) {
      this.functions.add(function);
   }

   public void clearFunctions() {
      this.functions.clear();
   }
}
