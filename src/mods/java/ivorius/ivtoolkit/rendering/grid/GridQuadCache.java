package ivorius.ivtoolkit.rendering.grid;

import com.google.common.base.Function;
import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

public class GridQuadCache<T> implements Iterable<GridQuadCache.CachedQuadLevel<T>> {
   protected final List<GridQuadCache.CachedQuadLevel<T>> cachedQuadLevels = new ArrayList();
   protected float[] size;

   public GridQuadCache() {
   }

   public static int[] getCacheAxes(EnumFacing direction, int... axes) {
      switch(direction) {
      case DOWN:
      case UP:
         return new int[]{axes[1], axes[0], axes[2]};
      case WEST:
      case EAST:
         return new int[]{axes[0], axes[2], axes[1]};
      case NORTH:
      case SOUTH:
         return new int[]{axes[2], axes[1], axes[0]};
      default:
         throw new IllegalArgumentException();
      }
   }

   public static int[] getNormalAxes(EnumFacing direction, int... axes) {
      return getCacheAxes(direction, axes);
   }

   public static float[] getCacheAxes(EnumFacing direction, float... axes) {
      switch(direction) {
      case DOWN:
      case UP:
         return new float[]{axes[1], axes[0], axes[2]};
      case WEST:
      case EAST:
         return new float[]{axes[0], axes[2], axes[1]};
      case NORTH:
      case SOUTH:
         return new float[]{axes[2], axes[1], axes[0]};
      default:
         throw new IllegalArgumentException();
      }
   }

   public static float[] getNormalAxes(EnumFacing direction, float... axes) {
      return getCacheAxes(direction, axes);
   }

   public static <T> GridQuadCache<T> createQuadCache(int[] size, float[] scale, Function<Pair<BlockPos, EnumFacing>, T> mapper) {
      return createQuadCacheGreedy(size, scale, mapper);
   }

   protected static <T> GridQuadCache<T> createQuadCacheGreedy(int[] size, float[] scale, Function<Pair<BlockPos, EnumFacing>, T> mapper) {
      Map<GridQuadCache.QuadContext<T>, GridQuadCache.CoordGrid> partialCache = new HashMap();

      for(int x = 0; x < size[0]; ++x) {
         for(int y = 0; y < size[1]; ++y) {
            for(int z = 0; z < size[2]; ++z) {
               BlockPos coord = new BlockPos(x, y, z);
               addToCache(partialCache, mapper, EnumFacing.UP, coord);
               addToCache(partialCache, mapper, EnumFacing.DOWN, coord);
               addToCache(partialCache, mapper, EnumFacing.NORTH, coord);
               addToCache(partialCache, mapper, EnumFacing.EAST, coord);
               addToCache(partialCache, mapper, EnumFacing.SOUTH, coord);
               addToCache(partialCache, mapper, EnumFacing.WEST, coord);
            }
         }
      }

      Set<Entry<GridQuadCache.QuadContext<T>, GridQuadCache.CoordGrid>> quads = partialCache.entrySet();
      GridQuadCache<T> cache = new GridQuadCache<T>();
      cache.size = new float[3];

      for(int i = 0; i < 3; ++i) {
         cache.size[i] = (float)size[i] * scale[i];
      }

      for(Entry<GridQuadCache.QuadContext<T>, GridQuadCache.CoordGrid> entry : quads) {
         GridQuadCache.QuadContext<T> context = (GridQuadCache.QuadContext)entry.getKey();
         int[] sAxes = getCacheAxes(context.direction, size);
         float[] scAxes = getCacheAxes(context.direction, scale);
         GridQuadCache.QuadCollection mesh = ((GridQuadCache.CoordGrid)entry.getValue()).computeMesh(0, 0, sAxes[1], sAxes[2]);
         FloatBuffer cachedQuadCoords = BufferUtils.createFloatBuffer(mesh.quadCount() * 4);
         float pxAxis = scAxes[1];
         float pzAxis = scAxes[2];

         for(int i = 0; i < mesh.quadCount(); ++i) {
            cachedQuadCoords.put((float)mesh.x1(i) * pxAxis).put((float)mesh.y1(i) * pzAxis).put((float)(mesh.x2(i) + 1) * pxAxis).put((float)(mesh.y2(i) + 1) * pzAxis);
         }

         cachedQuadCoords.position(0);
         float zLevel = (float)(context.direction.getFrontOffsetX() + context.direction.getFrontOffsetY() + context.direction.getFrontOffsetZ() > 0 ? context.layer + 1 : context.layer) * scAxes[0];
         cache.cachedQuadLevels.add(new GridQuadCache.CachedQuadLevel(zLevel, context.direction, context.t, cachedQuadCoords));
      }

      return cache;
   }

   protected static <T> void addToCache(Map<GridQuadCache.QuadContext<T>, GridQuadCache.CoordGrid> cache, Function<Pair<BlockPos, EnumFacing>, T> mapper, EnumFacing direction, BlockPos coord) {
      T t = (T)mapper.apply(Pair.of(coord, direction));
      if (t != null) {
         int[] sAxes = getCacheAxes(direction, coord.getX(), coord.getY(), coord.getZ());
         addToCache(cache, new GridQuadCache.QuadContext(sAxes[0], direction, t), sAxes[1], sAxes[2]);
      }

   }

