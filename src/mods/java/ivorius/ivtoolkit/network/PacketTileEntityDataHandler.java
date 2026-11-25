package ivorius.ivtoolkit.network;

import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketTileEntityDataHandler extends SchedulingMessageHandler<PacketTileEntityData, IMessage> {
   public PacketTileEntityDataHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void processClient(PacketTileEntityData message, MessageContext ctx) {
      World world = IvSideClient.getClientWorld();
      TileEntity entity = world.getTileEntity(message.getPos());
      if (entity != null) {
         ((PartialUpdateHandler)entity).readUpdateData(message.getPayload(), message.getContext());
      }

   }
}
