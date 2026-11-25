package snownee.kiwi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockModFence extends BlockFence implements IModBlock {
   private final String name;
   private final IBlockState baseState;

   public BlockModFence(String name, IBlockState baseState) {
      super(baseState.getMaterial(), MapColor.AIR);
      this.name = name;
      this.baseState = baseState;
      this.setSoundType(baseState.getBlock().getSoundType());
   }

   public Block cast() {
      return this;
   }

   public String getName() {
      return this.name;
   }

   public void register(String modid) {
      this.setRegistryName(modid, this.getName());
      this.setUnlocalizedName(modid + "." + this.getName());
   }

   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return this.baseState.getMapColor(worldIn, pos);
   }
}
