package ivorius.ivtoolkit.random;

import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public class BlurredValueField implements NBTCompoundObject, BlurrablePivot {
   public static final int MAX_VALUES_PER_CHUNK = 10;
   public static final int CHUNKS_SPLIT = 2;
   private List<BlurredValueField.Value> values = new ArrayList();
   private int[] chunkCount;
   private BlurredValueField[] chunks;
   private int[] offset;
   private int[] size;
   private int[] center;
   private int[] neighborP1;
   private int[] neighborP2;
   private double weight;
   private double average;

   public BlurredValueField() {
   }

   public BlurredValueField(int... size) {
      this.setBounds(new int[size.length], size);
   }

   public BlurredValueField(int[] offset, int[] size) {
      this.setBounds(offset, size);
   }

   public static double getValue(Collection<? extends BlurrablePivot> values, int[] position) {
      int idx = 0;
      double total = 0.0D;
      double[] invDist = new double[values.size()];

      for(BlurrablePivot value : values) {
         double dist = 0.0D;

         for(int j = 0; j < position.length; ++j) {
            double d = (double)(position[j] - value.pos()[j]);
            dist += d * d;
         }

         dist = dist * dist;
         dist = dist * dist;
         if (dist <= 1.0E-4D) {
            return value.value();
         }

         invDist[idx] = 1.0D / dist * value.weight();
         total += invDist[idx++];
      }

      double retVal = 0.0D;
      total = 1.0D / total;
      idx = 0;

      for(BlurrablePivot value : values) {
         retVal += value.value() * invDist[idx++] * total;
      }

      return retVal;
   }

   public boolean almostContains(int[] pos) {
      for(int d = 0; d < pos.length; ++d) {
         if (pos[d] < this.neighborP1[d] || pos[d] >= this.neighborP2[d]) {
            return false;
         }
      }

      return true;
   }

   protected void setBounds(int[] chunkOffset, int[] chunkSize) {
      this.offset = chunkOffset;
      this.size = chunkSize;
      this.center = new int[this.offset.length];

      for(int d = 0; d < this.offset.length; ++d) {
         this.center[d] = this.offset[d] + this.size[d] / 2;
      }

      this.neighborP1 = new int[this.offset.length];
      this.neighborP2 = new int[this.offset.length];

      for(int d = 0; d < this.offset.length; ++d) {
         this.neighborP1[d] = this.offset[d] - this.size[d];
         this.neighborP2[d] = this.offset[d] + this.size[d] * 2;
      }

   }

   public int[] getSize() {
      return (int[])this.size.clone();
   }

   public int[] getOffset() {
      return (int[])this.offset.clone();
   }

   public boolean addValue(BlurredValueField.Value value) {
      if (value.pos.length != this.size.length) {
         throw new IllegalArgumentException();
      } else {
         if (this.chunks != null) {
            if (!this.getChunk(value.pos).addValue(value)) {
               return false;
            }
         } else {
            if (this.values.stream().anyMatch((v) -> {
               return Arrays.equals(v.pos, value.pos);
            })) {
               return false;
            }

            this.values.add(value);
            if (this.values.size() > 10) {
               this.split();
            }
         }

         this.average = this.calculateAverage();
         this.weight += value.weight();
         return true;
      }
   }

   protected double calculateAverage() {
      return this.chunks != null ? Arrays.stream(this.chunks).mapToDouble((c) -> {
         return c.average;
      }).sum() / (double)this.chunks.length : this.values.stream().mapToDouble((c) -> {
         return c.value;
      }).sum() / (double)this.values.size();
   }

   protected double calculateWeight() {
      return this.chunks != null ? Arrays.stream(this.chunks).mapToDouble(BlurredValueField::weight).sum() : (double)this.values.size();
   }

   protected BlurredValueField getChunk(int[] pos) {
      int[] chunkPos = this.getChunkPos(pos);
      return !this.hasChunk(chunkPos) ? null : this.chunks[this.chunkToIndex(chunkPos)];
   }

   private boolean hasChunk(int[] chunkPos) {
      for(int d = 0; d < chunkPos.length; ++d) {
         if (chunkPos[d] < 0 || chunkPos[d] >= this.chunkCount[d]) {
            return false;
         }
      }

      return true;
   }

   protected int[] getChunkPos(int[] pos) {
      int[] chunkPos = new int[pos.length];

      for(int d = 0; d < pos.length; ++d) {
         chunkPos[d] = (pos[d] - this.offset[d]) * this.chunkCount[d] / this.size[d];
      }

      return chunkPos;
   }

   private int chunkToIndex(int[] chunkPos) {
      int index = 0;

      for(int d = 0; d < chunkPos.length; ++d) {
         index = index * this.chunkCount[d];
         index = index + chunkPos[d];
      }

      return index;
   }

   protected int[] chunkFromIndex(int index) {
      int[] chunkPos = new int[this.size.length];

      for(int i = chunkPos.length - 1; i >= 0; --i) {
         chunkPos[i] = index % this.chunkCount[i];
         index /= this.chunkCount[i];
      }

      return chunkPos;
   }

   protected void split() {
      int max = 1;
      this.chunkCount = new int[this.size.length];

      for(int d = 0; d < this.size.length; ++d) {
         this.chunkCount[d] = Math.min(this.size[d], 2);
         max *= this.chunkCount[d];
      }

      if (max > 1) {
         this.chunks = new BlurredValueField[max];

         for(int c = 0; c < this.chunks.length; ++c) {
            int[] chunkPos = this.chunkFromIndex(c);
            int[] chunkOffset = new int[this.size.length];
            int[] chunkSize = new int[this.size.length];

            for(int d = 0; d < this.size.length; ++d) {
               chunkOffset[d] = this.calculateChunkOffset(d, chunkPos[d]);
               int nextOffset = this.calculateChunkOffset(d, chunkPos[d] + 1);
               chunkSize[d] = nextOffset - chunkOffset[d];
            }

            this.chunks[c] = new BlurredValueField(chunkOffset, chunkSize);
         }
      }

      for(BlurredValueField.Value value : this.values) {
         this.getChunk(value.pos).addValue(value);
      }

      this.values.clear();
   }

   private int calculateChunkOffset(int d, int chunkPos) {
      int roundUpCorrection = this.chunkCount[d] - 1;
      return this.offset[d] + (this.size[d] * chunkPos + roundUpCorrection) / this.chunkCount[d];
   }

   public boolean addValue(double value, Random random) {
      int[] pos = new int[this.size.length];

      for(int i = 0; i < pos.length; ++i) {
         pos[i] = random.nextInt(this.size[i]) + this.offset[i];
      }

      return this.addValue(new BlurredValueField.Value(value, pos));
   }

   public double getValue(int... position) {
      List<BlurrablePivot> relevant = new ArrayList();
      this.addRelevantValues(position, relevant);
      return getValue(relevant, position);
   }

   protected void addRelevantValues(int[] position, List<BlurrablePivot> rv) {
      if (this.chunks != null) {
         for(BlurredValueField chunk : this.chunks) {
            if (chunk.almostContains(position)) {
               chunk.addRelevantValues(position, rv);
            } else {
               rv.add(chunk);
            }
         }
      } else {
         rv.addAll(this.values);
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      this.size = compound.getIntArray("size");
      this.offset = compound.getIntArray("offset");
      this.values.addAll(NBTCompoundObjects.readListFrom(compound, "values", BlurredValueField.Value.class));
      if (compound.hasKey("chunks")) {
         this.chunks = (BlurredValueField[])NBTCompoundObjects.readListFrom(compound, "chunks", BlurredValueField::new).stream().toArray((x$0) -> {
            return new BlurredValueField[x$0];
         });
         this.chunkCount = compound.getIntArray("chunkCount");
      }

      this.weight = this.calculateWeight();
      this.average = this.calculateAverage();
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setIntArray("size", this.size);
      compound.setIntArray("offset", this.offset);
      NBTCompoundObjects.writeListTo(compound, "values", this.values);
      if (this.chunks != null) {
         NBTCompoundObjects.writeListTo(compound, "chunks", this.values);
         compound.setIntArray("chunkCount", this.chunkCount);
      }

   }

   public double value() {
      return this.average;
   }

   public double weight() {
      return this.weight;
   }

   public int[] pos() {
      return this.center;
   }

   public static class Value implements NBTCompoundObject, BlurrablePivot {
      private double value;
      @Nullable
      private Double weight;
      private int[] pos;

      public Value() {
      }

      public Value(double value, int[] pos) {
         this.value = value;
         this.pos = pos;
      }

      public Value(double value, Double weight, int[] pos) {
         this.value = value;
         this.weight = weight;
         this.pos = pos;
      }

      public double getActiveWeight() {
         return this.weight != null ? this.weight.doubleValue() : 1.0D;
      }

      public void readFromNBT(NBTTagCompound compound) {
         this.value = compound.getDouble("value");
         this.weight = compound.hasKey("weight") ? compound.getDouble("weight") : null;
         this.pos = compound.getIntArray("pos");
      }

      public void writeToNBT(NBTTagCompound compound) {
         compound.setDouble("value", this.value);
         if (this.weight != null) {
            compound.setDouble("weight", this.weight.doubleValue());
         }

         compound.setIntArray("pos", this.pos);
      }

      public double value() {
         return this.value;
      }

      public double weight() {
         return this.weight != null ? this.weight.doubleValue() : 1.0D;
      }

      public int[] pos() {
         return this.pos;
      }
   }
}
