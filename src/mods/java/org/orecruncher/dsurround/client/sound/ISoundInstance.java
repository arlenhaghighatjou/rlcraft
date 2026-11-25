package org.orecruncher.dsurround.client.sound;

import javax.annotation.Nonnull;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISoundInstance extends ISound {
   SoundState getState();

   void setState(@Nonnull SoundState var1);

   void setQueue(boolean var1);

   boolean getQueue();

   default boolean canSoundBeHeard() {
      return true;
   }
}
