package com.ferreusveritas.dynamictrees.systems;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiFunction;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class EndpointTracker {
   private final HashMap<EndpointTracker.ChunkLocation, EndpointTracker.ChunkEntry> chunkMap = new HashMap();

   public EndpointTracker() {
   }

   public void addPoint(World world, BlockPos pos) {
      this.getChunkEntry(world, pos.getX() >> 4, pos.getZ() >> 4).addPoint(pos);
   }

   public void remPoint(World world, BlockPos pos) {
      this.getChunkEntry(world, pos.getX() >> 4, pos.getZ() >> 4).remPoint(pos);
   }

   public EndpointTracker.ChunkEntry getChunkEntry(World world, int x, int z) {
      return (EndpointTracker.ChunkEntry)this.chunkMap.compute(new EndpointTracker.ChunkLocation(world, x, z), (k, v) -> {
         return v != null ? v : new EndpointTracker.ChunkEntry(k);
      });
   }

   public static class ChunkEntry {
      public EndpointTracker.ChunkLocation chunkLoc;
      public short[] points;
      private int numPoints;

      public ChunkEntry(EndpointTracker.ChunkLocation chunkPos) {
         this.chunkLoc = chunkPos;
         this.points = new short[32];
      }

      private int findIndex(short pos) {
         return Arrays.binarySearch(this.points, 0, this.numPoints, pos);
      }

      private void insert(short pos) {
         int elem = this.findIndex(pos);
         if (elem < 0) {
            if (this.numPoints + 1 > this.points.length) {
               this.points = Arrays.copyOf(this.points, this.points.length * 2);
            }

            elem = -elem - 1;
            System.arraycopy(this.points, elem, this.points, elem + 1, this.numPoints - elem);
            this.points[elem] = pos;
            ++this.numPoints;
         }

      }

      private void remove(short pos) {
         int elem = this.findIndex(pos);
         if (elem >= 0) {
            int len = this.numPoints - elem - 1;
            if (len > 0) {
               System.arraycopy(this.points, elem + 1, this.points, elem, len);
            }

            --this.numPoints;
         }

      }

      public void addPoint(BlockPos pos) {
         this.insert(this.encode(pos));
      }

      public void remPoint(BlockPos pos) {
         this.remove(this.encode(pos));
      }

      private short encode(BlockPos pos) {
         return (short)((pos.getX() & 15) << 16 | pos.getZ() << 8 | pos.getY() & 15);
      }

      public MutableBlockPos decode(short input, MutableBlockPos pos) {
         return pos.setPos(this.chunkLoc.blockX() + (input >> 16), input & 255, this.chunkLoc.blockZ() + (input >> 8 & 15));
      }

      public int size() {
         return this.numPoints;
      }

      public void getPoint(int index, MutableBlockPos pos) {
         if (index >= 0 && index < this.numPoints) {
            this.decode(this.points[index], pos);
         }

      }

      public NBTTagByteArray saveNBT() {
         ByteBuffer byteBuf = ByteBuffer.allocate(this.numPoints * 2);

         for(int i = 0; i < this.numPoints; ++i) {
            byteBuf.putShort(this.points[i]);
         }

         return new NBTTagByteArray(byteBuf.array());
      }

      public void loadNBT(NBTTagByteArray nbtArray) {
         byte[] bytes = nbtArray.getByteArray();
         this.numPoints = bytes.length / 2;
         if (this.points.length < this.numPoints) {
            this.points = new short[this.numPoints];
         }

         ByteBuffer byteBuf = ByteBuffer.wrap(bytes);

         for(int i = 0; i < this.numPoints; ++i) {
            this.points[i] = byteBuf.getShort();
         }

      }
   }

   public static class ChunkLocation extends Vec3i {
      public ChunkLocation(World world, int x, int z) {
         super(x, world.provider.getDimension(), z);
      }

      public int blockX() {
         return this.getX() << 4;
      }

      public int blockZ() {
         return this.getZ() << 4;
      }
   }
}
