package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.addon.Addons;
import goblinbob.mobends.core.data.EntityDatabase;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class DataUpdateHandler {
   public static float partialTicks = 0.0F;
   protected static float ticks = 0.0F;
   public static float ticksPerFrame = 0.0F;

   public DataUpdateHandler() {
   }

   public static float getTicks() {
      return ticks;
   }

   @SubscribeEvent
   public void updateAnimations(RenderTickEvent event) {
      if (event.phase != Phase.END) {
         if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
            if (!Minecraft.getMinecraft().isGamePaused()) {
               partialTicks = event.renderTickTime;
            }

            float newTicks = (float)Minecraft.getMinecraft().player.ticksExisted + event.renderTickTime;
            if (ticks > newTicks) {
               onTicksRestart();
            }

            if (Minecraft.getMinecraft().world.isRemote && Minecraft.getMinecraft().isGamePaused()) {
               ticksPerFrame = 0.0F;
            } else {
               ticksPerFrame = Math.min(Math.max(0.0F, newTicks - ticks), 1.0F);
               ticks = newTicks;
               EntityDatabase.instance.updateRender(event.renderTickTime);
               Addons.onRenderTick(event.renderTickTime);
            }

         }
      }
   }

   public static void onTicksRestart() {
      EntityDatabase.instance.onTicksRestart();
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.END && Minecraft.getMinecraft().player != null && !Minecraft.getMinecraft().isGamePaused()) {
         EntityDatabase.instance.updateClient();
         Addons.onClientTick();
      }
   }
}
