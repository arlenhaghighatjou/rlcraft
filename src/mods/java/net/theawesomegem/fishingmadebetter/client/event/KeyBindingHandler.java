package net.theawesomegem.fishingmadebetter.client.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.client.KeybindManager;
import net.theawesomegem.fishingmadebetter.common.networking.PrimaryPacketHandler;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketKeybindS;

public class KeyBindingHandler {
   private boolean lastKeyPress;

   public KeyBindingHandler() {
   }

   @SideOnly(Side.CLIENT)
   public void register(EventBus bus) {
      bus.register(ClientTickEvent.class, this::onTick);
   }

   public void onTick(ClientTickEvent event) {
      if (!event.phase.equals(Phase.START)) {
         if (KeybindManager.reelIn.isKeyDown()) {
            this.lastKeyPress = true;
            PrimaryPacketHandler.INSTANCE.sendToServer(new PacketKeybindS(PacketKeybindS.Keybind.REEL_IN));
         } else if (KeybindManager.reelOut.isKeyDown()) {
            this.lastKeyPress = true;
            PrimaryPacketHandler.INSTANCE.sendToServer(new PacketKeybindS(PacketKeybindS.Keybind.REEL_OUT));
         } else {
            if (this.lastKeyPress) {
               PrimaryPacketHandler.INSTANCE.sendToServer(new PacketKeybindS(PacketKeybindS.Keybind.NONE));
            }

            this.lastKeyPress = false;
         }

      }
   }
}
