package me.ichun.mods.ichunutil.common.core.event;

import java.util.ArrayList;
import java.util.HashSet;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.block.BlockCompactPorkchop;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import me.ichun.mods.ichunutil.common.packet.mod.PacketNewGrabbedEntityId;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatrons;
import me.ichun.mods.ichunutil.common.packet.mod.PacketUserShouldShowUpdates;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandlerServer {
   public int ticks;
   public HashSet<PatronInfo> patrons = new HashSet();
   public EntityTrackerRegistry entityTrackerRegistry = new EntityTrackerRegistry();

   public EventHandlerServer() {
   }

   @SubscribeEvent
   public void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         this.entityTrackerRegistry.tick();
         GrabHandler.tick(Side.SERVER);
         ++this.ticks;
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.side.isServer() && event.phase == Phase.END && EventCalendar.isAFDay() && (event.player.isPlayerSleeping() && event.player.getRNG().nextFloat() < 0.025F || event.player.getRNG().nextFloat() < 0.005F)) {
         event.player.getEntityWorld().playSound((EntityPlayer)null, event.player.posX, event.player.posY + (double)event.player.getEyeHeight(), event.player.posZ, SoundEvents.ENTITY_PIG_AMBIENT, SoundCategory.PLAYERS, event.player.isPlayerSleeping() ? 0.2F : 1.0F, (event.player.getRNG().nextFloat() - event.player.getRNG().nextFloat()) * 0.2F + 1.0F);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      for(ConfigBase conf : ConfigHandler.configs) {
         if (!conf.sessionProp.isEmpty()) {
            conf.sendPlayerSession(event.player);
         }
      }

      iChunUtil.channel.sendTo(new PacketPatrons((PatronInfo)null), event.player);
      iChunUtil.channel.sendTo(new PacketUserShouldShowUpdates(iChunUtil.config.versionNotificationTypes == 0 || iChunUtil.config.versionNotificationTypes == 1 && ((EntityPlayerMP)event.player).mcServer.getPlayerList().canSendCommands(event.player.getGameProfile())), event.player);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      ArrayList<GrabHandler> handlers = GrabHandler.getHandlers(event.player, Side.SERVER);

      for(int i = handlers.size() - 1; i >= 0; --i) {
         GrabHandler handler = (GrabHandler)handlers.get(i);
         if (handler.canSendAcrossDimensions()) {
            GrabHandler.dimensionalEntities.add(Integer.valueOf(handler.grabbed.getEntityId()));
            handler.grabbed.getEntityData().setInteger("Grabbed-ID", handler.grabbed.getEntityId());
            handler.grabbed.changeDimension(event.player.dimension);
            handler.update();
         } else {
            handler.terminate();
            handlers.remove(i);
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerLogout(PlayerLoggedOutEvent event) {
      this.patrons.remove(new PatronInfo(event.player.getGameProfile().getId().toString().replaceAll("-", ""), 1, false));
   }

   @SubscribeEvent
   public void onEntitySpawn(EntityJoinWorldEvent event) {
      if (!event.getEntity().world.isRemote && event.getEntity().getEntityData().hasKey("Grabbed-ID")) {
         Integer x = event.getEntity().getEntityData().getInteger("Grabbed-ID");
         if (event.getEntity().getEntityId() != x.intValue()) {
            for(int i = GrabHandler.dimensionalEntities.size() - 1; i >= 0; --i) {
               if (((Integer)GrabHandler.dimensionalEntities.get(i)).equals(x)) {
                  GrabHandler.dimensionalEntities.remove(i);

                  for(GrabHandler handler : (ArrayList)GrabHandler.grabbedEntities.get(Side.SERVER)) {
                     if (handler.grabbed.getEntityId() == x.intValue()) {
                        handler.grabbed = event.getEntity();
                        iChunUtil.channel.sendToAll(new PacketNewGrabbedEntityId(true, x.intValue(), event.getEntity().getEntityId()));
                     }
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onRegisterBlock(Register<Block> event) {
      iChunUtil.blockCompactPorkchop = new BlockCompactPorkchop();
      event.getRegistry().register(iChunUtil.blockCompactPorkchop);
   }

   @SubscribeEvent
   public void onRegisterItem(Register<Item> event) {
      event.getRegistry().register((new ItemBlock(iChunUtil.blockCompactPorkchop)).setRegistryName(iChunUtil.blockCompactPorkchop.getRegistryName()));
   }

   public void shuttingDownServer() {
      this.patrons.clear();
      this.entityTrackerRegistry.trackerEntries.clear();
      ((ArrayList)GrabHandler.grabbedEntities.get(Side.SERVER)).clear();
      GrabHandler.dimensionalEntities.clear();
   }
}
