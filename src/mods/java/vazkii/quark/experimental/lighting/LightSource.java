package vazkii.quark.experimental.lighting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.quark.experimental.features.ColoredLights;

public class LightSource {
   public final World world;
   public final BlockPos pos;
   public final IBlockState state;
   public final byte brightness;
   public byte[][][] incidences = (byte[][][])null;

   public LightSource(World world, BlockPos pos, IBlockState state, int brightness) {
      this.world = world;
      this.pos = pos;
      this.state = state;
      this.brightness = (byte)brightness;
   }

   public void newFrame() {
      this.incidences = (byte[][][])null;
   }

   public int getIncidence(BlockPos checkPos) {
      int dist = this.manhattanDistance(checkPos);
      if (dist >= this.brightness) {
         return 0;
      } else if (ColoredLights.simulateTravel) {
         if (this.incidences == null) {
            this.computeIncidences();
         }

         int[] coords = this.posToIndex(checkPos);
         return this.getIncidenceAtCoords(coords);
      } else {
         return 15 - dist;
      }
   }

   public boolean isValid(World world) {
      return world.isBlockLoaded(this.pos) && world.getBlockState(this.pos).equals(this.state);
   }

   public final int manhattanDistance(BlockPos target) {
      return Math.abs(this.pos.getX() - target.getX()) + Math.abs(this.pos.getY() - target.getY()) + Math.abs(this.pos.getZ() - target.getZ());
   }

   public void computeIncidences() {
      int size = this.brightness * 2 + 1;
      this.incidences = new byte[size][size][size];
      LightSource.Edge[] edges = new LightSource.Edge[size * size * 2];
      LightSource.Edge[] edges2 = new LightSource.Edge[edges.length];

      LightSource.Edge[] tempEdges;
      for(edges[0] = new LightSource.Edge(this.pos, this.brightness); edges[0] != null; edges2 = tempEdges) {
         int newIndex = 0;

         for(LightSource.Edge edge : edges) {
            if (edge == null) {
               break;
            }

            for(EnumFacing face : EnumFacing.VALUES) {
               LightSource.Edge next = edge.next(this.world, face);
               if (next != null) {
                  int[] coords = this.posToIndex(next.pos);
                  byte curr = this.getIncidenceAtCoords(coords);
                  if (next.light > curr) {
                     this.pushByteToCoords(coords, next.light);
                     if (newIndex >= edges2.length) {
                        return;
                     }

                     edges2[newIndex++] = next;
                  }
               }
            }
         }

         tempEdges = edges;
         edges2[newIndex] = null;
         edges = edges2;
      }

   }

   public int[] posToIndex(BlockPos target) {
      int dX = this.pos.getX() - target.getX() + this.brightness;
      int dY = this.pos.getY() - target.getY() + this.brightness;
      int dZ = this.pos.getZ() - target.getZ() + this.brightness;
      return new int[]{dX, dY, dZ};
   }

   public byte getIncidenceAtCoords(int[] coords) {
      return this.incidences[coords[0]][coords[1]][coords[2]];
   }

   public void pushByteToCoords(int[] coords, byte b) {
      this.incidences[coords[0]][coords[1]][coords[2]] = b;
   }

   public boolean equals(Object obj) {
      return obj == this || this.pos.equals(obj) || obj != null && obj instanceof LightSource && ((LightSource)obj).pos.equals(this.pos);
   }

   private static class Edge {
      public final BlockPos pos;
      public final byte light;

      public Edge(BlockPos pos, byte light) {
         this.pos = pos;
         this.light = light;
      }

      public LightSource.Edge next(IBlockAccess world, EnumFacing face) {
         BlockPos nextPos = this.pos.offset(face);
         byte opacity = (byte)world.getBlockState(nextPos).getLightOpacity(world, nextPos);
         if (opacity < 0) {
            return null;
         } else {
            byte nextLight = (byte)(this.light - opacity - 1);
            return nextLight <= 0 ? null : new LightSource.Edge(nextPos, nextLight);
         }
      }
   }
}
