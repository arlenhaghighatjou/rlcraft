package ivorius.reccomplex.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockGenericSpace extends ItemBlock {
   public ItemBlockGenericSpace(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int damage) {
      return damage;
   }

   public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack stack = playerIn.getHeldItem(hand);
      IBlockState iblockstate = worldIn.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (!block.isReplaceable(worldIn, pos) || block == this.getBlock()) {
         pos = pos.offset(facing);
      }

      if (stack.getCount() != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity)null)) {
         int i = this.getMetadata(stack.getMetadata());
         IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn, EnumHand.MAIN_HAND);
         if (this.placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1)) {
            SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
            worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            stack.shrink(1);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return super.getUnlocalizedName(par1ItemStack) + ".meta." + par1ItemStack.getItemDamage();
   }
}
