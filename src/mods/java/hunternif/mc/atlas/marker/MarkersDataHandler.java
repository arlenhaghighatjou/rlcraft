package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class MarkersDataHandler {
   private static final String MARKERS_DATA_PREFIX = "aaMarkers_";
   private final Map<String, MarkersData> markersDataClientCache = new ConcurrentHashMap();

   public MarkersDataHandler() {
   }

   public MarkersData getMarkersData(ItemStack stack, World world) {
      return stack.getItem() == RegistrarAntiqueAtlas.ATLAS ? this.getMarkersData(stack.getItemDamage(), world) : null;
   }

   public MarkersData getMarkersData(int atlasID, World world) {
      String key = this.getMarkersDataKey(atlasID);
      MarkersData data = null;
      if (world.isRemote) {
         data = (MarkersData)this.markersDataClientCache.get(key);
      }

      if (data == null) {
         data = (MarkersData)world.loadData(MarkersData.class, key);
         if (data == null) {
            data = new MarkersData(key);
            world.setData(key, data);
         }

         if (world.isRemote) {
            this.markersDataClientCache.put(key, data);
            this.addGlobalMarkers(data);
         }
      }

      return data;
   }

   private void addGlobalMarkers(MarkersData data) {
      MarkersData global = AntiqueAtlasMod.globalMarkersData.getData();
      Iterator var3 = global.getVisitedDimensions().iterator();

      while(var3.hasNext()) {
         int dim = ((Integer)var3.next()).intValue();

         for(Marker marker : global.getMarkersInDimension(dim)) {
            Marker localCopy = new Marker(-marker.getId(), marker.getType(), marker.getLabel(), marker.getDimension(), marker.getX(), marker.getZ(), marker.isVisibleAhead());
            data.loadMarker(localCopy);
         }
      }

   }

   public Collection<MarkersData> getAllLoadedMarkersData() {
      return this.markersDataClientCache.values();
   }

   private String getMarkersDataKey(int atlasID) {
      return "aaMarkers_" + atlasID;
   }

   @SubscribeEvent
   public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
      this.markersDataClientCache.clear();
   }
}
