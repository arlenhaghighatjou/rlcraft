package com.chaosthedude.realistictorches.proxy;

import com.chaosthedude.realistictorches.blocks.RealisticTorchesBlocks;
import com.chaosthedude.realistictorches.items.RealisticTorchesItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerModels() {
      for(Item item : RealisticTorchesItems.REGISTRY) {
         this.registerModel(item);
      }

      for(Block block : RealisticTorchesBlocks.REGISTRY) {
         this.registerModel(Item.getItemFromBlock(block));
      }

   }

   public void registerModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
   }
}
