package knightminer.inspirations.tools.item;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRedstoneCharger extends Item {
   public ItemRedstoneCharger() {
      this.maxStackSize = 1;
      this.setMaxDamage(120);
      this.setCreativeTab(CreativeTabs.TOOLS);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!InspirationsTools.redstoneCharge.canPlaceBlockAt(world, pos)) {
         pos = pos.offset(facing);
      }

      ItemStack stack = player.getHeldItem(hand);
      if (!player.canPlayerEdit(pos, facing, stack)) {
         return EnumActionResult.FAIL;
      } else {
         if (InspirationsTools.redstoneCharge.canPlaceBlockAt(world, pos)) {
            world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            IBlockState state = InspirationsTools.redstoneCharge.getDefaultState().withProperty(BlockRedstoneCharge.FACING, facing.getOpposite()).withProperty(BlockRedstoneCharge.QUICK, Boolean.valueOf(player.isSneaking()));
            world.setBlockState(pos, state, 11);
         }

         if (player instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
         }

         stack.damageItem(1, player);
         return EnumActionResult.SUCCESS;
      }
   }
}
