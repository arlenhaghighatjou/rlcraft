package slimeknights.mantle.config;

import com.google.common.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;
import slimeknights.mantle.configurate.objectmapping.serialize.TypeSerializer;

public class BlockMeta {
   private static final BlockMeta MISSING = new BlockMeta(Blocks.AIR, 0);
   public static final transient TypeSerializer<BlockMeta> SERIALIZER = new TypeSerializer<BlockMeta>() {
      public BlockMeta deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
         String val = configurationNode.getString();
         String[] parts = val.split(":");
         Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
         if (block == Blocks.AIR) {
            return BlockMeta.MISSING;
         } else {
            int meta = -1;
            if (parts.length > 2) {
               meta = Integer.valueOf(parts[2]).intValue();
            }

            return new BlockMeta(block, meta);
         }
      }

      public void serialize(TypeToken<?> typeToken, BlockMeta blockMeta, ConfigurationNode configurationNode) throws ObjectMappingException {
         configurationNode.setValue(blockMeta.toString());
      }
   };
   public Block block;
   public int metadata;

   public BlockMeta() {
   }

   public BlockMeta(Block block, int metadata) {
      this.block = block;
      this.metadata = metadata;
   }

   public static BlockMeta of(IBlockState state) {
      return new BlockMeta(state.getBlock(), state.getBlock().getMetaFromState(state));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlockMeta blockMeta = (BlockMeta)o;
         if (this.metadata != blockMeta.metadata) {
            return false;
         } else {
            return this.block != null ? this.block.equals(blockMeta.block) : blockMeta.block == null;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.block != null ? this.block.hashCode() : 0;
      result = 31 * result + this.metadata;
      return result;
   }

   public String toString() {
      String val = this.block.getRegistryName().toString();
      if (this.metadata > -1) {
         val = val + ":" + this.metadata;
      }

      return val;
   }
}
