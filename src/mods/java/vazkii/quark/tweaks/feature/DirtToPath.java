package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.feature.Pickarang;

public class DirtToPath extends Feature {
   public DirtToPath() {
   }

   @SubscribeEvent
   public void onRightClick(RightClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      BlockPos pos = event.getPos();
      EnumFacing facing = event.getFace();
      World world = event.getWorld();
      ItemStack itemstack = player.getHeldItem(event.getHand());
      IBlockState state = world.getBlockState(pos);
      if (itemstack.getItem() != Pickarang.pickarang && (itemstack.getItem().getToolClasses(itemstack).contains("shovel") || itemstack.getDestroySpeed(state) <= 0.0F)) {
         if (facing != null && player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
            Block block = state.getBlock();
            if (facing != EnumFacing.DOWN && world.getBlockState(pos.up()).getMaterial() == Material.AIR && block == Blocks.DIRT) {
               IBlockState pathState = Blocks.GRASS_PATH.getDefaultState();
               world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
               if (!world.isRemote) {
                  world.setBlockState(pos, pathState, 11);
                  itemstack.damageItem(1, player);
               }

               event.setCanceled(true);
               event.setCancellationResult(EnumActionResult.SUCCESS);
            }
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
