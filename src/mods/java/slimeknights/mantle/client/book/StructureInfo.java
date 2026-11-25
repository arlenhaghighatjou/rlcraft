package slimeknights.mantle.client.book;

import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import slimeknights.mantle.client.book.data.element.BlockData;

public class StructureInfo {
   public IBlockState[][][] data;
   public int blockCount = 0;
   public int[] countPerLevel;
   public int structureHeight = 0;
   public int structureLength = 0;
   public int structureWidth = 0;
   public int showLayer = -1;
   private int blockIndex = 0;
   private int maxBlockIndex;

   public StructureInfo(int length, int height, int width, BlockData[] blockData) {
      this.structureWidth = width;
      this.structureHeight = height;
      this.structureLength = length;
      IBlockState[][][] states = new IBlockState[height][length][width];

      for(int y = 0; y < height; ++y) {
         for(int x = 0; x < length; ++x) {
            for(int z = 0; z < width; ++z) {
               for(BlockData data : blockData) {
                  if (this.inside(x, y, z, data.pos, data.endPos)) {
                     states[y][x][z] = this.convert(data);
                     break;
                  }
               }
            }
         }
      }

      this.data = states;
      this.maxBlockIndex = this.blockIndex = this.structureHeight * this.structureLength * this.structureWidth;
   }

   private IBlockState convert(BlockData data) {
      Block block = Block.getBlockFromName(data.block);
      if (block == null) {
         return Blocks.AIR.getDefaultState();
      } else {
         IBlockState state;
         if (data.state != null && !data.state.isEmpty()) {
            state = block.getDefaultState();

            for(Entry<String, String> entry : data.state.entrySet()) {
               Optional<IProperty<?>> property = state.getPropertyKeys().stream().filter((iProperty) -> {
                  return ((String)entry.getKey()).equals(iProperty.getName());
               }).findFirst();
               if (property.isPresent()) {
                  state = this.setProperty(state, (IProperty)property.get(), (String)entry.getValue());
               }
            }
         } else {
            state = block.getStateFromMeta(data.meta);
         }

         return state;
      }
   }

   private <T extends Comparable<T>> IBlockState setProperty(IBlockState state, IProperty<T> prop, String valueString) {
      com.google.common.base.Optional<T> value = prop.parseValue(valueString);
      if (value.isPresent()) {
         state = state.withProperty(prop, (Comparable)value.get());
      }

      return state;
   }

   private boolean inside(int x, int y, int z, int[] rangeStart, int[] rangeEnd) {
      return x >= rangeStart[0] && x <= rangeEnd[0] && y >= rangeStart[1] && y <= rangeEnd[1] && z >= rangeStart[2] && z <= rangeEnd[2];
   }

   public void setShowLayer(int layer) {
      this.showLayer = layer;
      this.blockIndex = (layer + 1) * this.structureLength * this.structureWidth - 1;
   }

   public void reset() {
      this.blockIndex = this.maxBlockIndex;
   }

   public boolean canStep() {
      int index = this.blockIndex;

      while(true) {
         ++index;
         if (index >= this.maxBlockIndex) {
            return false;
         }

         if (!this.isEmpty(index)) {
            break;
         }
      }

      return true;
   }

   public void step() {
      int start = this.blockIndex;

      while(true) {
         if (++this.blockIndex >= this.maxBlockIndex) {
            this.blockIndex = 0;
         }

         if (!this.isEmpty(this.blockIndex) || this.blockIndex == start) {
            break;
         }
      }

   }

   private boolean isEmpty(int index) {
      int y = index / (this.structureLength * this.structureWidth);
      int r = index % (this.structureLength * this.structureWidth);
      int x = r / this.structureWidth;
      int z = r % this.structureWidth;
      return this.data[y][x][z] == null || this.data[y][x][z].getBlock() == Blocks.AIR;
   }

   public int getLimiter() {
      return this.blockIndex;
   }
}
