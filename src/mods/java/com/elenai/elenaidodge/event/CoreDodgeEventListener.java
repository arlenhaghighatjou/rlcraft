package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.api.DodgeEvent;
import com.elenai.elenaidodge.network.PacketHandler;
import com.elenai.elenaidodge.network.message.SDodgeMessage;
import com.elenai.elenaidodge.util.ClientStorage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoreDodgeEventListener {
   public CoreDodgeEventListener() {
   }

   @SubscribeEvent
   public void onClientDodge(DodgeEvent.RequestDodgeEvent event) {
      PacketHandler.instance.sendToServer(new SDodgeMessage(event.getDirection().toString(), ClientStorage.dodges));
      ClientStorage.cooldown = 8;
   }
}
