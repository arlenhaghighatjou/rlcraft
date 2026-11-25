package team.creative.enhancedvisuals.common.addon.simpledifficulty;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.creativemd.creativecore.common.config.api.CreativeConfig;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.client.VisualManager;

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
      int temp = 12;
      if (player != null && !player.isDead) {
         temp = ((ITemperatureCapability)player.getCapability(SDCapabilities.TEMPERATURE, (EnumFacing)null)).getTemperatureLevel();
      }

      TemperatureEnum enumRange = TemperatureUtil.getTemperatureEnum(temp);
      switch(enumRange) {
      case FREEZING:
         aimedHeat = 0.0D;
         aimedFreeze = this.maxIntensity;
         break;
      case COLD:
         aimedHeat = 0.0D;
         aimedFreeze = this.mediumIntensity * (double)getRangeModifier(temp, enumRange, true);
         break;
      case NORMAL:
         aimedHeat = this.defaultIntensity;
         aimedFreeze = this.defaultIntensity;
         break;
      case HOT:
         aimedHeat = this.mediumIntensity * (double)getRangeModifier(temp, enumRange, false);
         aimedFreeze = 0.0D;
         break;
      case BURNING:
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

   private static float getRangeModifier(int temp, TemperatureEnum enumRange, boolean cold) {
      int lowerBound = enumRange.getLowerBound();
      int upperBound = enumRange.getUpperBound();
      int range = upperBound - lowerBound;
      if (cold) {
         if (temp == lowerBound) {
            return 1.0F;
         } else if (temp == upperBound) {
            return 0.0F;
         } else {
            float returnable = (float)(1 - (temp - lowerBound) / range);
            return returnable;
         }
      } else if (temp == lowerBound) {
         return 0.0F;
      } else if (temp == upperBound) {
         return 1.0F;
      } else {
         float returnable = (float)((temp - lowerBound) / range);
         return returnable;
      }
   }
}
