package svenhjol.charm.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import svenhjol.charm.automation.feature.GunpowderBlock;
import svenhjol.meson.iface.IMesonBlock;

public class BlockGunpowder extends BlockFalling implements IMesonBlock {
   protected String name = "gunpowder_block";

   public BlockGunpowder() {
      this.register(this.name);
      this.setSoundType(SoundType.SAND);
      this.setHardness(GunpowderBlock.hardness);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHarvestLevel("shovel", 0);
   }

   public String getName() {
      return this.name;
   }

   public String getModId() {
      return "charm";
   }

   protected boolean tryTouchLava(World world, BlockPos pos, IBlockState state) {
      boolean flag = false;

      for(EnumFacing facing : EnumFacing.values()) {
         if (facing != EnumFacing.DOWN) {
            BlockPos blockPos = pos.offset(facing);
            if (world.getBlockState(blockPos).getMaterial() == Material.LAVA) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
         world.setBlockToAir(pos);
      }

      return flag;
   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
      if (!this.tryTouchLava(world, pos, state)) {
         super.neighborChanged(state, world, pos, block, fromPos);
      }

   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (!this.tryTouchLava(world, pos, state)) {
         super.onBlockAdded(world, pos, state);
      }

   }
}
