package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeBlur;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.client.VisualManager;

public class HeartbeatHandler extends VisualHandler {
   @CreativeConfig
   public boolean useHealthPercentage = false;
   @CreativeConfig
   public int maxHealth = 6;
   @CreativeConfig
   public float maxHealthPercentage = 0.3F;
   @CreativeConfig
   public float heartbeatIntensity = 50.0F;
   @CreativeConfig
   public int heartbeatDuration = 5;
   @CreativeConfig
   public int minHeartbeatLength = 15;
   @CreativeConfig
   public float heartbeatTimeFactor = 100.0F;
   @CreativeConfig
   public float heartbeatVolume = 1.0F;
   @CreativeConfig
   public VisualType lowhealth = new VisualTypeOverlay("lowhealth");
   @CreativeConfig
   public VisualType blur = new VisualTypeBlur("blur");
   public int effectBufferTicks;

   public HeartbeatHandler() {
   }

   public void tick(@Nullable EntityPlayer player) {
      if (this.shouldHeartbeatTrigger(player)) {
         if (this.effectBufferTicks <= 0) {
            float intensity = this.getIntensity(player);
            this.resetBufferTicks(player);
            VisualManager.addVisualFadeOut(this.lowhealth, this, new DecimalCurve(0.0D, (double)Math.min(0.7F, intensity), (double)this.effectBufferTicks, 0.0D));
            VisualManager.addVisualFadeOut(this.blur, this, new DecimalCurve(0.0D, (double)(Math.min(0.7F, intensity) * this.heartbeatIntensity), (double)this.heartbeatDuration, 0.0D));
            this.playSound(new ResourceLocation("enhancedvisuals", "heartbeatout"), this.heartbeatVolume);
         } else if (this.effectBufferTicks == 5) {
            float intensity = this.getIntensity(player);
            this.playSound(new ResourceLocation("enhancedvisuals", "heartbeatin"), this.heartbeatVolume);
            VisualManager.addVisualFadeOut(this.blur, this, new DecimalCurve(0.0D, (double)(Math.min(0.7F, intensity) * this.heartbeatIntensity), (double)this.heartbeatDuration, 0.0D));
         }
      }

      --this.effectBufferTicks;
   }

   private void resetBufferTicks(@Nonnull EntityPlayer player) {
      float percentHealthLeft = player.getHealth() / player.getMaxHealth();
      this.effectBufferTicks = (int)(percentHealthLeft * this.heartbeatTimeFactor + (float)this.minHeartbeatLength);
   }

   private float getIntensity(@Nonnull EntityPlayer player) {
      float percentHealthLeft = player.getHealth() / player.getMaxHealth();
      return this.useHealthPercentage ? (this.maxHealthPercentage - percentHealthLeft) * 2.0F : ((float)this.maxHealth - player.getHealth()) / player.getMaxHealth() * 2.0F;
   }

   private boolean shouldHeartbeatTrigger(@Nullable EntityPlayer player) {
      if (player != null && !Minecraft.getMinecraft().isGamePaused() && !player.isDead) {
         if (this.useHealthPercentage) {
            float percentageHealth = player.getHealth() / player.getMaxHealth();
            return percentageHealth < this.maxHealthPercentage;
         } else {
            return player.getHealth() < (float)this.maxHealth;
         }
      } else {
         return false;
      }
   }
}
