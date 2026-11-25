package xzeroair.trinkets.util.compat.enhancedvisuals;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import team.creative.enhancedvisuals.api.event.FireParticlesEvent;
import team.creative.enhancedvisuals.api.event.SelectEndermanEvent;
import team.creative.enhancedvisuals.api.event.SplashEvent;
import team.creative.enhancedvisuals.api.event.VisualExplosionEvent;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.util.TrinketsConfig;

public class EnhancedVisualsRenderEvent {
   public static EnhancedVisualsRenderEvent instance = new EnhancedVisualsRenderEvent();
   Minecraft mc = Minecraft.getMinecraft();

   public EnhancedVisualsRenderEvent() {
   }

   @SubscribeEvent
   public void FireParticlesEvent(FireParticlesEvent event) {
   }

   @SubscribeEvent
   public void EndermenEvent(SelectEndermanEvent event) {
      if (TrinketsConfig.compat.enhancedvisuals && this.mc.player != null && !event.isCanceled() && TrinketHelper.AccessoryCheck(this.mc.player, ModItems.trinkets.TrinketEnderTiara)) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void SplashEvent(SplashEvent event) {
   }

   @SubscribeEvent
   public void VisualExplosionEvent(VisualExplosionEvent event) {
      if (TrinketsConfig.compat.enhancedvisuals && this.mc.player != null && !event.isCanceled() && TrinketHelper.AccessoryCheck(this.mc.player, ModItems.trinkets.TrinketDamageShield)) {
         event.setCanceled(true);
      }

   }
}
