package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.network.msg.MessageConfigRequest;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldJoinHandler {
   public WorldJoinHandler() {
   }

   @SubscribeEvent
   public void onPlayerJoinedServer(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof AbstractClientPlayer) {
         NetworkConfiguration.instance.onWorldJoin();
         Core.getNetworkWrapper().sendToServer(new MessageConfigRequest());
      }

   }
}
