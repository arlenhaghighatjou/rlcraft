package ivorius.reccomplex.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ivorius.reccomplex.json.JsonUtils;
import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryItemCollection extends LootEntry {
   public String itemCollectionID;

   public LootEntryItemCollection(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName, String itemCollectionID) {
      super(weightIn, qualityIn, conditionsIn, entryName);
      this.itemCollectionID = itemCollectionID;
   }

   public static LootEntryItemCollection deserialize(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName, JsonObject json, JsonDeserializationContext context) {
      return new LootEntryItemCollection(weightIn, qualityIn, conditionsIn, entryName, JsonUtils.getString(json, "itemCollectionID", ""));
   }

   public void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
      GenericLootTable.Component component = (GenericLootTable.Component)GenericItemCollectionRegistry.INSTANCE.get(this.itemCollectionID);
      if (component != null) {
         stacks.add(component.getRandomItemStack(rand));
      }

   }

   protected void serialize(JsonObject json, JsonSerializationContext context) {
      json.addProperty("itemCollectionID", this.itemCollectionID);
   }
}
