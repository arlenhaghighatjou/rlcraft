package ivorius.reccomplex.item;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface GeneratingItem {
   void generateInInventory(WorldServer var1, IItemHandlerModifiable var2, EaglercraftRandom var3, ItemStack var4, int var5);
}
