package ivorius.reccomplex.item;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface GeneratingItem {
   void generateInInventory(WorldServer var1, IItemHandlerModifiable var2, Random var3, ItemStack var4, int var5);
}
