package com.endertech.minecraft.forge.world;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.data.INBTSerializable;
import com.endertech.minecraft.forge.data.INetSerializable;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkLoc implements INetSerializable<ChunkLoc>, INBTSerializable<ChunkLoc> {
   public static final ChunkLoc ZERO = new ChunkLoc(0, new ChunkPos(0, 0));
   private final int dimensionId;
   private final ChunkPos pos;
   private final ChunkBounds bounds;

   public ChunkLoc(int dimensionId, ChunkPos pos) {
      this.pos = pos;
      this.dimensionId = dimensionId;
      this.bounds = ChunkBounds.from(pos);
   }

   public ChunkLoc(World world, ChunkPos pos) {
      this(world.provider.getDimension(), pos);
   }

   public static ChunkLoc from(Chunk chunk) {
      return new ChunkLoc(chunk.getWorld(), chunk.getPos());
   }

   public static ChunkLoc fromIntArray(int[] array) {
      int dim = array[0];
      int x = array[1];
      int z = array[2];
      return new ChunkLoc(dim, new ChunkPos(x, z));
   }

   public int getDimensionId() {
      return this.dimensionId;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public ChunkBounds getBounds() {
      return this.bounds;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof ChunkLoc)) {
         return super.equals(obj);
      } else {
         ChunkLoc loc = (ChunkLoc)obj;
         return loc.getDimensionId() == this.getDimensionId() && this.getPos().equals(loc.getPos());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getPos(), this.getDimensionId()});
   }

   public boolean contains(Entity entity) {
      return this.getDimensionId() == entity.dimension && this.getBounds().encloses(entity.getPosition());
   }

   public String toString() {
      return ChunkLoc.class.getSimpleName() + Args.group(Args.get("pos", this.getPos()), Args.get("dimensionId", Integer.valueOf(this.getDimensionId())));
   }

   public ChunkLoc readFrom(ByteBuf buf) {
      return new ChunkLoc(buf.readInt(), new ChunkPos(buf.readInt(), buf.readInt()));
   }

   public ByteBuf writeTo(ByteBuf buf) {
      buf.writeInt(this.getDimensionId());
      buf.writeInt(this.getPos().x);
      buf.writeInt(this.getPos().z);
      return buf;
   }

   public ChunkLoc readFrom(NBTTagCompound compound) {
      int[] array = compound.getIntArray(this.getTagName());
      int dim = array[0];
      int x = array[1];
      int z = array[2];
      return new ChunkLoc(dim, new ChunkPos(x, z));
   }

   public NBTTagCompound writeTo(NBTTagCompound compound) {
      int[] array = new int[]{this.getDimensionId(), this.getPos().x, this.getPos().z};
      compound.setIntArray(this.getTagName(), array);
      return compound;
   }
}
