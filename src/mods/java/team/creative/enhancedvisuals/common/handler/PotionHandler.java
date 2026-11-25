package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.IntMinMax;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import java.awt.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.client.VisualManager;

public class PotionHandler extends VisualHandler {
   @CreativeConfig
   public IntMinMax duration = new IntMinMax(30, 60);
   @CreativeConfig
   public VisualType potion = new VisualTypeOverlay("potion");

   public PotionHandler() {
   }

   public void impact(double distance, ItemStack stack) {
      double modifier = 1.0D - Math.min(5.0D, distance) / 5.0D;
      int var11 = PotionUtils.getPotionColor(PotionUtils.getPotionFromItem(stack));
      float r = (float)(var11 >> 16 & 255) / 255.0F;
      float g = (float)(var11 >> 8 & 255) / 255.0F;
      float b = (float)(var11 & 255) / 255.0F;
      if (modifier > 0.0D) {
         Visual v = VisualManager.addVisualFadeOut(this.potion, this, new DecimalCurve(0.0D, Math.min(1.0D, modifier * 2.0D), (double)this.duration.next(VisualManager.rand), 0.0D));
         v.color = new Color(r, g, b);
      }
   }
}
