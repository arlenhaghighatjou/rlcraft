package me.ichun.mods.googlyeyes.common.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.layerrenderer.LayerGooglyEyes;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.core.event.RendererSafeCompatibilityEvent;
import me.ichun.mods.ichunutil.client.entity.head.HeadHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EventHandler {
   protected WeakHashMap<EntityLivingBase, GooglyTracker> trackers = new WeakHashMap();

   public EventHandler() {
   }

   @SubscribeEvent
   public void onWorldTick(ClientTickEvent event) {
      if (event.phase == Phase.END && Minecraft.getMinecraft().world != null && !Minecraft.getMinecraft().isGamePaused()) {
         Iterator<Entry<EntityLivingBase, GooglyTracker>> ite = this.trackers.entrySet().iterator();

         while(ite.hasNext()) {
            Entry<EntityLivingBase, GooglyTracker> e = (Entry)ite.next();
            GooglyTracker tracker = (GooglyTracker)e.getValue();
            if ((long)iChunUtil.eventHandlerClient.ticks - tracker.lastUpdateRequest > 10L) {
               ite.remove();
            } else {
               tracker.update();
            }
         }
      }

   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      if (event.getWorld().isRemote) {
         Iterator<Entry<EntityLivingBase, GooglyTracker>> ite = this.trackers.entrySet().iterator();

         while(ite.hasNext()) {
            Entry<EntityLivingBase, GooglyTracker> e = (Entry)ite.next();
            GooglyTracker tracker = (GooglyTracker)e.getValue();
            if (tracker.parent.getEntityWorld() == event.getWorld()) {
               ite.remove();
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onRendererSafeCompatibilityEvent(RendererSafeCompatibilityEvent event) {
      LayerGooglyEyes layerGooglyEyes = new LayerGooglyEyes(Minecraft.getMinecraft().getTextureManager());
      ArrayList<RenderLivingBase> addedRenderers = new ArrayList();
      boolean doPlayer = true;

      for(String s : GooglyEyes.config.disabledGoogly) {
         if (s.equalsIgnoreCase("player")) {
            doPlayer = false;
            break;
         }
      }

      if (doPlayer) {
         Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

         for(Entry<String, RenderPlayer> e : skinMap.entrySet()) {
            ((RenderPlayer)e.getValue()).addLayer(layerGooglyEyes);
            addedRenderers.add(e.getValue());
         }
      }

      label76:
      for(Entry<Class<? extends Entity>, Render<? extends Entity>> entry : Minecraft.getMinecraft().getRenderManager().entityRenderMap.entrySet()) {
         if (!EntityPlayer.class.isAssignableFrom((Class)entry.getKey()) && EntityLivingBase.class.isAssignableFrom((Class)entry.getKey()) && RenderLivingBase.class.isAssignableFrom(((Render)entry.getValue()).getClass())) {
            boolean addLayer = true;
            EntityEntry entEntry = EntityRegistry.getEntry((Class)entry.getKey());
            if (entEntry != null) {
               String entName = entEntry.getName();

               for(String s : GooglyEyes.config.disabledGoogly) {
                  if (s.equalsIgnoreCase(entName) || entEntry.getRegistryName() != null && s.equalsIgnoreCase(entEntry.getRegistryName().toString())) {
                     addLayer = false;
                     break;
                  }
               }
            }

            if (addLayer) {
               Iterator var21 = HeadHandler.modelOffsetHelpers.entrySet().iterator();

               while(true) {
                  if (!var21.hasNext()) {
                     continue label76;
                  }

                  Entry<Class<? extends EntityLivingBase>, HeadBase> e = (Entry)var21.next();
                  if (((Class)e.getKey()).isAssignableFrom((Class)entry.getKey())) {
                     break;
                  }
               }

               RenderLivingBase renderLiving = (RenderLivingBase)entry.getValue();
               this.addGooglyLayer(renderLiving, layerGooglyEyes);
            }
         }
      }

   }

   public void addGooglyLayer(RenderLivingBase render, LayerGooglyEyes layerGooglyEyes) {
      render.addLayer(layerGooglyEyes);
   }

   public GooglyTracker getGooglyTracker(EntityLivingBase living, HeadBase helper) {
      GooglyTracker tracker = (GooglyTracker)this.trackers.get(living);
      if (tracker == null) {
         tracker = new GooglyTracker(living, helper);
         this.trackers.put(living, tracker);
      }

      return tracker;
   }
}
