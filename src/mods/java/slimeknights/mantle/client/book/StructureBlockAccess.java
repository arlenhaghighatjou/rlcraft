package slimeknights.mantle.client.book;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class StructureBlockAccess implements IBlockAccess {
   private final StructureInfo data;
   private final IBlockState[][][] structure;

   public StructureBlockAccess(StructureInfo data) {
      this.data = data;
      this.structure = data.data;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return null;
   }

   public int getCombinedLight(BlockPos pos, int lightValue) {
      return 15728880;
   }

   public IBlockState getBlockState(BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (y >= 0 && y < this.structure.length && x >= 0 && x < this.structure[y].length && z >= 0 && z < this.structure[y][x].length) {
         int index = y * this.data.structureLength * this.data.structureWidth + x * this.data.structureWidth + z;
         if (index <= this.data.getLimiter()) {
            return this.structure[y][x][z] != null ? this.structure[y][x][z] : Blocks.AIR.getDefaultState();
         }
      }

      return Blocks.AIR.getDefaultState();
   }

   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock() == Blocks.AIR;
   }

   public Biome getBiome(BlockPos pos) {
      return null;
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return 0;
   }

   public WorldType getWorldType() {
      return null;
   }

   public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
      return false;
   }
}
