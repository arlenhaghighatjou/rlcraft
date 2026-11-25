package org.orecruncher.dsurround.client.hud;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public final class GuiHUDHandler {
   private static GuiHUDHandler INSTANCE;
   private final ObjectArray<IGuiOverlay> overlays = new ObjectArray(1);

   private GuiHUDHandler() {
      this.register(new InspectionHUD());
   }

   public void register(IGuiOverlay overlay) {
      this.overlays.add(overlay);
   }

   public static void register() {
      INSTANCE = new GuiHUDHandler();
      MinecraftForge.EVENT_BUS.register(INSTANCE);
   }

   public static void unregister() {
      if (INSTANCE != null) {
         MinecraftForge.EVENT_BUS.unregister(INSTANCE);
         INSTANCE = null;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onRenderGameOverlayEvent(Pre event) {
      this.overlays.forEach((o) -> {
         o.doRender(event);
      });
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onRenderGameOverlayEvent(Post event) {
      this.overlays.forEach((o) -> {
         o.doRender(event);
      });
   }

   @SubscribeEvent
   public void playerTick(PlayerTickEvent event) {
      if (event.side != Side.SERVER && event.phase != Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
         if (event.player != null && event.player.world != null) {
            if (event.player == Minecraft.getMinecraft().player) {
               int tickRef = EnvironStateHandler.EnvironState.getTickCounter();
               this.overlays.forEach((o) -> {
                  o.doTick(tickRef);
               });
            }
         }
      }
   }
}
