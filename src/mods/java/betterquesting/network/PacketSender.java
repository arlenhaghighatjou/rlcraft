package betterquesting.network;

import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api2.utils.BQThreadedIO;
import betterquesting.core.BetterQuesting;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class PacketSender implements IPacketSender {
   public static final PacketSender INSTANCE = new PacketSender();

   public PacketSender() {
   }

   public void sendToPlayers(QuestingPacket payload, EntityPlayerMP... players) {
      payload.getPayload().setString("ID", payload.getHandler().toString());
      BQThreadedIO.INSTANCE.enqueue(() -> {
         List<NBTTagCompound> fragments = PacketAssembly.INSTANCE.splitPacket(payload.getPayload());

         for(EntityPlayerMP p : players) {
            for(NBTTagCompound tag : fragments) {
               BetterQuesting.instance.network.sendTo(new PacketQuesting(tag), p);
            }
         }

      });
   }

   public void sendToAll(QuestingPacket payload) {
      payload.getPayload().setString("ID", payload.getHandler().toString());
      BQThreadedIO.INSTANCE.enqueue(() -> {
         for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload())) {
            BetterQuesting.instance.network.sendToAll(new PacketQuesting(p));
         }

      });
   }

   public void sendToServer(QuestingPacket payload) {
      payload.getPayload().setString("ID", payload.getHandler().toString());
      BQThreadedIO.INSTANCE.enqueue(() -> {
         for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload())) {
            BetterQuesting.instance.network.sendToServer(new PacketQuesting(p));
         }

      });
   }

   public void sendToAround(QuestingPacket payload, TargetPoint point) {
      payload.getPayload().setString("ID", payload.getHandler().toString());
      BQThreadedIO.INSTANCE.enqueue(() -> {
         for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload())) {
            BetterQuesting.instance.network.sendToAllAround(new PacketQuesting(p), point);
         }

      });
   }

   public void sendToDimension(QuestingPacket payload, int dimension) {
      payload.getPayload().setString("ID", payload.getHandler().toString());
      BQThreadedIO.INSTANCE.enqueue(() -> {
         for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload())) {
            BetterQuesting.instance.network.sendToDimension(new PacketQuesting(p), dimension);
         }

      });
   }
}
