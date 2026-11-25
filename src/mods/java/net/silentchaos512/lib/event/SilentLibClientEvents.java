package net.silentchaos512.lib.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.network.internal.MessageLeftClick;

public final class SilentLibClientEvents {
   public SilentLibClientEvents() {
   }

   @SubscribeEvent
   public void onLeftClickEmpty(LeftClickEmpty event) {
      ItemStack stack = event.getItemStack();
      if (!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem) {
         ActionResult<ItemStack> result = ((ILeftClickItem)stack.getItem()).onItemLeftClickSL(event.getWorld(), event.getEntityPlayer(), event.getHand());
         if (result.getType() == EnumActionResult.SUCCESS) {
            SilentLib.network.wrapper.sendToServer(new MessageLeftClick(MessageLeftClick.Type.EMPTY, event.getHand()));
         }
      }

   }

   @SubscribeEvent
   public void onLeftClickBlock(LeftClickBlock event) {
      ItemStack stack = event.getItemStack();
      if (!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem) {
         ActionResult<ItemStack> result = ((ILeftClickItem)stack.getItem()).onItemLeftClickBlockSL(event.getWorld(), event.getEntityPlayer(), event.getHand());
         if (result.getType() == EnumActionResult.SUCCESS) {
            SilentLib.network.wrapper.sendToServer(new MessageLeftClick(MessageLeftClick.Type.BLOCK, event.getHand()));
         }
      }

   }
}
