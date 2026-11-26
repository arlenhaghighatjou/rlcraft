package hunternif.mc.atlas.marker;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.event.OptionalMarkerEvent;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.OptionalMarkerPacket;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.DummyWorldAccess;
import hunternif.mc.atlas.util.Log;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class NetherPortalWatcher extends DummyWorldAccess {
   private final Map<Integer, Integer> teleportingPlayersOrigin = new ConcurrentHashMap();

   public NetherPortalWatcher() {
   }

   public void register(EventBus bus) {
      bus.register(OptionalMarkerEvent.class, this::onOptionalMarker);
      bus.register(Load.class, this::onWorldLoad);
      bus.register(Unload.class, this::onWorldUnload);
   }

   public void onOptionalMarker(OptionalMarkerEvent event) {
      if (event.type == MarkerTypes.NETHER_PORTAL && !SettingsConfig.gameplay.autoNetherPortalMarkers) {
         event.setCanceled(true);
      }

   }

   public void onWorldLoad(Load event) {
      if (!event.getWorld().isRemote) {
         event.getWorld().addEventListener(this);
      }

   }

   public void onWorldUnload(Unload event) {
      if (!event.getWorld().isRemote) {
         event.getWorld().removeEventListener(this);
      }

   }

   public void onEntityAdded(Entity entity) {
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         if (this.teleportingPlayersOrigin.containsKey(Integer.valueOf(entity.getEntityId()))) {
            int origin = ((Integer)this.teleportingPlayersOrigin.remove(Integer.valueOf(entity.getEntityId()))).intValue();
            Log.info("Entering");
            int dimension = player.dimension;
            if (origin == DimensionType.NETHER.getId() || dimension == DimensionType.NETHER.getId()) {
               Log.info("Player %s teleported to the %s", player.getGameProfile().getName(), DimensionType.getById(dimension).getName());
               this.addPortalMarkerIfNone(player, dimension);
            }
         }
      }

   }

   public void onEntityRemoved(Entity entity) {
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         if (isEntityInPortal(entity)) {
            Log.info("Exiting");
            int originDimension = player.getEntityWorld().provider.getDimension();
            Log.info("Player %s left the %s", player.getGameProfile().getName(), DimensionType.getById(originDimension).getName());
            this.teleportingPlayersOrigin.put(Integer.valueOf(entity.getEntityId()), Integer.valueOf(originDimension));
            this.addPortalMarkerIfNone(player, originDimension);
         }
      }

   }

   private void addPortalMarkerIfNone(EntityPlayerMP player, int dimension) {
      World world = AntiqueAtlasMod.proxy.getServer().getWorld(dimension);
      if (!SettingsConfig.gameplay.itemNeeded) {
         this.addPortalMarkerIfNone(player, world, dimension, player.getUniqueID().hashCode());
      } else {
         for(ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
               this.addPortalMarkerIfNone(player, world, dimension, stack.getItemDamage());
            }
         }

      }
   }

   private void addPortalMarkerIfNone(EntityPlayerMP player, World world, int dimension, int atlasID) {
      DimensionMarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world).getMarkersDataInDimension(dimension);
      int x = (int)player.posX;
      int z = (int)player.posZ;
      List<Marker> markers = data.getMarkersAtChunk((x >> 4) / 8, (z >> 4) / 8);
      if (markers != null) {
         for(Marker marker : markers) {
            if (marker.isOfType(MarkerTypes.NETHER_PORTAL)) {
               return;
            }
         }
      }

      PacketDispatcher.sendTo(new OptionalMarkerPacket(atlasID, world.provider.getDimension(), MarkerTypes.NETHER_PORTAL, "gui.antiqueatlas.marker.netherPortal", x, z, false), player);
   }

   private static boolean isEntityInPortal(Entity entity) {
      return entity.inPortal;
   }
}
