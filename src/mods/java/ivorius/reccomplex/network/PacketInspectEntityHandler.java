package ivorius.reccomplex.network;

import ivorius.ivtoolkit.network.SchedulingMessageHandler;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.nbt.TableDataSourceNBTTagCompound;
import ivorius.reccomplex.gui.table.screen.GuiScreenEditTable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketInspectEntityHandler extends SchedulingMessageHandler<PacketInspectEntity, IMessage> {
   public PacketInspectEntityHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void processClient(final PacketInspectEntity message, MessageContext ctx) {
      GuiScreenEditTable<TableDataSourceNBTTagCompound> screen = new GuiScreenEditTable<TableDataSourceNBTTagCompound>();
      screen.setDataSource(new TableDataSourceNBTTagCompound(screen, screen, message.getData()) {
         @Nonnull
         public String title() {
            return message.getName();
         }
      }, (ds) -> {
         RecurrentComplex.network.sendToServer(new PacketInspectEntity(ds.getNbt(), message.getUuid(), message.getName()));
      });
      Minecraft.getMinecraft().displayGuiScreen(screen);
   }

   public void processServer(PacketInspectEntity message, MessageContext ctx, WorldServer world) {
      NetHandlerPlayServer playServer = ctx.getServerHandler();
      EntityPlayerMP player = playServer.player;
      if (!RecurrentComplex.checkPerms(player)) {
         Entity entity = world.getEntityFromUuid(message.getUuid());
         if (entity != null) {
            entity.readFromNBT(message.getData());
         }

      }
   }
}
