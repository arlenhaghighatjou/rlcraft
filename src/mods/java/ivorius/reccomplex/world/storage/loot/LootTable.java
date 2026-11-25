package ivorius.reccomplex.world.storage.loot;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

public interface LootTable {
   ItemStack getRandomItemStack(WorldServer var1, EaglercraftRandom var2);

   String getDescriptor();
}
