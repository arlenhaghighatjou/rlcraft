package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.util.IntVec2;
import hunternif.mc.atlas.util.ListMapValueIterator;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DimensionMarkersData {
   private final MarkersData parent;
   private final int dimension;
   private int size = 0;
   private final Map<IntVec2, List<Marker>> chunkMap = new ConcurrentHashMap(2, 0.75F, 2);
   private final DimensionMarkersData.Values values = new DimensionMarkersData.Values();
   private final IntVec2 key = new IntVec2(0.0D, 0.0D);

   private IntVec2 getKey() {
      return this.key;
   }

   public DimensionMarkersData(MarkersData parent, int dimension) {
      this.parent = parent;
      this.dimension = dimension;
   }

   public int getDimension() {
      return this.dimension;
   }

   public List<Marker> getMarkersAtChunk(int x, int z) {
      return (List)this.chunkMap.get(this.getKey().set(x, z));
   }

   public void insertMarker(Marker marker) {
      IntVec2 key = this.getKey().set(marker.getChunkX() / 8, marker.getChunkZ() / 8);
      List<Marker> list = (List)this.chunkMap.get(key);
      if (list == null) {
         list = new CopyOnWriteArrayList();
         this.chunkMap.put(key.clone(), list);
      }

      boolean inserted = false;

      for(int i = 0; i < list.size(); ++i) {
         if (((Marker)list.get(i)).getZ() > marker.getZ()) {
            list.add(i, marker);
            inserted = true;
            break;
         }
      }

      if (!inserted) {
         list.add(marker);
      }

      ++this.size;
      this.parent.markDirty();
   }

   public boolean removeMarker(Marker marker) {
      --this.size;
      return this.getMarkersAtChunk(marker.getChunkX() / 8, marker.getChunkZ() / 8).remove(marker);
   }

   public Collection<Marker> getAllMarkers() {
      return this.values;
   }

   private class Values extends AbstractCollection<Marker> {
      private Values() {
      }

      public Iterator<Marker> iterator() {
         return (new ListMapValueIterator<Marker>(DimensionMarkersData.this.chunkMap)).setImmutable(true);
      }

      public int size() {
         return DimensionMarkersData.this.size;
      }
   }
}
