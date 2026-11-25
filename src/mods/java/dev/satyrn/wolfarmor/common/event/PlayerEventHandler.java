package dev.satyrn.wolfarmor.common.event;

import dev.satyrn.wolfarmor.item.ItemWolfArmor;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class PlayerEventHandler {
   public PlayerEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public void onPlayerInteract(@Nonnull RightClickBlock event) {
      Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
      if (block == Blocks.CAULDRON) {
         ItemStack stack = event.getItemStack();
         this.handleCauldronRightClick(event, stack);
      }

   }

   private void handleCauldronRightClick(@Nonnull RightClickBlock event, @Nonnull ItemStack stack) {
      EntityPlayer player = event.getEntityPlayer();
      World world = event.getWorld();
      if (!world.isRemote && !stack.isEmpty()) {
         IBlockState blockCauldronState = event.getWorld().getBlockState(event.getPos());
         int fillLevel = ((Integer)blockCauldronState.getValue(BlockCauldron.LEVEL)).intValue();
         if (fillLevel > 0 && stack.getItem() instanceof ItemWolfArmor) {
            ItemWolfArmor itemWolfArmor = (ItemWolfArmor)stack.getItem();
            if (itemWolfArmor.getMaterial().getCanBeDyed() && itemWolfArmor.getHasColor(stack)) {
               itemWolfArmor.removeColor(stack);
               Blocks.CAULDRON.setWaterLevel(event.getWorld(), event.getPos(), blockCauldronState, fillLevel - 1);
               player.addStat(StatList.ARMOR_CLEANED);
               event.setResult(Result.ALLOW);
               event.setCanceled(true);
            }
         }
      }

   }
}
