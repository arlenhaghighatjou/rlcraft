package com.creativemd.creativecore.client.rendering.model;

import com.creativemd.creativecore.client.CreativeCoreClient;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreativeBlockRenderHelper {
   public static HashMap<ResourceLocation, Block> blocks = new HashMap();
   public static HashMap<ResourceLocation, Item> items = new HashMap();

   public CreativeBlockRenderHelper() {
   }

   public static void registerCreativeRenderedItem(Item item) {
      items.put(item.getRegistryName(), item);
      CreativeCoreClient.registerItemRenderer(item);
   }

   public static void registerCreativeRenderedBlock(Block block) {
      blocks.put(block.getRegistryName(), block);
      CreativeCoreClient.registerBlockItem(block);
   }
}
