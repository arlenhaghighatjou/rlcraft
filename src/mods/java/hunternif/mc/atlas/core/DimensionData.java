package hunternif.mc.atlas.core;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileGroupsPacket;
import hunternif.mc.atlas.util.IntVec2;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.Rect;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class DimensionData implements ITileStorage {
   public final AtlasData parent;
   public final int dimension;
   private int browsingX;
   private int browsingY;
   private double browsingZoom = 0.5D;
   private final Map<IntVec2, TileGroup> tileGroups = new ConcurrentHashMap(2, 0.75F, 2);
   private final IntVec2 key = new IntVec2(0.0D, 0.0D);
   private final Rect scope = new Rect();

   public DimensionData(AtlasData parent, int dimension) {
      this.parent = parent;
      this.dimension = dimension;
   }

   public Map<IntVec2, Tile> getSeenChunks() {
      Map<IntVec2, Tile> chunks = new ConcurrentHashMap(2, 0.75F, 2);
      Tile t = null;

      for(Entry<IntVec2, TileGroup> entry : this.tileGroups.entrySet()) {
         int basex = ((TileGroup)entry.getValue()).getScope().minX;
         int basey = ((TileGroup)entry.getValue()).getScope().minY;

         for(int x = basex; x < basex + 16; ++x) {
            for(int y = basey; y < basey + 16; ++y) {
               t = ((TileGroup)entry.getValue()).getTile(x, y);
               if (t != null) {
                  chunks.put(new IntVec2((double)x, (double)y), t);
               }
            }
         }
      }

      return chunks;
   }

   public void setBrowsingPosition(int x, int y, double zoom) {
      this.browsingX = x;
      this.browsingY = y;
      this.browsingZoom = zoom;
      if (this.browsingZoom <= 0.0D) {
         Log.warn("Setting map zoom to invalid value of %f", zoom);
         this.browsingZoom = SettingsConfig.userInterface.minScale;
      }

      this.parent.markDirty();
   }

   public int getBrowsingX() {
      return this.browsingX;
   }

   public int getBrowsingY() {
      return this.browsingY;
   }

   public double getBrowsingZoom() {
      return this.browsingZoom;
   }

   private IntVec2 getKey() {
      return this.key;
   }

   public void setTile(int x, int y, Tile tile) {
      IntVec2 groupPos = this.getKey().set((int)Math.floor((double)((float)x / 16.0F)), (int)Math.floor((double)((float)y / 16.0F)));
      TileGroup tg = (TileGroup)this.tileGroups.get(groupPos);
      if (tg == null) {
         tg = new TileGroup(groupPos.x * 16, groupPos.y * 16);
         this.tileGroups.put(groupPos, tg);
      }

      tg.setTile(x, y, tile);
      this.scope.extendTo(x, y);
      this.parent.markDirty();
   }

   public void putTileGroup(TileGroup t) {
      IntVec2 key = new IntVec2((double)Math.floorDiv(t.scope.minX, 16), (double)Math.floorDiv(t.scope.minY, 16));
      this.tileGroups.put(key, t);
      this.extendToTileGroup(t);
   }

   public Tile removeTile(int x, int y) {
      return this.getTile(x, y);
   }

   public Tile getTile(int x, int y) {
      IntVec2 groupPos = this.getKey().set((int)Math.floor((double)((float)x / 16.0F)), (int)Math.floor((double)((float)y / 16.0F)));
      TileGroup tg = (TileGroup)this.tileGroups.get(groupPos);
      return tg == null ? null : tg.getTile(x, y);
   }

   public boolean hasTileAt(int x, int y) {
      return this.getTile(x, y) != null;
   }

   public Rect getScope() {
      return this.scope;
   }

   public DimensionData clone() {
      DimensionData data = new DimensionData(this.parent, this.dimension);
      data.tileGroups.putAll(this.tileGroups);
      data.scope.set(this.scope);
      return data;
   }

   public void addData(DimensionData other) {
      for(Entry<IntVec2, TileGroup> e : other.tileGroups.entrySet()) {
         TileGroup group = (TileGroup)e.getValue();
         Rect s = group.getScope();

         for(int x = s.minX; x <= s.maxX; ++x) {
            for(int y = s.minY; y <= s.maxY; ++y) {
               Tile tile = group.getTile(x, y);
               if (tile != null) {
                  this.setTile(x, y, tile);
               }
            }
         }
      }

   }

   public NBTTagList writeToNBT() {
      NBTTagList tileGroupList = new NBTTagList();

      for(Entry<IntVec2, TileGroup> entry : this.tileGroups.entrySet()) {
         NBTTagCompound newbie = new NBTTagCompound();
         ((TileGroup)entry.getValue()).writeToNBT(newbie);
         tileGroupList.appendTag(newbie);
      }

      return tileGroupList;
   }

   private void extendToTileGroup(TileGroup tg) {
      for(int x = tg.scope.minX; x <= tg.scope.maxX; ++x) {
         for(int y = tg.scope.minY; y <= tg.scope.maxY; ++y) {
            if (tg.hasTileAt(x, y)) {
               this.scope.extendTo(x, y);
            }
         }
      }

   }

   public void readFromNBT(NBTTagList me) {
      if (me != null) {
         for(int d = 0; d < me.tagCount(); ++d) {
            NBTTagCompound tgTag = me.getCompoundTagAt(d);
            TileGroup tg = new TileGroup(0, 0);
            tg.readFromNBT(tgTag);
            this.putTileGroup(tg);
         }

      }
   }

   public void syncOnPlayer(int atlasID, EntityPlayer player) {
      Log.info("Sending dimension #%d", this.dimension);
      ArrayList<TileGroup> tgs = new ArrayList(100);
      int count = 0;
      int total = 0;

      for(Entry<IntVec2, TileGroup> t : this.tileGroups.entrySet()) {
         tgs.add((TileGroup)t.getValue());
         ++count;
         ++total;
         if (count >= 100) {
            TileGroupsPacket p = new TileGroupsPacket(tgs, atlasID, this.dimension);
            PacketDispatcher.sendTo(p, (EntityPlayerMP)player);
            tgs.clear();
            count = 0;
         }
      }

      if (count > 0) {
         TileGroupsPacket p = new TileGroupsPacket(tgs, atlasID, this.dimension);
         PacketDispatcher.sendTo(p, (EntityPlayerMP)player);
      }

      Log.info("Sent dimension #%d (%d tiles)", this.dimension, total);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof DimensionData)) {
         return false;
      } else {
         DimensionData other = (DimensionData)obj;
         if (other.tileGroups.size() != this.tileGroups.size()) {
            return false;
         } else {
            for(IntVec2 entry : this.tileGroups.keySet()) {
               if (!((TileGroup)this.tileGroups.get(entry)).equals(other.tileGroups.get(entry))) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
