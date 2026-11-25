package team.creative.enhancedvisuals.common.addon.toughasnails;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.client.VisualManager;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureScale.TemperatureRange;

public class TemperatureHandler extends VisualHandler {
   @CreativeConfig
   public double defaultIntensity = 0.0D;
   @CreativeConfig
   public double mediumIntensity = 0.2D;
   @CreativeConfig
   public double maxIntensity = 0.4D;
   @CreativeConfig
   public double fadeFactor = 0.005D;
   @CreativeConfig
   public VisualType freeze = new VisualTypeOverlay("freeze");
   public Visual freezeVisual;
   @CreativeConfig
   public VisualType heat = new VisualTypeOverlay("heat");
   public Visual heatVisual;
   private static Temperature defaultTemperature = new Temperature(12);

   public TemperatureHandler() {
   }

   public void tick(@Nullable EntityPlayer player) {
      if (this.freezeVisual == null) {
         this.freezeVisual = new Visual(this.freeze, this, 0);
         this.freezeVisual.setOpacityInternal(0.0F);
         VisualManager.add(this.freezeVisual);
         this.heatVisual = new Visual(this.heat, this, 0);
         this.heatVisual.setOpacityInternal(0.0F);
         VisualManager.add(this.heatVisual);
      }

      double aimedHeat = this.defaultIntensity;
      double aimedFreeze = this.defaultIntensity;
      Temperature temp = null;
      if (player != null) {
         temp = ((ITemperature)player.getCapability(TANCapabilities.TEMPERATURE, (EnumFacing)null)).getTemperature();
      }

      if (temp == null) {
         temp = defaultTemperature;
      }

      TemperatureRange range = temp.getRange();
      switch(range) {
      case ICY:
         aimedHeat = 0.0D;
         aimedFreeze = this.maxIntensity;
         break;
      case COOL:
         aimedHeat = 0.0D;
         aimedFreeze = this.mediumIntensity * (double)temp.getRangeDelta(true);
         break;
      case MILD:
         aimedHeat = this.defaultIntensity;
         aimedFreeze = this.defaultIntensity;
         break;
      case WARM:
         aimedHeat = this.mediumIntensity * (double)temp.getRangeDelta(false);
         aimedFreeze = 0.0D;
         break;
      case HOT:
         aimedHeat = this.maxIntensity;
         aimedFreeze = 0.0D;
      }

      if ((double)this.freezeVisual.getOpacityInternal() < aimedFreeze) {
         this.freezeVisual.setOpacityInternal((float)Math.min((double)this.freezeVisual.getOpacityInternal() + this.fadeFactor, aimedFreeze));
      } else if ((double)this.freezeVisual.getOpacityInternal() > aimedFreeze) {
         this.freezeVisual.setOpacityInternal((float)Math.max((double)this.freezeVisual.getOpacityInternal() - this.fadeFactor, aimedFreeze));
      }

      if ((double)this.heatVisual.getOpacityInternal() < aimedHeat) {
         this.heatVisual.setOpacityInternal((float)Math.min((double)this.heatVisual.getOpacityInternal() + this.fadeFactor, aimedHeat));
      } else if ((double)this.heatVisual.getOpacityInternal() > aimedHeat) {
         this.heatVisual.setOpacityInternal((float)Math.max((double)this.heatVisual.getOpacityInternal() - this.fadeFactor, aimedHeat));
      }

   }
}
