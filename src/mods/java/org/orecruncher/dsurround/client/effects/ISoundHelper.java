package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.ISoundInstance;

@SideOnly(Side.CLIENT)
public interface ISoundHelper {
   boolean playSound(@Nonnull ISoundInstance var1);

   void stopSound(@Nonnull ISoundInstance var1);
}
