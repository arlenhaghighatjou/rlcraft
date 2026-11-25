package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.Rect;
import net.minecraft.nbt.NBTTagCompound;

public class TileGroup implements ITileStorage {
   public static final String TAG_POSITION = "p";
   public static final String TAG_TILES = "t";
   public static final int CHUNK_STEP = 16;
   Rect scope = new Rect(0, 0, 16, 16);
   Tile[][] tiles = new Tile[16][16];

   public TileGroup(int x, int y) {
      this.scope.minX = x;
      this.scope.minY = y;
      this.scope.maxX = this.scope.minX + 16 - 1;
      this.scope.maxY = this.scope.minY + 16 - 1;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.scope.minX = compound.getIntArray("p")[0];
      this.scope.minY = compound.getIntArray("p")[1];
      this.scope.maxX = this.scope.minX + 16 - 1;
      this.scope.maxY = this.scope.minY + 16 - 1;
      int[] tileArray = compound.getIntArray("t");

      for(int y = 0; y < 16; ++y) {
         for(int x = 0; x < 16; ++x) {
            if (tileArray[x + y * 16] == -1) {
               this.tiles[x][y] = null;
            } else {
               this.tiles[x][y] = new Tile(tileArray[x + y * 16]);
            }
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      int[] tileArray = new int[256];
      int[] pos = new int[]{this.scope.minX, this.scope.minY};

      for(int y = 0; y < 16; ++y) {
         for(int x = 0; x < 16; ++x) {
            if (this.tiles[x][y] == null) {
               tileArray[x + y * 16] = -1;
            } else {
               tileArray[x + y * 16] = this.tiles[x][y].biomeID;
            }
         }
      }

      compound.setIntArray("p", pos);
      compound.setIntArray("t", tileArray);
      return compound;
   }

   public void setTile(int x, int y, Tile tile) {
      if (x >= this.scope.minX && y >= this.scope.minY && x <= this.scope.maxX && y <= this.scope.maxY) {
         int rx = x - this.scope.minX;
         int ry = y - this.scope.minY;
         this.tiles[rx][ry] = tile;
      } else {
         Log.warn("TileGroup tried to set tile out of bounds:\n\tbounds:" + this.scope + "\n\ttarget: x:" + x + ", y:" + y);
      }

   }

   public Tile removeTile(int x, int y) {
      Tile tmp = this.getTile(x, y);
      this.setTile(x, y, (Tile)null);
      return tmp;
   }

   public Tile getTile(int x, int y) {
      if (x >= this.scope.minX && y >= this.scope.minY && x <= this.scope.maxX && y <= this.scope.maxY) {
         int rx = x - this.scope.minX;
         int ry = y - this.scope.minY;
         return this.tiles[rx][ry];
      } else {
         return null;
      }
   }

   public boolean hasTileAt(int x, int y) {
      return this.getTile(x, y) != null;
   }

   public Rect getScope() {
      return this.scope;
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof TileGroup) {
         TileGroup other = (TileGroup)obj;
         if (!this.scope.equals(other.scope)) {
            return false;
         } else {
            for(int y = 0; y < 16; ++y) {
               for(int x = 0; x < 16; ++x) {
                  int a = this.tiles[x][y] == null ? -1 : this.tiles[x][y].biomeID;
                  int b = other.tiles[x][y] == null ? -1 : other.tiles[x][y].biomeID;
                  if (a != b) {
                     return false;
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
