package team.creative.enhancedvisuals.common.addon.toughasnails;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.IntMinMax;
import javax.annotation.Nullable;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeShader;
import team.creative.enhancedvisuals.client.VisualManager;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;

public class ThirstHandler extends VisualHandler {
   @CreativeConfig
   public double defaultIntensity = 0.0D;
   @CreativeConfig
   public double maxIntensity = 5.0D;
   @CreativeConfig
   public double fadeFactor = 0.05000000074505806D;
   @CreativeConfig
   public IntMinMax thirstLevel = new IntMinMax(2, 8);
   @CreativeConfig
   public VisualType focus = new VisualTypeShader("focus", new ResourceLocation("shaders/post/blobs2.json")) {
      public void changeProperties(float intensity) {
         for(Shader mcShader : this.shaderGroup.getShaders()) {
            ShaderUniform shaderuniform = mcShader.getShaderManager().getShaderUniform("Radius");
            if (shaderuniform != null) {
               shaderuniform.set(intensity);
            }
         }

      }
   };
   public Visual focusVisual;

   public ThirstHandler() {
   }

   public void tick(@Nullable EntityPlayer player) {
      if (this.focusVisual == null) {
         this.focusVisual = new Visual(this.focus, this, 0);
         VisualManager.add(this.focusVisual);
      }

      double aimedSaturation = this.defaultIntensity;
      if (player != null && ((IThirst)player.getCapability(TANCapabilities.THIRST, (EnumFacing)null)).getThirst() <= this.thirstLevel.max) {
         double leftFoodInSpan = (double)(((IThirst)player.getCapability(TANCapabilities.THIRST, (EnumFacing)null)).getThirst() - this.thirstLevel.min);
         double spanLength = (double)this.thirstLevel.spanLength();
         aimedSaturation = (1.0D - leftFoodInSpan / spanLength) * this.maxIntensity;
      }

      if ((double)this.focusVisual.getOpacityInternal() < aimedSaturation) {
         this.focusVisual.setOpacityInternal((float)Math.min((double)this.focusVisual.getOpacityInternal() + this.fadeFactor, aimedSaturation));
      } else if ((double)this.focusVisual.getOpacityInternal() > aimedSaturation) {
         this.focusVisual.setOpacityInternal((float)Math.max((double)this.focusVisual.getOpacityInternal() - this.fadeFactor, aimedSaturation));
      }

   }
}
