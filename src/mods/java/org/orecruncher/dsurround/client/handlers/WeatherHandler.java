package org.orecruncher.dsurround.client.handlers;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.sound.SoundInstance;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.event.ThunderEvent;

@SideOnly(Side.CLIENT)
public class WeatherHandler extends EffectHandlerBase {
   private int timer = 0;

   public WeatherHandler() {
      super("Weather");
   }

   public boolean doTick(int tick) {
      return this.timer > 0;
   }

   public void onConnect() {
      this.timer = 0;
   }

   public void process(@Nonnull EntityPlayer player) {
      --this.timer;
      player.getEntityWorld().setLastLightningBolt(2);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onThunderEvent(@Nonnull ThunderEvent event) {
      if (ModOptions.rain.allowBackgroundThunder && EnvironStateHandler.EnvironState.getDimensionId() == event.dimId) {
         SoundInstance thunder = Sounds.THUNDER.createSoundAt(event.location).setVolume(ModOptions.sound.thunderVolume);
         SoundEffectHandler.INSTANCE.playSound(thunder);
         if (event.doFlash) {
            this.timer = 2;
         }

      }
   }

   @SubscribeEvent
   public void diagnostic(@Nonnull DiagnosticEvent.Gather event) {
      event.output.add(Weather.diagnostic());
   }
}
