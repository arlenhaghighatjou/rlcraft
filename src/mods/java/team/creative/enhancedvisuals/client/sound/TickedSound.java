package team.creative.enhancedvisuals.client.sound;

import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class TickedSound extends PositionedSound implements ITickableSound {
   public int tick = 0;
   public DecimalCurve volumeGraph;

   public TickedSound(ResourceLocation location, SoundCategory category, float pitch, DecimalCurve volumeGraph) {
      super(location, category, (float)volumeGraph.valueAt(0.0D), pitch);
      this.volumeGraph = volumeGraph;
      this.repeat = true;
   }

   public TickedSound(ResourceLocation location, SoundCategory category, float pitch, BlockPos pos, DecimalCurve volumeGraph) {
      super(location, category, (float)volumeGraph.valueAt(0.0D), pitch, pos);
      this.volumeGraph = volumeGraph;
      this.repeat = true;
   }

   public boolean isDonePlaying() {
      return this.volume == 0.0F;
   }

   public void update() {
      ++this.tick;
      this.volume = (float)this.volumeGraph.valueAt((double)this.tick);
   }
}
