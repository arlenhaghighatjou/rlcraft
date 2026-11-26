package tschipp.callablehorses.client.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.callablehorses.CallableHorses;
import tschipp.callablehorses.client.keybinds.KeybindManager;
import tschipp.callablehorses.common.config.CallableHorsesConfig;
import tschipp.callablehorses.network.PressKeyPacket;

@SideOnly(Side.CLIENT)
public class KeybindEvents {
   private static long lastPressTime = 0L;

   public KeybindEvents() {
   }

   public static void register(EventBus bus) {
      bus.register(PlayerTickEvent.class, KeybindEvents::onPlayerTick);
   }

   @SideOnly(Side.CLIENT)
   public static void onPlayerTick(PlayerTickEvent event) {
      EntityPlayer player = event.player;
      if (player != null && event.side == Side.CLIENT) {
         boolean callHorse = KeybindManager.callHorse.isKeyDown();
         boolean setHorse = KeybindManager.setHorse.isKeyDown();
         boolean showStats = CallableHorsesConfig.settings.enableStatsViewer ? KeybindManager.showStats.isKeyDown() : false;
         if (callHorse && System.currentTimeMillis() - lastPressTime > 500L) {
            lastPressTime = System.currentTimeMillis();
            CallableHorses.network.sendToServer(new PressKeyPacket(0));
         }

         if (setHorse && System.currentTimeMillis() - lastPressTime > 500L) {
            lastPressTime = System.currentTimeMillis();
            CallableHorses.network.sendToServer(new PressKeyPacket(1));
         }

         if (showStats && System.currentTimeMillis() - lastPressTime > 500L) {
            lastPressTime = System.currentTimeMillis();
            CallableHorses.network.sendToServer(new PressKeyPacket(2));
         }
      }

   }
}
