package org.orecruncher.dsurround.client.renderer.weather;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.ReflectedField.IntegerField;

@SideOnly(Side.CLIENT)
public final class RenderWeather extends IRenderHandler {
   private static final IntegerField<EntityRenderer> rendererUpdateCount = new IntegerField(EntityRenderer.class, "rendererUpdateCount", "rendererUpdateCount");
   private final StormRenderer stormRenderer = new StormRenderer();

   public static int getRendererUpdateCount() {
      return rendererUpdateCount.get(Minecraft.getMinecraft().entityRenderer);
   }

   protected RenderWeather() {
   }

   public static boolean addRainParticles(@Nonnull EntityRenderer theThis) {
      World world = EnvironStateHandler.EnvironState.getWorld();
      if (world != null && world.provider.getWeatherRenderer() instanceof RenderWeather) {
         StormSplashRenderer.renderStormSplashes(EnvironStateHandler.EnvironState.getDimensionId(), theThis);
         return true;
      } else {
         return false;
      }
   }

   public void render(float partialTicks, @Nonnull WorldClient world, @Nonnull Minecraft mc) {
      this.stormRenderer.render(mc.entityRenderer, partialTicks);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onWorldLoad(@Nonnull Load e) {
      if (ModOptions.asm.enableWeatherASM && e.getWorld().isRemote) {
         WorldProvider provider = e.getWorld().provider;
         String dimName = provider.getDimensionType().getName();
         IRenderHandler renderer = provider.getWeatherRenderer();
         if (renderer == null) {
            ModBase.log().info("Setting weather renderer for dimension [%s]", new Object[]{dimName});
            provider.setWeatherRenderer(new RenderWeather());
         } else {
            ModBase.log().info("Not hooking weather renderer for dimension [%s] (%s)", new Object[]{dimName, renderer.getClass()});
         }

      }
   }
}
