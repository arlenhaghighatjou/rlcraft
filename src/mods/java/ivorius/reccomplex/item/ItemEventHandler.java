package ivorius.reccomplex.item;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ItemEventHandler {
   void onClientEvent(String var1, ByteBuf var2, EntityPlayer var3, ItemStack var4, int var5);
}
