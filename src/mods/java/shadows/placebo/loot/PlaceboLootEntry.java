package shadows.placebo.loot;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.registries.IForgeRegistryEntry;
import shadows.placebo.util.RecipeHelper;

public class PlaceboLootEntry extends LootEntry {
   private static int k = 0;
   private final ItemStack stack;
   private final int min;
   private final int max;

   public PlaceboLootEntry(ItemStack stack, int min, int max, int weight, int quality) {
      super(weight, quality, new LootCondition[0], "placebo_loot_entry_" + k++);
      this.stack = stack;
      this.min = min;
      this.max = max;
   }

   public PlaceboLootEntry(IForgeRegistryEntry<?> thing, int min, int max, int weight, int quality) {
      this(RecipeHelper.makeStack(thing), min, max, weight, quality);
   }

   public void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
      ItemStack s = this.stack.copy();
      s.setCount(MathHelper.getInt(rand, this.min, this.max));
      stacks.add(s);
   }

   protected void serialize(JsonObject json, JsonSerializationContext context) {
   }
}
