package noppes.vc.client;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import noppes.vc.Server;
import noppes.vc.VariedCommodities;
import noppes.vc.client.gui.GuiBook;
import noppes.vc.constants.GuiType;
import noppes.vc.constants.PacketClient;
import noppes.vc.shared.gui.GuiBasic;
import noppes.vc.shared.gui.GuiContainerBasic;

public class PacketHandlerClient {
   public PacketHandlerClient() {
   }

   @SubscribeEvent
   public void onPacketData(ClientCustomPacketEvent event) {
      final EntityPlayer player = Minecraft.getMinecraft().player;
      final ByteBuf buffer = event.getPacket().payload();
      Minecraft.getMinecraft().addScheduledTask(new Runnable() {
         public void run() {
            PacketClient en = null;

            try {
               en = PacketClient.values()[buffer.readInt()];
               PacketHandlerClient.this.client(buffer, player, en);
            } catch (Exception var3) {
               VariedCommodities.Log.error("Error with EnumPacketClient." + en, var3);
            }

         }
      });
   }

   private void client(ByteBuf buffer, EntityPlayer player, PacketClient type) throws Exception {
      if (type == PacketClient.GUI) {
         GuiType gui = GuiType.values()[buffer.readInt()];
         VariedCommodities.proxy.openGui(gui, buffer.readInt(), buffer.readInt(), buffer.readInt());
      } else if (type == PacketClient.GUI_DATA) {
         GuiScreen gui = Minecraft.getMinecraft().currentScreen;
         if (gui == null) {
            return;
         }

         if (gui instanceof GuiBasic && ((GuiBasic)gui).hasSubGui()) {
            gui = ((GuiBasic)gui).getSubGui();
         } else if (gui instanceof GuiContainerBasic && ((GuiContainerBasic)gui).hasSubGui()) {
            gui = ((GuiContainerBasic)gui).getSubGui();
         }

         if (gui instanceof GuiBasic.IGuiData) {
            ((GuiBasic.IGuiData)gui).setGuiData(Server.readNBT(buffer));
         }
      } else if (type == PacketClient.OPEN_BOOK) {
         int x = buffer.readInt();
         int y = buffer.readInt();
         int z = buffer.readInt();
         VariedCommodities.proxy.openGui(player, new GuiBook(player, new ItemStack(Server.readNBT(buffer)), x, y, z));
      }

   }
}
