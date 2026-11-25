package net.mcft.copy.backpacks.misc.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class MiscUtils {
   private MiscUtils() {
   }

   public static Block getBlockFromName(ResourceLocation location) {
      return (Block)Block.REGISTRY.getObject(location);
   }

   public static Block getBlockFromName(String name) {
      return getBlockFromName(new ResourceLocation(name));
   }

   public static Block getBlockFromItem(Item item) {
      return getBlockFromName(item.getRegistryName());
   }

   public static Item getItemFromName(ResourceLocation location) {
      return (Item)Item.REGISTRY.getObject(location);
   }

   public static Item getItemFromName(String name) {
      return getItemFromName(new ResourceLocation(name));
   }

   public static Item getItemFromBlock(Block block) {
      return (Item)Item.REGISTRY.getObject(block.getRegistryName());
   }

   public static boolean shouldKeepItem(Entity entity, ItemStack stack) {
      EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
      boolean keepInventory = entity.getEntityWorld().getGameRules().getBoolean("keepInventory");
      return player != null && keepInventory;
   }
}
