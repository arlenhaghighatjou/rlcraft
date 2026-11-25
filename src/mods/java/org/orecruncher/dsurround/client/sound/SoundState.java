package org.orecruncher.dsurround.client.sound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SoundState {
   NONE(false, false),
   QUEUED(true, false),
   PLAYING(true, false),
   DELAYED(true, false),
   PAUSED(true, false),
   DONE(false, true),
   ERROR(false, true);

   private final boolean isActive;
   private final boolean isTerminal;

   private SoundState(boolean active, boolean terminal) {
      this.isActive = active;
      this.isTerminal = terminal;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isTerminal() {
      return this.isTerminal;
   }
}
