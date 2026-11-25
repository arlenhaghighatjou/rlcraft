package ivorius.reccomplex.network;

import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import ivorius.reccomplex.gui.GuiHider;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketReopenGuiHandler extends SchedulingMessageHandler<PacketReopenGui, IMessage> {
   public PacketReopenGuiHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void processClient(PacketReopenGui message, MessageContext ctx) {
      GuiHider.tryReopenGUI();
   }
}
