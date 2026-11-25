package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.item.ItemCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.alcatrazcore.util.OreDictionaryHelper;
import com.alcatrazescapee.notreepunching.common.blocks.BlockRock;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRock extends ItemCore {
   private static Map<Stone, ItemRock> MAP = new HashMap();
   private final Stone type;

   public static ItemRock get(Stone type) {
      return (ItemRock)MAP.get(type);
   }

   public ItemRock(Stone type) {
      this.type = type;
      MAP.put(type, this);
      OreDictionaryHelper.register(this, new Object[]{"rock"});
      OreDictionaryHelper.register(this, new Object[]{"rock", type.name()});
   }

   @Nonnull
   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState stateUnder = worldIn.getBlockState(pos);
      IBlockState stateAt = worldIn.getBlockState(pos.up());
      if (stateUnder.isNormalCube() && stateAt.getBlock().isReplaceable(worldIn, pos.up())) {
         if (!worldIn.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            player.setHeldItem(hand, CoreHelpers.consumeItem(player, stack));
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockState(pos.up(), BlockRock.get(this.type).getDefaultState());
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }
}
