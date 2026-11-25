package ivorius.reccomplex.world.storage.loot;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext.Builder;

public class VanillaLootTable implements LootTable {
   public ResourceLocation vanillaKey;

   public VanillaLootTable(ResourceLocation lootTableKey) {
      this.vanillaKey = lootTableKey;
   }

   public ItemStack getRandomItemStack(WorldServer server, EaglercraftRandom random) {
      List<ItemStack> loot = null;

      try {
         net.minecraft.world.storage.loot.LootTable loottable = server.getLootTableManager().getLootTableFromLocation(this.vanillaKey);
         loot = loottable.generateLootForPools(random, (new Builder(server)).build());
         return loot.size() > 0 ? (ItemStack)loot.get(0) : null;
      } catch (Exception var5) {
         RecurrentComplex.logger.error("Error generating vanilla loot", var5);
         return ItemStack.EMPTY;
      }
   }

   public String getDescriptor() {
      return IvTranslations.get("inventoryGen.vanilla");
   }
}
