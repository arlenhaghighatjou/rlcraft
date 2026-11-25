package team.creative.enhancedvisuals.client.sound;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class PositionedSound extends net.minecraft.client.audio.PositionedSound {
   public PositionedSound(ResourceLocation location, SoundCategory category, float volume, float pitch) {
      super(location, category);
      this.volume = volume;
      this.pitch = pitch;
      this.attenuationType = AttenuationType.NONE;
   }

   public PositionedSound(ResourceLocation location, SoundCategory category, float volume, float pitch, BlockPos pos) {
      this(location, category, volume, pitch);
      this.xPosF = (float)pos.getX();
      this.yPosF = (float)pos.getY();
      this.zPosF = (float)pos.getZ();
      this.attenuationType = AttenuationType.LINEAR;
   }
}
