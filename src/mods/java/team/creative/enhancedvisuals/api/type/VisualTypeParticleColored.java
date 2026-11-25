package team.creative.enhancedvisuals.api.type;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.CreativeConfig.IntRange;
import com.creativemd.creativecore.common.config.premade.DecimalMinMax;
import java.awt.Color;

public class VisualTypeParticleColored extends VisualTypeParticle {
   @CreativeConfig
   @IntRange(
      min = 0,
      max = 255
   )
   public int red;
   @CreativeConfig
   @IntRange(
      min = 0,
      max = 255
   )
   public int green;
   @CreativeConfig
   @IntRange(
      min = 0,
      max = 255
   )
   public int blue;
   @CreativeConfig
   @IntRange(
      min = 0,
      max = 255
   )
   public int alpha;
   protected Color cached;

   public VisualTypeParticleColored(String name, int animationSpeed, DecimalMinMax scale, Color defaultColor) {
      super(name, animationSpeed, scale);
      this.red = defaultColor.getRed();
      this.green = defaultColor.getGreen();
      this.blue = defaultColor.getBlue();
      this.alpha = defaultColor.getAlpha();
      this.cached = defaultColor;
   }

   public VisualTypeParticleColored(String name, Color defaultColor) {
      this(name, 0, new DecimalMinMax(0.2D, 1.0D), defaultColor);
   }

   public void configured() {
      this.cached = new Color(this.red, this.green, this.blue, this.alpha);
   }

   public Color getColor() {
      return this.cached;
   }
}
