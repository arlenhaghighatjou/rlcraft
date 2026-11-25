package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.util.Log;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public class MarkersData extends WorldSavedData {
   private static final int VERSION = 3;
   private static final String TAG_VERSION = "aaVersion";
   private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
   private static final String TAG_DIMENSION_ID = "dimID";
   private static final String TAG_MARKERS = "markers";
   private static final String TAG_MARKER_ID = "id";
   private static final String TAG_MARKER_TYPE = "markerType";
   private static final String TAG_MARKER_LABEL = "label";
   private static final String TAG_MARKER_X = "x";
   private static final String TAG_MARKER_Y = "y";
   private static final String TAG_MARKER_VISIBLE_AHEAD = "visAh";
   public static final int CHUNK_STEP = 8;
   private final Set<EntityPlayer> playersSentTo = new HashSet();
   private final AtomicInteger largestID = new AtomicInteger(0);
   private final Map<Integer, Marker> idMap = new ConcurrentHashMap(2, 0.75F, 2);
   private final Map<Integer, DimensionMarkersData> dimensionMap = new ConcurrentHashMap(2, 0.75F, 2);

   private int getNewID() {
      return this.largestID.incrementAndGet();
   }

   public MarkersData(String key) {
      super(key);
   }

   public void readFromNBT(NBTTagCompound compound) {
      int version = compound.getInteger("aaVersion");
      if (version < 3) {
         Log.warn("Outdated atlas data format! Was %d but current is %d", version, Integer.valueOf(3));
         this.markDirty();
      }

      NBTTagList dimensionMapList = compound.getTagList("dimMap", 10);

      for(int d = 0; d < dimensionMapList.tagCount(); ++d) {
         NBTTagCompound tag = dimensionMapList.getCompoundTagAt(d);
         int dimensionID = tag.getInteger("dimID");
         NBTTagList tagList = tag.getTagList("markers", 10);

         for(int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound markerTag = tagList.getCompoundTagAt(i);
            boolean visibleAhead = true;
            if (version < 2) {
               Log.warn("Marker is visible ahead by default");
            } else {
               visibleAhead = markerTag.getBoolean("visAh");
            }

            int id;
            if (version < 3) {
               id = this.getNewID();
            } else {
               id = markerTag.getInteger("id");
               if (this.getMarkerByID(id) != null) {
                  Log.warn("Loading marker with duplicate id %d. Getting new id", id);
                  id = this.getNewID();
               }

               this.markDirty();
            }

            if (this.largestID.intValue() < id) {
               this.largestID.set(id);
            }

            Marker marker = new Marker(id, markerTag.getString("markerType"), markerTag.getString("label"), dimensionID, markerTag.getInteger("x"), markerTag.getInteger("y"), visibleAhead);
            this.loadMarker(marker);
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      Log.info("Saving local markers data to NBT");
      compound.setInteger("aaVersion", 3);
      NBTTagList dimensionMapList = new NBTTagList();

      for(Integer dimension : this.dimensionMap.keySet()) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setInteger("dimID", dimension.intValue());
         DimensionMarkersData data = this.getMarkersDataInDimension(dimension.intValue());
         NBTTagList tagList = new NBTTagList();

         for(Marker marker : data.getAllMarkers()) {
            NBTTagCompound markerTag = new NBTTagCompound();
            markerTag.setInteger("id", marker.getId());
            markerTag.setString("markerType", marker.getType());
            markerTag.setString("label", marker.getLabel());
            markerTag.setInteger("x", marker.getX());
            markerTag.setInteger("y", marker.getZ());
            markerTag.setBoolean("visAh", marker.isVisibleAhead());
            tagList.appendTag(markerTag);
         }

         tag.setTag("markers", tagList);
         dimensionMapList.appendTag(tag);
      }

      compound.setTag("dimMap", dimensionMapList);
      return compound;
   }

   public Set<Integer> getVisitedDimensions() {
      return this.dimensionMap.keySet();
   }

   public Collection<Marker> getMarkersInDimension(int dimension) {
      return this.getMarkersDataInDimension(dimension).getAllMarkers();
   }

   public DimensionMarkersData getMarkersDataInDimension(int dimension) {
      return (DimensionMarkersData)this.dimensionMap.computeIfAbsent(Integer.valueOf(dimension), (k) -> {
         return new DimensionMarkersData(this, dimension);
      });
   }

   public List<Marker> getMarkersAtChunk(int dimension, int x, int z) {
      return this.getMarkersDataInDimension(dimension).getMarkersAtChunk(x, z);
   }

   private Marker getMarkerByID(int id) {
      return (Marker)this.idMap.get(Integer.valueOf(id));
   }

   public Marker removeMarker(int id) {
      Marker marker = this.getMarkerByID(id);
      if (marker == null) {
         return null;
      } else {
         if (this.idMap.remove(Integer.valueOf(id)) != null) {
            this.getMarkersDataInDimension(marker.getDimension()).removeMarker(marker);
            this.markDirty();
         }

         return marker;
      }
   }

   public Marker createAndSaveMarker(String type, String label, int dimension, int x, int z, boolean visibleAhead) {
      Marker marker = new Marker(this.getNewID(), type, label, dimension, x, z, visibleAhead);
      Log.info("Created new marker %s", marker.toString());
      this.idMap.put(Integer.valueOf(marker.getId()), marker);
      this.getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
      this.markDirty();
      return marker;
   }

   public Marker loadMarker(Marker marker) {
      if (!this.idMap.containsKey(Integer.valueOf(marker.getId()))) {
         this.idMap.put(Integer.valueOf(marker.getId()), marker);
         int totalMarkers = 0;

         for(Entry<Integer, DimensionMarkersData> e : this.dimensionMap.entrySet()) {
            totalMarkers += ((DimensionMarkersData)e.getValue()).getAllMarkers().size();
         }

         if (totalMarkers < SettingsConfig.performance.markerLimit) {
            this.getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
         } else {
            Log.warn("Could not add new marker. Atlas is at it's limit of %d markers", SettingsConfig.performance.markerLimit);
         }
      }

      return marker;
   }

   public boolean isSyncedOnPlayer(EntityPlayer player) {
      return this.playersSentTo.contains(player);
   }

   public void syncOnPlayer(int atlasID, EntityPlayer player) {
      for(Integer dimension : this.dimensionMap.keySet()) {
         MarkersPacket packet = this.newMarkersPacket(atlasID, dimension.intValue());
         DimensionMarkersData data = this.getMarkersDataInDimension(dimension.intValue());

         for(Marker marker : data.getAllMarkers()) {
            packet.putMarker(marker);
         }

         PacketDispatcher.sendTo(packet, (EntityPlayerMP)player);
      }

      Log.info("Sent markers data #%d to player %s", atlasID, player.getName());
      this.playersSentTo.add(player);
   }

   MarkersPacket newMarkersPacket(int atlasID, int dimension) {
      return new MarkersPacket(atlasID, dimension, new Marker[0]);
   }

   public boolean isEmpty() {
      return this.idMap.isEmpty();
   }
}
