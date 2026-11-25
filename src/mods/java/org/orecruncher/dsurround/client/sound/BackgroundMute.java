package org.orecruncher.dsurround.client.sound;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;

@SideOnly(Side.CLIENT)
public class BackgroundMute {
   public BackgroundMute() {
   }

   @SubscribeEvent
   public static void clientTick(ClientTickEvent event) {
      if (ModOptions.sound.muteWhenBackground && Display.isCreated()) {
         boolean active = Display.isActive();
         boolean muted = SoundEngine.instance().isMuted();
         if (active && muted) {
            SoundEngine.instance().setMuted(false);
            ModBase.log().debug("Unmuting sounds", new Object[0]);
         } else if (!active && !muted) {
            SoundEngine.instance().setMuted(true);
            ModBase.log().debug("Muting sounds", new Object[0]);
         }
      }

   }
}
