package com.ferreusveritas.dynamictrees.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.models.bakedmodels.BakedModelBlockBonsaiPot;
import com.ferreusveritas.dynamictrees.models.bakedmodels.BakedModelBlockRooty;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelBakeEventListener {
   public ModelBakeEventListener() {
   }

   @SideOnly(Side.CLIENT)
   public void register(EventBus bus) {
      bus.register(ModelBakeEvent.class, this::onModelBakeEvent);
   }

   public void onModelBakeEvent(ModelBakeEvent event) {
      Block[] rootyBlocks = new Block[]{ModBlocks.blockRootyDirt, ModBlocks.blockRootyDirtSpecies, ModBlocks.blockRootySand, ModBlocks.blockRootyDirtFake};

      for(Block block : rootyBlocks) {
         IBakedModel rootsObject = (IBakedModel)event.getModelRegistry().getObject(new ModelResourceLocation(block.getRegistryName(), "normal"));
         if (rootsObject instanceof IBakedModel) {
            BakedModelBlockRooty rootyModel = new BakedModelBlockRooty(rootsObject);
            event.getModelRegistry().putObject(new ModelResourceLocation(block.getRegistryName(), "normal"), rootyModel);
         }
      }

      IBakedModel flowerPotObject = (IBakedModel)event.getModelRegistry().getObject(new ModelResourceLocation(ModBlocks.blockBonsaiPot.getRegistryName(), "normal"));
      if (flowerPotObject instanceof IBakedModel) {
         BakedModelBlockBonsaiPot bonsaiPotModel = new BakedModelBlockBonsaiPot(flowerPotObject);
         event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.blockBonsaiPot.getRegistryName(), "normal"), bonsaiPotModel);
      }

   }
}
