package team.creative.enhancedvisuals.api.type;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.DecimalMinMax;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import team.creative.enhancedvisuals.api.VisualCategory;

public class VisualTypeParticle extends VisualTypeTexture {
   @CreativeConfig
   public DecimalMinMax scale;

   public VisualTypeParticle(String name, int animationSpeed, DecimalMinMax scale) {
      super(VisualCategory.particle, name, animationSpeed);
      this.scale = scale;
   }

   public VisualTypeParticle(String name) {
      this(name, 0, new DecimalMinMax(0.2D, 1.0D));
   }

   public boolean scaleVariants() {
      return true;
   }

   public double randomScale(EaglercraftRandom rand) {
      return this.scale.next(rand);
   }
}
