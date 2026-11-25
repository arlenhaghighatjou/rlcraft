package org.orecruncher.lib.compat;

import javax.annotation.Nonnull;
import net.minecraft.client.audio.PositionedSound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedField;

@SideOnly(Side.CLIENT)
public class PositionedSoundUtil {
   private static final ReflectedField.FloatField<PositionedSound> getVolume = new ReflectedField.FloatField<PositionedSound>(PositionedSound.class, "volume", "volume");
   private static final ReflectedField.FloatField<PositionedSound> getPitch = new ReflectedField.FloatField<PositionedSound>(PositionedSound.class, "pitch", "pitch");

   public PositionedSoundUtil() {
   }

   public static float getVolume(@Nonnull PositionedSound sound) {
      return getVolume.get(sound);
   }

   public static float getPitch(@Nonnull PositionedSound sound) {
      return getPitch.get(sound);
   }
}
