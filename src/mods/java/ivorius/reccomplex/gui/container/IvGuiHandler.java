package ivorius.reccomplex.gui.container;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public interface IvGuiHandler {
   Container getServerGuiElement(int var1, EntityPlayerMP var2, ByteBuf var3);

   Object getClientGuiElement(int var1, EntityPlayer var2, ByteBuf var3);
}
