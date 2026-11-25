package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeSaturation;
import team.creative.enhancedvisuals.client.VisualManager;

public class SaturationHandler extends VisualHandler {
   @CreativeConfig
   public double fadeFactor = 5.0E-4D;
   @CreativeConfig
   public DecimalCurve saturation = new DecimalCurve(2.0D, 0.0D, 8.0D, 1.0D);
   @CreativeConfig
   public VisualType desaturate = new VisualTypeSaturation("desaturate");
   public Visual saturationVisual;

   public SaturationHandler() {
   }

   public void tick(@Nullable EntityPlayer player) {
      if (this.saturationVisual == null) {
         this.saturationVisual = new Visual(this.desaturate, this, 0);
         this.saturationVisual.setOpacityInternal(1.0F);
         VisualManager.add(this.saturationVisual);
      }

      double aimedSaturation = this.saturation.maxValue;
      if (player != null && !player.isDead) {
         aimedSaturation = this.saturation.valueAt((double)player.getFoodStats().getFoodLevel());
         if ((double)this.saturationVisual.getOpacityInternal() < aimedSaturation) {
            this.saturationVisual.setOpacityInternal((float)Math.min((double)this.saturationVisual.getOpacityInternal() + this.fadeFactor, aimedSaturation));
         } else if ((double)this.saturationVisual.getOpacityInternal() > aimedSaturation) {
            this.saturationVisual.setOpacityInternal((float)Math.max((double)this.saturationVisual.getOpacityInternal() - this.fadeFactor, aimedSaturation));
         }
      } else {
         this.saturationVisual.setOpacityInternal((float)this.saturation.maxValue);
      }

   }
}
