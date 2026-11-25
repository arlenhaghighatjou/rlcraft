package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.DecimalMinMax;
import com.creativemd.creativecore.common.config.premade.IntMinMax;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.event.SplashEvent;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeBlur;
import team.creative.enhancedvisuals.client.VisualManager;
import team.creative.enhancedvisuals.common.event.EVEvents;

public class SplashHandler extends VisualHandler {
   @CreativeConfig
   public IntMinMax duration = new IntMinMax(10, 10);
   @CreativeConfig
   public DecimalMinMax intensity = new DecimalMinMax(5.0D, 10.0D);
   @CreativeConfig
   public VisualType blur = new VisualTypeBlur("blur");
   public boolean wasInWater = false;
   public EaglercraftRandom rand = new EaglercraftRandom();

   public SplashHandler() {
   }

   public void tick(@Nullable EntityPlayer player) {
      if (player != null) {
         boolean isInWater = EVEvents.areEyesInWater(player);
         if (isInWater != this.wasInWater) {
            SplashEvent event = new SplashEvent(player);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
               VisualManager.addVisualFadeOut(this.blur, this, new DecimalCurve(this.rand, this.duration, this.intensity));
            }
         }

         this.wasInWater = isInWater;
      }

   }
}
