package net.mcft.copy.backpacks.item;

import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DyeWashingHandler {
   public DyeWashingHandler() {
   }

   @SubscribeEvent
   public void onPlayerInteractBlock(RightClickBlock event) {
      if (!event.getWorld().isRemote && !event.getEntityPlayer().isSneaking()) {
         ItemStack stack = event.getItemStack();
         if (stack.getItem() instanceof IDyeableItem && ((IDyeableItem)stack.getItem()).canWash(stack) && NbtUtils.has(stack, "display", "color")) {
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            if (state.getBlock() instanceof BlockCauldron) {
               BlockCauldron block = (BlockCauldron)state.getBlock();
               int level = ((Integer)state.getValue(BlockCauldron.LEVEL)).intValue();
               if (level > 0) {
                  NbtUtils.remove(stack, "display", "color");
                  block.setWaterLevel(event.getWorld(), event.getPos(), state, level - 1);
                  event.getEntityPlayer().addStat(StatList.ARMOR_CLEANED);
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
