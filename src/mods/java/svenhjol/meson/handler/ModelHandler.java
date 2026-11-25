package svenhjol.meson.handler;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.meson.iface.IMesonBlock;
import svenhjol.meson.iface.IMesonItem;
import svenhjol.meson.registry.ProxyRegistry;

public final class ModelHandler {
   public ModelHandler() {
   }

   @SubscribeEvent
   public static void onRegister(ModelRegistryEvent event) {
      ProxyRegistry.items.forEach(ModelHandler::registerModels);
      ProxyRegistry.blocks.forEach(ModelHandler::registerBlockModels);
   }

   @SubscribeEvent
   public static void onItemColorRegister(Item event) {
      for(net.minecraft.item.Item item : ProxyRegistry.items) {
         if (item instanceof IMesonItem.IItemColorHandler) {
            IItemColor col = ((IMesonItem.IItemColorHandler)item).getItemColor();
            event.getItemColors().registerItemColorHandler(col, new net.minecraft.item.Item[]{item});
         }
      }

   }

   public static void registerModels(net.minecraft.item.Item item) {
      int meta = 0;
      if (item instanceof IMesonItem.IItemCustomModel) {
         ((IMesonItem.IItemCustomModel)item).registerModels(item);
      } else {
         ModelResourceLocation loc = new ModelResourceLocation((ResourceLocation)Objects.requireNonNull(item.getRegistryName()), "inventory");
         ModelLoader.setCustomModelResourceLocation(item, meta, loc);
      }

      if (item instanceof IMesonItem.IItemVariants) {
         List<ResourceLocation> variants = ((IMesonItem.IItemVariants)item).getVariants();

         for(int i = 0; i < variants.size(); ++i) {
            ModelResourceLocation loc = new ModelResourceLocation((ResourceLocation)variants.get(i), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, i, loc);
         }

         ModelLoader.registerItemVariants(item, (ResourceLocation[])variants.toArray(new ResourceLocation[0]));
      }

      if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof IMesonBlock) {
         IMesonBlock block = (IMesonBlock)((ItemBlock)item).getBlock();
         String[] variants = block.getVariants();
         String name = block.getModId() + ":" + block.getName();
         if (variants.length > 1) {
            for(int i = 0; i < variants.length; ++i) {
               String variant = variants[i];
               ModelResourceLocation inv = new ModelResourceLocation(name, "inventory,variant=" + variant);
               ModelLoader.setCustomModelResourceLocation(item, i, inv);
            }
         } else {
            ModelResourceLocation loc = new ModelResourceLocation(name, "inventory");
            ModelLoader.setCustomModelResourceLocation(item, meta, loc);
         }

         if (block instanceof IMesonBlock.IHasCustomStateMapper) {
            ((IMesonBlock.IHasCustomStateMapper)block).setStateMapper();
         }

         ModelResourceLocation loc = new ModelResourceLocation((ResourceLocation)Objects.requireNonNull(item.getRegistryName()), "inventory");
         ModelLoader.setCustomModelResourceLocation(item, meta, loc);
      }

   }

   public static void registerBlockModels(Block block) {
      if (block instanceof IMesonBlock.IHasCustomStateMapper) {
         ((IMesonBlock.IHasCustomStateMapper)block).setStateMapper();
      }

      if (block instanceof IMesonBlock.IHasCustomItemBlockModel) {
         ((IMesonBlock.IHasCustomItemBlockModel)block).setInventoryItemModel();
      } else {
         int meta = 0;
         net.minecraft.item.Item item = net.minecraft.item.Item.getItemFromBlock(block);
         ModelResourceLocation loc = new ModelResourceLocation((ResourceLocation)Objects.requireNonNull(item.getRegistryName()), "inventory");
         ModelLoader.setCustomModelResourceLocation(item, meta, loc);
      }

   }
}
