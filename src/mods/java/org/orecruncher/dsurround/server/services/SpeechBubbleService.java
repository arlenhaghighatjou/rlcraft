package org.orecruncher.dsurround.server.services;

import javax.annotation.Nonnull;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.network.Locus;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.network.PacketSpeechBubble;

public final class SpeechBubbleService extends Service {
   SpeechBubbleService() {
      super("SpeechBubbleService");
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onChatMessageEvent(@Nonnull ServerChatEvent event) {
      Locus point = new Locus(event.getPlayer(), (double)ModOptions.speechbubbles.speechBubbleRange);
      PacketSpeechBubble packet = new PacketSpeechBubble(event.getPlayer(), event.getMessage());
      Network.sendToAllAround(point, packet);
   }
}
