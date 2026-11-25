package com.mujmajnkraft.bettersurvival.init;

import com.mujmajnkraft.bettersurvival.blocks.BlockCustomCauldron;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModBlocks {
   public static Block customcauldron;

   public ModBlocks() {
   }

   @SubscribeEvent
   public void registerBlocks(Register<Block> event) {
      customcauldron = new BlockCustomCauldron();
      customcauldron.setRegistryName("customcauldron");
      customcauldron.setUnlocalizedName("cauldron");
      event.getRegistry().register(customcauldron);
   }

   public static void registerRenders() {
      registerRender(customcauldron);
   }

   private static void registerRender(Block block) {
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
   }
}
