package com.bloodnbonesgaming.lib.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class OnItemRightClickEvent extends Event {
   public final World world;
   public final EntityPlayer player;
   public final ItemStack itemStack;
   public final EnumHand hand;
   public EnumActionResult result;

   public OnItemRightClickEvent(World world, EntityPlayer player, ItemStack itemStack, EnumHand hand) {
      this.world = world;
      this.player = player;
      this.itemStack = itemStack;
      this.hand = hand;
      this.result = EnumActionResult.FAIL;
   }

   public static ActionResult<ItemStack> useItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      OnItemRightClickEvent event = new OnItemRightClickEvent(world, player, itemStack, hand);
      return MinecraftForge.EVENT_BUS.post(event) ? new ActionResult(event.result, itemStack) : itemStack.getItem().onItemRightClick(world, player, hand);
   }
}
