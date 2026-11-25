package vazkii.quark.tweaks.base;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStack {
   public static final BlockStack EMPTY = new BlockStack(Blocks.AIR);
   private final Block block;
   private final int meta;
   private final IBlockState state;
   private static final Pattern BLOCK_STACK_PATTERN = Pattern.compile("(?:(\\w+):)?(\\w+)(?::(\\d+))?");

   public BlockStack(Block block, int meta) {
      this.block = block;
      this.meta = meta;
      this.state = block.getStateFromMeta(meta);
   }

   public BlockStack(Block block) {
      this(block, 0);
   }

   public static BlockStack fromString(String key) {
      Matcher match = BLOCK_STACK_PATTERN.matcher(key);
      if (!match.matches()) {
         return EMPTY;
      } else {
         String modid = match.group(1);
         String block = match.group(2);
         String metaString = match.group(3);
         int meta = metaString == null ? 0 : Integer.parseInt(metaString);
         ResourceLocation loc = new ResourceLocation(modid, block);
         Block blockInstance = (Block)Block.REGISTRY.getObject(loc);
         return blockInstance == null ? EMPTY : new BlockStack(blockInstance, meta);
      }
   }

   public static BlockStack getStackFromPos(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return new BlockStack(state.getBlock(), state.getBlock().getMetaFromState(state));
   }

   public boolean isEmpty() {
      return this.block == Blocks.AIR;
   }

   public Block getBlock() {
      return this.block;
   }

   public int getMeta() {
      return this.meta;
   }

   public IBlockState getState() {
      return this.state;
   }

   public String toString() {
      return Objects.toString(this.getBlock().getRegistryName()) + ":" + this.getMeta();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlockStack that = (BlockStack)o;
         return Objects.equals(this.state, that.state);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.state});
   }
}
