package ivorius.reccomplex.world.storage.loot;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

public interface LootTable {
   ItemStack getRandomItemStack(WorldServer var1, Random var2);

   String getDescriptor();
}
