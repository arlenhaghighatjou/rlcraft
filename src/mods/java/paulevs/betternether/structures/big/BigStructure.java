package paulevs.betternether.structures.big;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BigStructure {
   protected HashMap<StructurePos, StructureChunk> data = new HashMap();
   protected int chunksGenerated = 0;
   protected int chunksMax = 0;
   protected BlockPos pos;
   protected int maxHeight = 0;
   protected int minHeight = 0;
   protected int chunkMinX = 0;
   protected int chunkMaxX = 0;
   protected int chunkMinZ = 0;
   protected int chunkMaxZ = 0;
   protected int cx;
   protected int cz;

   public BigStructure(BlockPos pos, int cx, int cz) {
      this.pos = pos;
      this.maxHeight = pos.getY();
      this.minHeight = pos.getY();
      this.cx = cx;
      this.cz = cz;
      this.chunkMinX = pos.getX() >> 4;
      this.chunkMaxX = this.chunkMinX;
      this.chunkMinZ = pos.getZ() >> 4;
      this.chunkMaxZ = this.chunkMinZ;
   }

   public BigStructure(NBTTagCompound structure) {
      this.chunksGenerated = structure.getInteger("chunksGenerated");
      this.chunksMax = structure.getInteger("chunksMax");
      this.maxHeight = structure.getInteger("maxHeight");
      this.minHeight = structure.getInteger("minHeight");
      this.chunkMinX = structure.getInteger("chunkMinX");
      this.chunkMaxX = structure.getInteger("chunkMaxX");
      this.chunkMinZ = structure.getInteger("chunkMinZ");
      this.chunkMaxZ = structure.getInteger("chunkMaxZ");
      this.cx = structure.getInteger("cx");
      this.cz = structure.getInteger("cz");
      int[] pos = structure.getIntArray("pos");
      this.pos = new BlockPos(pos[0], pos[1], pos[2]);
      NBTTagList chunkData = structure.getTagList("chunks", 10);

      for(int i = 0; i < chunkData.tagCount(); ++i) {
         NBTTagCompound chunk = chunkData.getCompoundTagAt(i);
         StructureChunk c = new StructureChunk(chunk);
         pos = chunk.getIntArray("pos");
         this.data.put(new StructurePos(pos[0], pos[1], pos[2]), c);
      }

   }

   protected StructureChunk getStructureChunk(int x, int y, int z) {
      StructurePos pos = new StructurePos(x >> 4, y >> 4, z >> 4);
      if (this.data.containsKey(pos)) {
         return (StructureChunk)this.data.get(pos);
      } else {
         StructureChunk chunk = new StructureChunk();
         this.data.put(pos, chunk);
         return chunk;
      }
   }

   public void setBlock(IBlockState state, BlockPos pos) {
      int id = Block.getIdFromBlock(state.getBlock());
      byte meta = (byte)state.getBlock().getMetaFromState(state);
      this.maxHeight = Math.min(Math.max(this.maxHeight, pos.getY() + this.pos.getY()), 255);
      this.minHeight = Math.max(Math.min(this.minHeight, pos.getY() + this.pos.getY()), 0);
      StructureChunk chunk = this.getStructureChunk(pos.getX(), pos.getY(), pos.getZ());
      chunk.setBlock(id, meta, pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
      this.updateBounds(pos);
   }

   public IBlockState getBlockState(int x, int y, int z) {
      StructureChunk chunk = this.getStructureChunk(x, y, z);
      return chunk.getBlockState(x & 15, y & 15, z & 15);
   }

   public boolean setChunk(World world, int cx, int cz) {
      if (cx >= this.chunkMinX && cx <= this.chunkMaxX && cz >= this.chunkMinZ && cz <= this.chunkMaxZ) {
         int sx = cx << 4 | 8;
         int sz = cz << 4 | 8;

         for(int y = this.minHeight; y <= this.maxHeight; ++y) {
            int py = y - this.pos.getY();

            for(int x = 0; x < 16; ++x) {
               int wx = sx + x;
               int px = wx - this.pos.getX();

               for(int z = 0; z < 16; ++z) {
                  int wz = sz + z;
                  int pz = wz - this.pos.getZ();
                  IBlockState state = this.getBlockState(px, py, pz);
                  if (state != null) {
                     world.setBlockState(new BlockPos(wx, y, wz), state);
                  }
               }
            }
         }

         ++this.chunksGenerated;
         return true;
      } else {
         return false;
      }
   }

   public boolean generationComplete() {
      return this.chunksGenerated >= this.chunksMax;
   }

   public NBTTagCompound toNBT() {
      NBTTagCompound structure = new NBTTagCompound();
      structure.setInteger("chunksGenerated", this.chunksGenerated);
      structure.setInteger("chunksMax", this.chunksMax);
      structure.setInteger("maxHeight", this.maxHeight);
      structure.setInteger("minHeight", this.minHeight);
      structure.setInteger("chunkMinX", this.chunkMinX);
      structure.setInteger("chunkMaxX", this.chunkMaxX);
      structure.setInteger("chunkMinZ", this.chunkMinZ);
      structure.setInteger("chunkMaxZ", this.chunkMaxZ);
      structure.setInteger("cx", this.cx);
      structure.setInteger("cz", this.cz);
      structure.setIntArray("pos", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
      NBTTagList chunkData = new NBTTagList();
      structure.setTag("chunks", chunkData);

      for(StructurePos p : this.data.keySet()) {
         StructureChunk c = (StructureChunk)this.data.get(p);
         NBTTagCompound chunk = new NBTTagCompound();
         chunk.setIntArray("pos", new int[]{p.getX(), p.getY(), p.getZ()});
         c.writeNBT(chunk);
         chunkData.appendTag(chunk);
      }

      return structure;
   }

   private void updateBounds(BlockPos pos) {
      int cx = pos.getX() + this.pos.getX() >> 4;
      int y = pos.getY() + this.pos.getY();
      int cz = pos.getZ() + this.pos.getZ() >> 4;
      this.maxHeight = y > this.maxHeight ? y : this.maxHeight;
      this.minHeight = y < this.minHeight ? y : this.minHeight;
      boolean update = false;
      if (cx < this.chunkMinX) {
         this.chunkMinX = cx;
         update = true;
      }

      if (cx > this.chunkMaxX) {
         this.chunkMaxX = cx;
         update = true;
      }

      if (cz < this.chunkMinZ) {
         this.chunkMinZ = cz;
         update = true;
      }

      if (cz > this.chunkMaxZ) {
         this.chunkMaxZ = cz;
         update = true;
      }

      if (update) {
         this.chunksMax = (this.chunkMaxX - this.chunkMinX + 1) * (this.chunkMaxZ - this.chunkMinZ + 1);
      }

   }
}
