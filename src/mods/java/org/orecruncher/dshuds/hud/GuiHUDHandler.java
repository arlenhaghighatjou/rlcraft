package org.orecruncher.dshuds.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dshuds.Environment;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "dshuds"
)
public final class GuiHUDHandler {
   private static final List<GuiOverlay> overlays = new ArrayList();

   public GuiHUDHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onRenderGameOverlayEvent(Pre event) {
      for(int i = 0; i < overlays.size(); ++i) {
         ((GuiOverlay)overlays.get(i)).doRender(event);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onRenderGameOverlayEvent(Post event) {
      for(int i = 0; i < overlays.size(); ++i) {
         ((GuiOverlay)overlays.get(i)).doRender(event);
      }

   }

   @SubscribeEvent
   public static void playerTick(PlayerTickEvent event) {
      if (event.side != Side.SERVER && event.phase != Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
         if (event.player != null && event.player.world != null) {
            if (event.player == Environment.getPlayer()) {
               int tickRef = Environment.getTickCounter();

               for(int i = 0; i < overlays.size(); ++i) {
                  ((GuiOverlay)overlays.get(i)).doTick(tickRef);
               }

            }
         }
      }
   }

   static {
      overlays.add(new PotionHUD());
      overlays.add(new CompassHUD());
      overlays.add(new LightLevelHUD());
   }
}
