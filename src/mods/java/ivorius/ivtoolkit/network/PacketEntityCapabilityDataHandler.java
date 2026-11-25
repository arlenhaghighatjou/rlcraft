package ivorius.ivtoolkit.network;

import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEntityCapabilityDataHandler extends SchedulingMessageHandler<PacketEntityCapabilityData, IMessage> {
   public PacketEntityCapabilityDataHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void processClient(PacketEntityCapabilityData message, MessageContext ctx) {
      this.processClientC(message);
   }

   private <T> void processClientC(PacketEntityCapabilityData message) {
      World world = IvSideClient.getClientWorld();
      Entity entity = world.getEntityByID(message.getEntityID());
      if (entity != null) {
         Capability<T> capability = CapabilityUpdateRegistry.INSTANCE.capability(message.getCapabilityKey());
         T t = (T)entity.getCapability(capability, message.getDirection());
         if (t instanceof PartialUpdateHandler) {
            ((PartialUpdateHandler)t).readUpdateData(message.getPayload(), message.getContext());
         }
      }

   }
}
