package org.orecruncher.dsurround.client.sound;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.sound.SoundMetadata;

@SideOnly(Side.CLIENT)
public class ConfigSoundInstance extends SoundInstance {
   public ConfigSoundInstance(@Nonnull String soundResource, float volume) {
      super(new ResourceLocation(soundResource), SoundCategory.MASTER);
      this.volume = volume;
      this.pitch = 1.0F;
      this.xPosF = this.yPosF = this.zPosF = 0.0F;
      this.repeat = false;
      this.repeatDelay = 0;
      this.attenuationType = AttenuationType.NONE;
      SoundMetadata data = RegistryManager.SOUND.getSoundMetadata(this.positionedSoundLocation);
      if (data != null) {
         this.category = data.getCategory();
      }

   }

   public String toString() {
      return MoreObjects.toStringHelper(this).addValue(this.positionedSoundLocation.toString()).add("volume", this.volume).add("pitch", this.pitch).add("attenuation", this.attenuationType).toString();
   }
}
