package org.orecruncher.dsurround.registry.acoustics;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProbabilityWeightsAcoustic implements IAcoustic {
   protected final IAcoustic[] acoustics;
   protected final int[] weights;
   protected final int totalWeight;

   public ProbabilityWeightsAcoustic(@Nonnull List<IAcoustic> acoustics, @Nonnull List<Integer> weights) {
      this.acoustics = (IAcoustic[])acoustics.toArray(new IAcoustic[0]);
      this.weights = new int[weights.size()];
      int tWeight = 0;

      for(int i = 0; i < weights.size(); ++i) {
         this.weights[i] = ((Integer)weights.get(i)).intValue();
         tWeight += this.weights[i];
      }

      this.totalWeight = tWeight;
   }

   @Nonnull
   public String getName() {
      return "Probability Weights Acoustic";
   }

   public void playSound(@Nonnull ISoundPlayer player, @Nonnull Vec3d location, @Nullable EventType event, @Nullable IOptions inputOptions) {
      if (this.totalWeight > 0) {
         int targetWeight = player.getRNG().nextInt(this.totalWeight);

         int i;
         for(i = this.weights.length; (targetWeight -= this.weights[i - 1]) >= 0; --i) {
            ;
         }

         this.acoustics[i - 1].playSound(player, location, event, inputOptions);
      }
   }
}
