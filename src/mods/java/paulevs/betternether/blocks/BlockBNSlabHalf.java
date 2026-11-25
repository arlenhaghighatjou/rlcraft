package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBNSlabHalf extends BlockBNSlab {
   private IBlockState doubleSlab;

   public BlockBNSlabHalf(String name, Material material, MapColor color, SoundType sound) {
      super(name + "_slab_half", material, color, sound);
   }

   public boolean isDouble() {
      return false;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      Item handItem = playerIn.getHeldItemMainhand().getItem();
      if (handItem == Item.getItemFromBlock(this) && state.getBlock() == this && state.getValue(HALF) == EnumBlockHalf.TOP && facing == EnumFacing.DOWN) {
         worldIn.setBlockState(pos, this.doubleSlab);
         playerIn.playSound(state.getBlock().getSoundType(state, worldIn, pos, playerIn).getPlaceSound(), 1.0F, 1.0F);
         if (!playerIn.isCreative()) {
            playerIn.getHeldItemMainhand().shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public void setDoubleSlab(Block block) {
      this.doubleSlab = block.getDefaultState();
   }
}
