package ivorius.reccomplex.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ItemInputHandler {
   boolean onMouseInput(EntityPlayer var1, ItemStack var2, int var3, boolean var4, int var5);
}
