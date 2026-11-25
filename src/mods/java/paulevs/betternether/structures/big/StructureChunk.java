package paulevs.betternether.structures.big;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

public class StructureChunk {
   private int[] arrayID = new int[4096];
   private byte[] arrayMeta = new byte[4096];

   public StructureChunk() {
      for(int i = 0; i < this.arrayID.length; ++i) {
         this.arrayID[i] = -1;
      }

   }

   public StructureChunk(NBTTagCompound tag) {
      this.arrayID = tag.getIntArray("id");
      this.arrayMeta = tag.getByteArray("meta");
   }

   private int getIndex(int x, int y, int z) {
      return (x << 4 | y) << 4 | z;
   }

   public void setBlock(int blockID, byte blockMeta, int x, int y, int z) {
      int index = this.getIndex(x, y, z);
      this.arrayID[index] = blockID;
      this.arrayMeta[index] = blockMeta;
   }

   public IBlockState getBlockState(int x, int y, int z) {
      int index = this.getIndex(x, y, z);
      return this.arrayID[index] == -1 ? null : Block.getBlockById(this.arrayID[index]).getStateFromMeta(this.arrayMeta[index]);
   }

   public void writeNBT(NBTTagCompound tag) {
      tag.setIntArray("id", this.arrayID);
      tag.setByteArray("meta", this.arrayMeta);
   }
}
