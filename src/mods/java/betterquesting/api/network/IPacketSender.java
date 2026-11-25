package betterquesting.api.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public interface IPacketSender {
   void sendToPlayers(QuestingPacket var1, EntityPlayerMP... var2);

   void sendToAll(QuestingPacket var1);

   void sendToServer(QuestingPacket var1);

   void sendToAround(QuestingPacket var1, TargetPoint var2);

   void sendToDimension(QuestingPacket var1, int var2);
}
