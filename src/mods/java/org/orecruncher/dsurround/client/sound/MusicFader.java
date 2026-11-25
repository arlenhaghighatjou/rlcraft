package org.orecruncher.dsurround.client.sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.math.MathStuff;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "dsurround"
)
public final class MusicFader {
   private static final float MIN_VOLUME_SCALE = 0.001F;
   private static final float FADE_AMOUNT = 0.02F;
   private static float currentScale = 1.0F;
   private static ConfigSoundInstance playingConfigSound;

   public MusicFader() {
   }

   public static float getMusicScaling() {
      return currentScale;
   }

   @SubscribeEvent
   public static void onTick(@Nonnull ClientTickEvent event) {
      if (event.phase != Phase.END) {
         float oldScale = currentScale;
         if (playingConfigSound != null && playingConfigSound.isDonePlaying()) {
            stopConfigSound(playingConfigSound);
         }

         if (playingConfigSound == null) {
            if (EnvironStateHandler.EnvironState.getBattleScanner().inBattle()) {
               currentScale -= 0.04F;
            } else {
               currentScale += 0.02F;
            }
         }

         currentScale = MathStuff.clamp(currentScale, 0.001F, 1.0F);
         if (Float.compare(oldScale, currentScale) != 0) {
            float mcScale = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MUSIC);
            SoundEngine.instance().getSoundManager().setVolume(SoundCategory.MUSIC, mcScale);
         }

      }
   }

   public static void playConfigSound(@Nullable ConfigSoundInstance sound) {
      if (sound != null) {
         SoundEngine.instance().stopAllSounds();
         currentScale = 0.001F;
         playingConfigSound = sound;
         SoundEngine.instance().playSound(sound);
      }

   }

   public static void stopConfigSound(@Nonnull ConfigSoundInstance sound) {
      if (playingConfigSound != null) {
         if (playingConfigSound != sound) {
            ModBase.log().warn("Inconsistent sound in MusicFader", new Object[0]);
         }

         SoundEngine.instance().stopSound(playingConfigSound);
         playingConfigSound = null;
      }

   }
}