   protected static <T> void addToCache(Map<GridQuadCache.QuadContext<T>, GridQuadCache.CoordGrid> cache, GridQuadCache.QuadContext<T> context, int x, int y) {
      GridQuadCache.CoordGrid quad = (GridQuadCache.CoordGrid)cache.get(context);
      if (quad == null) {
         cache.put(context, quad = new GridQuadCache.CoordGrid());
      }

      quad.addCoord(x, y);
   }

   public float[] getSize() {
      return (float[])this.size.clone();
   }

   public Iterator<GridQuadCache.CachedQuadLevel<T>> iterator() {
      return this.cachedQuadLevels.iterator();
   }

   public static class CachedQuadLevel<T> {
      public final float zLevel;
      public final EnumFacing direction;
      public final T t;
      public final FloatBuffer quads;

      public CachedQuadLevel(float zLevel, EnumFacing direction, T t, FloatBuffer quads) {
         this.zLevel = zLevel;
         this.direction = direction;
         this.t = t;
         this.quads = quads;
      }
   }

   public static class CoordGrid extends TIntArrayList {
      public CoordGrid() {
      }

      public CoordGrid(int capacity) {
         super(capacity);
      }

      public CoordGrid(int capacity, int no_entry_value) {
         super(capacity, no_entry_value);
      }

      public CoordGrid(TIntCollection collection) {
         super(collection);
      }

      public CoordGrid(int[] values) {
         super(values);
      }

      public CoordGrid(int[] values, int no_entry_value, boolean wrap) {
         super(values, no_entry_value, wrap);
      }

      private static boolean isFree(boolean[][] mask, int lX, int hX, int y) {
         for(int tX = lX; tX <= hX; ++tX) {
            if (!mask[tX][y]) {
               return false;
            }
         }

         return true;
      }

      public void addCoord(int x, int y) {
         this.add(x);
         this.add(y);
      }

      public int coordCount() {
         return this.size() / 2;
      }

      public int x(int index) {
         return this.get(index * 2);
      }

      public int y(int index) {
         return this.get(index * 2 + 1);
      }

      public GridQuadCache.QuadCollection computeMesh(int minX, int minY, int maxX, int maxY) {
         boolean[][] mask = new boolean[maxX - minX][maxY - minY];
         GridQuadCache.QuadCollection collection = new GridQuadCache.QuadCollection();

         for(int c = 0; c < this.coordCount(); ++c) {
            mask[this.x(c)][this.y(c)] = true;
         }

         for(int x = minX; x < maxX; ++x) {
            for(int y = minY; y < maxY; ++y) {
               if (mask[x][y]) {
                  int lX = x;
                  int hX = x;
                  int lY = y;

                  int hY;
                  for(hY = y; lX > minX && mask[lX - 1][y]; --lX) {
                     ;
                  }

                  while(hX < maxX - 1 && mask[hX + 1][y]) {
                     ++hX;
                  }

                  while(lY > minY && isFree(mask, lX, hX, lY - 1)) {
                     --lY;
                  }

                  while(hY < maxY - 1 && isFree(mask, lX, hX, hY + 1)) {
                     ++hY;
                  }

                  for(int tX = lX; tX <= hX; ++tX) {
                     for(int tY = lY; tY <= hY; ++tY) {
                        mask[tX][tY] = false;
                     }
                  }

                  collection.addQuad(lX, lY, hX, hY);
               }
            }
         }

         return collection;
      }
   }

   public static class QuadCollection extends TIntArrayList {
      public QuadCollection() {
      }

      public QuadCollection(int capacity) {
         super(capacity);
      }

      public QuadCollection(int capacity, int no_entry_value) {
         super(capacity, no_entry_value);
      }

      public QuadCollection(TIntCollection collection) {
         super(collection);
      }

      public QuadCollection(int[] values) {
         super(values);
      }

      public QuadCollection(int[] values, int no_entry_value, boolean wrap) {
         super(values, no_entry_value, wrap);
      }

      public void addQuad(int x1, int y1, int x2, int y2) {
         this.add(x1);
         this.add(y1);
         this.add(x2);
         this.add(y2);
      }

      public int x1(int index) {
         return this.get(index * 4);
      }

      public int x2(int index) {
         return this.get(index * 4 + 2);
      }

      public int y1(int index) {
         return this.get(index * 4 + 1);
      }

      public int y2(int index) {
         return this.get(index * 4 + 3);
      }

      public int quadCount() {
         return this.size() / 4;
      }
   }

   public static class QuadContext<T> {
      public final int layer;
      public final EnumFacing direction;
      public final T t;

      public QuadContext(int layer, EnumFacing direction, T t) {
         this.layer = layer;
         this.direction = direction;
         this.t = t;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            GridQuadCache.QuadContext that = (GridQuadCache.QuadContext)o;
            if (this.layer != that.layer) {
               return false;
            } else if (this.direction != that.direction) {
               return false;
            } else {
               return this.t.equals(that.t);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.layer;
         result = 31 * result + this.direction.hashCode();
         result = 31 * result + this.t.hashCode();
         return result;
      }
   }
}
