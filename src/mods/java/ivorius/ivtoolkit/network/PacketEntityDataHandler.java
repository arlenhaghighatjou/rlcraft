package ivorius.ivtoolkit.network;

import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEntityDataHandler extends SchedulingMessageHandler<PacketEntityData, IMessage> {
   public PacketEntityDataHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void processClient(PacketEntityData message, MessageContext ctx) {
      World world = IvSideClient.getClientWorld();
      Entity entity = world.getEntityByID(message.getEntityID());
      if (entity != null) {
         ((PartialUpdateHandler)entity).readUpdateData(message.getPayload(), message.getContext());
      }

   }
}
