package org.orecruncher.dsurround.client.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.ConfigSoundInstance;
import org.orecruncher.dsurround.client.sound.MusicFader;
import org.orecruncher.dsurround.client.sound.SoundEngine;

@SideOnly(Side.CLIENT)
public class PlaySoundButton extends GuiButtonExt {
   private final String soundResource;
   private ConfigSoundInstance playingSound;

   public PlaySoundButton(int id, @Nonnull String sound) {
      super(id, 0, 0, 68, 18, GuiConstants.TEXT_PLAY);
      this.soundResource = sound;
   }

   public String getSoundResource() {
      return this.soundResource;
   }

   protected void updateDisplayText() {
      this.displayString = this.playingSound != null ? GuiConstants.TEXT_STOP : GuiConstants.TEXT_PLAY;
   }

   public void drawButton(@Nonnull Minecraft mc, int x, int y, float partial) {
      if (this.playingSound != null && !SoundEngine.instance().isSoundPlaying(this.playingSound)) {
         this.playingSound = null;
         this.updateDisplayText();
      }

      super.drawButton(mc, x, y, partial);
   }

   private void doPlay(@Nonnull ConfigSoundInstance sound) {
      this.playingSound = sound;
      MusicFader.playConfigSound(sound);
   }

   public void playSound(@Nullable Minecraft mc, float volume) {
      if (this.playingSound != null) {
         MusicFader.stopConfigSound(this.playingSound);
         this.playingSound = null;
      } else {
         this.doPlay(new ConfigSoundInstance(this.soundResource, volume));
      }

      this.updateDisplayText();
   }

   public void stopSound() {
      if (this.playingSound != null) {
         this.playSound((Minecraft)null, 0.0F);
      }

   }
}
