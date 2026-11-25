package ivorius.reccomplex.network;

import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import ivorius.reccomplex.gui.loot.GuiEditLootTable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEditLootTableHandler extends SchedulingMessageHandler<PacketEditLootTable, IMessage> {
   public PacketEditLootTableHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void processClient(PacketEditLootTable message, MessageContext ctx) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiEditLootTable(Minecraft.getMinecraft().player, message.getComponent(), message.getKey(), message.getSaveDirectoryData()));
   }
}
