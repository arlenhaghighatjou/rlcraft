package noppes.vc.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.vc.blocks.BlockBasicLightable;

public class ItemBlockLight extends ItemBlock {
   public ItemBlockLight(Block block) {
      super(block);
   }

   public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
      boolean bo = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
      if (bo) {
         IBlockState state = world.getBlockState(pos);
         ((BlockBasicLightable)this.block).onPostBlockPlaced(world, pos, state, player, stack, side);
      }

      return bo;
   }
}
