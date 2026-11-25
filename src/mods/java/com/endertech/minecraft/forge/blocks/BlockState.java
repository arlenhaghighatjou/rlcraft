package com.endertech.minecraft.forge.blocks;

import com.endertech.minecraft.forge.units.IWithMeta;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

@Immutable
public class BlockState implements IWithMeta<BlockState> {
   public static final BlockState EMPTY = from((Block)null, 0);
   public static final BlockState AIR = from(Blocks.AIR.getDefaultState());
   @Nullable
   private final Block block;
   private final int meta;

   protected BlockState(@Nullable Block block, int meta) {
      this.block = block;
      this.meta = meta;
   }

   public static BlockState from(@Nullable Block block, int meta) {
      return new BlockState(block, meta);
   }

   public static BlockState from(IBlockState state) {
      return from(state.getBlock(), getMeta(state));
   }

   public static int getMeta(IBlockState state) {
      return state.getBlock().getMetaFromState(state);
   }

   @Nullable
   public Block getBlock() {
      return this.block;
   }

   public int getMeta() {
      return this.meta;
   }

   public boolean exists() {
      return this.getBlock() != null;
   }

   public boolean isAir() {
      return this.matches(AIR);
   }

   public boolean matches(IBlockState state) {
      return this.matches(from(state));
   }

   public boolean matches(BlockState state) {
      return state.getBlock() == this.getBlock() && UnitId.metasAreMatched(state.getMeta(), this.getMeta());
   }

   public BlockState withMeta(int meta) {
      return from(this.getBlock(), meta);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getBlock(), this.getMeta()});
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof BlockState)) {
         return super.equals(obj);
      } else {
         BlockState state = (BlockState)obj;
         return state.getBlock() == this.getBlock() && state.getMeta() == this.getMeta();
      }
   }

   public String toString() {
      return (this.exists() ? this.getBlock().getUnlocalizedName() : "null") + "@" + this.getMeta();
   }
}
