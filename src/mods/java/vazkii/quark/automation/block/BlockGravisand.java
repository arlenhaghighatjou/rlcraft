package vazkii.quark.automation.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.automation.entity.EntityGravisand;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockGravisand extends BlockMod implements IQuarkBlock {
   public BlockGravisand() {
      super("gravisand", Material.SAND, new String[0]);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.SAND);
      this.setCreativeTab(CreativeTabs.REDSTONE);
   }

   public int tickRate(World worldIn) {
      return 2;
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkRedstone(worldIn, pos);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkRedstone(worldIn, pos);
   }

   private void checkRedstone(World worldIn, BlockPos pos) {
      boolean powered = worldIn.isBlockPowered(pos);
      if (powered) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }

   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return 15;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && this.checkFallable(worldIn, pos)) {
         for(EnumFacing face : EnumFacing.VALUES) {
            BlockPos offPos = pos.offset(face);
            IBlockState offState = worldIn.getBlockState(offPos);
            if (offState.getBlock() == this) {
               worldIn.scheduleUpdate(offPos, this, this.tickRate(worldIn));
            }
         }
      }

   }

   private boolean checkFallable(World worldIn, BlockPos pos) {
      if (!worldIn.isRemote) {
         return this.tryFall(worldIn, pos, EnumFacing.DOWN) ? true : this.tryFall(worldIn, pos, EnumFacing.UP);
      } else {
         return false;
      }
   }

   private boolean tryFall(World worldIn, BlockPos pos, EnumFacing facing) {
      BlockPos target = pos.offset(facing);
      if ((worldIn.isAirBlock(target) || canFallThrough(worldIn.getBlockState(target))) && pos.getY() >= 0) {
         EntityGravisand entity = new EntityGravisand(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos), (float)facing.getFrontOffsetY());
         worldIn.spawnEntity(entity);
         return true;
      } else {
         return false;
      }
   }

   public static boolean canFallThrough(IBlockState state) {
      Block block = state.getBlock();
      Material material = state.getMaterial();
      return block == Blocks.WEB || block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
   }
}
