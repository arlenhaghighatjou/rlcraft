package com.teamacronymcoders.base.client;

import com.teamacronymcoders.base.blocks.IHasBlockColor;
import com.teamacronymcoders.base.items.IHasItemColor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Colors {
   public Colors() {
   }

   public static void registerItemColor(Object object, IHasItemColor itemColor) {
      ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
      if (object instanceof Item) {
         itemColors.registerItemColorHandler(itemColor::getColorFromItemstack, new Item[]{(Item)object});
      } else if (object instanceof Block) {
         itemColors.registerItemColorHandler(itemColor::getColorFromItemstack, new Block[]{(Block)object});
      }

   }

   public static void registerBlockColor(IHasBlockColor blockColor) {
      Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blockColor::colorMultiplier, new Block[]{blockColor.getBlock()});
   }
}
