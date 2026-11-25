package me.ichun.mods.ichunutil.common.module.worldportals.client.core;

import java.util.HashMap;
import java.util.HashSet;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.WorldPortalRenderer;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandlerWorldPortalClient {
   public float prevCameraRoll;
   public float cameraRoll;
   public WorldClient instance;
   public RenderGlobalProxy renderGlobalProxy;
   public HashSet<AxisAlignedBB> aabbToRender = new HashSet();

   public EventHandlerWorldPortalClient() {
   }

   @SubscribeEvent
   public void onCameraSetupEvent(CameraSetup event) {
      if (this.cameraRoll != 0.0F && WorldPortalRenderer.renderLevel <= 0) {
         event.setRoll(EntityHelper.interpolateValues(this.prevCameraRoll, this.cameraRoll, (float)event.getRenderPartialTicks()));
      }

   }

   @SubscribeEvent
   public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
      if (WorldPortals.eventHandler.isInPortal(event.getPlayer())) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onPushPlayerSPOutOfBlock(PlayerSPPushOutOfBlocksEvent event) {
      if (WorldPortals.eventHandler.isInPortal(event.getEntityPlayer())) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onRenderTick(RenderTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (event.phase == Phase.START) {
         if (this.instance != mc.world) {
            this.instance = mc.world;
            if (this.renderGlobalProxy == null) {
               this.renderGlobalProxy = new RenderGlobalProxy(mc);
               this.renderGlobalProxy.updateDestroyBlockIcons();
            }

            this.renderGlobalProxy.setWorldAndLoadRenderers(this.instance);
         }

         WorldPortalRenderer.renderLevel = 0;
         WorldPortalRenderer.renderCount = 0;
      }

   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         this.prevCameraRoll = this.cameraRoll;
         this.cameraRoll *= 0.85F;
         if (Math.abs(this.cameraRoll) < 0.05F) {
            this.cameraRoll = 0.0F;
         }
      }

   }

   @SubscribeEvent
   public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
      Minecraft.getMinecraft().addScheduledTask(this::disconnectFromServer);
   }

   public void disconnectFromServer() {
      ((HashMap)WorldPortals.eventHandler.monitoredEntities.get(Side.CLIENT)).clear();
      WorldPortalRenderer.renderLevel = 0;
      WorldPortalRenderer.rollFactor.clear();
   }
}
