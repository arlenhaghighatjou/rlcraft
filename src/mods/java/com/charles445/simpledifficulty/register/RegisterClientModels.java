package com.charles445.simpledifficulty.register;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.block.IBlockStateIgnore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.relauncher.Side;

public class RegisterClientModels {
   public static final RegisterClientModels instance = new RegisterClientModels();

   public RegisterClientModels() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, RegisterClientModels::onModelRegistryEvent);
   }

   public static void onModelRegistryEvent(ModelRegistryEvent event) {
      for(String key : SDFluids.fluidBlocks.keySet()) {
         instance.registerFluidModel((BlockFluidBase)SDFluids.fluidBlocks.get(key));
      }

      for(String key : SDItems.items.keySet()) {
         instance.registerItemModel((Item)SDItems.items.get(key));
      }

      for(String key : SDBlocks.blocks.keySet()) {
         instance.tweakBlockModelState((Block)SDBlocks.blocks.get(key));
         instance.registerBlockItemModel(Item.getItemFromBlock((Block)SDBlocks.blocks.get(key)), ((Block)SDBlocks.blocks.get(key)).getUnlocalizedName());
      }

   }

   private void tweakBlockModelState(Block block) {
      if (block instanceof IBlockStateIgnore) {
         ModelLoader.setCustomStateMapper(block, (new Builder()).ignore(((IBlockStateIgnore)block).getIgnoredProperties()).build());
      }

   }

   private void registerBlockItemModel(Item item, String blockUnloc) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(blockUnloc.substring(5), "inventory"));
   }

   private void registerFluidModel(BlockFluidBase block) {
      final ModelResourceLocation model = new ModelResourceLocation("simpledifficulty:fluids", block.getFluid().getName());
      Item item = Item.getItemFromBlock(block);
      ModelLoader.setCustomMeshDefinition(item, (meshDefinition) -> {
         return model;
      });
      ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
         protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return model;
         }
      });
   }

   private void registerItemModel(Item item) {
      this.registerSingleItemModel(item, item.getRegistryName().toString());
      if (item.getHasSubtypes()) {
         item.addPropertyOverride(new ResourceLocation("simpledifficulty", "type"), new RegisterClientModels.MetadataPropertyGetter());
      }

   }

   private void registerSingleItemModel(Item item, String path) {
      final ModelResourceLocation fullModelLocation = new ModelResourceLocation(path, "inventory");
      ModelBakery.registerItemVariants(item, new ResourceLocation[]{fullModelLocation});
      ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
         public ModelResourceLocation getModelLocation(ItemStack stack) {
            return fullModelLocation;
         }
      });
   }

   public class MetadataPropertyGetter implements IItemPropertyGetter {
      public MetadataPropertyGetter() {
      }

      public float apply(ItemStack stack, World world, EntityLivingBase entity) {
         return (float)stack.getMetadata();
      }
   }
}
