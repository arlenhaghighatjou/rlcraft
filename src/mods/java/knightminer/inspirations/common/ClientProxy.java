package knightminer.inspirations.common;

import javax.annotation.Nonnull;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;

public class ClientProxy extends CommonProxy {
   public static final String VARIANT_INVENTORY = "inventory";

   public ClientProxy() {
   }

   public void preInit() {
      super.preInit();
      MinecraftForge.EVENT_BUS.register(this);
   }

   protected static ResourceLocation registerItemModel(Block block) {
      return registerItemModel(Item.getItemFromBlock(block));
   }

   protected static ResourceLocation registerItemModel(Item item) {
      ResourceLocation itemLocation = null;
      if (item != null) {
         itemLocation = item.getRegistryName();
      }

      if (itemLocation != null) {
         itemLocation = registerItemModel(item, itemLocation);
      }

      return itemLocation;
   }

   protected static ResourceLocation registerItemModel(Item item, final ResourceLocation location) {
      if (item != null && location != null) {
         ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
            @Nonnull
            public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
               return new ModelResourceLocation(location, "inventory");
            }
         });
         ModelLoader.registerItemVariants(item, new ResourceLocation[]{location});
         return location;
      } else {
         return location;
      }
   }

   public static void registerItemModel(Item item, int meta) {
      registerItemModel(item, meta, "inventory");
   }

   public static void registerItemModel(Block block, int meta, String variant) {
      if (block != null) {
         registerItemModel(Item.getItemFromBlock(block), meta, block.getRegistryName(), variant);
      }

   }

   public static void registerItemModel(Item item, int meta, String variant) {
      if (item != null) {
         registerItemModel(item, meta, item.getRegistryName(), variant);
      }

   }

   public static void registerItemModel(Block block, int meta, ResourceLocation location) {
      if (block != null) {
         registerItemModel(Item.getItemFromBlock(block), meta, location);
      }

   }

   public static void registerItemModel(Item item, int meta, ResourceLocation location) {
      if (item != null) {
         registerItemModel(item, meta, location, "inventory");
      }

   }

   public static void registerItemModel(Block block, int meta, ResourceLocation location, String variant) {
      if (block != null && !StringUtils.isNullOrEmpty(variant)) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(location, variant));
      }

   }

   public static void registerItemModel(Item item, int meta, ResourceLocation location, String variant) {
      if (item != null && !StringUtils.isNullOrEmpty(variant)) {
         ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, variant));
      }

   }

   public static void registerItemBlockMeta(Block block) {
      if (block != null) {
         Item item = Item.getItemFromBlock(block);
         ((ItemBlockMeta)item).registerItemModels();
      }

   }

   public static void registerItemMetaDynamic(ItemMetaDynamic item) {
      if (item != null) {
         item.registerItemModels();
      }

   }

   public static void registerItemMetaDynamic(ItemEdible item) {
      if (item != null) {
         item.registerItemModels();
      }

   }

   public static void registerFluidModels(Fluid fluid) {
      if (fluid != null) {
         Block block = fluid.getBlock();
         if (block != null) {
            Item item = Item.getItemFromBlock(block);
            ClientProxy.FluidStateMapper mapper = new ClientProxy.FluidStateMapper(fluid);
            if (item != Items.AIR) {
               ModelLoader.registerItemVariants(item, new ResourceLocation[0]);
               ModelLoader.setCustomMeshDefinition(item, mapper);
            }

            ModelLoader.setCustomStateMapper(block, mapper);
         }

      }
   }

   public static void registerFluidTexture(TextureMap map, Fluid fluid) {
      if (fluid != null) {
         map.registerSprite(fluid.getFlowing());
         map.registerSprite(fluid.getStill());
      }

   }

   protected static void setModelStateMapper(Block block, IStateMapper mapper) {
      if (block != null) {
         ModelLoader.setCustomStateMapper(block, mapper);
      }

   }

   protected static void registerBlockColors(BlockColors blockColors, IBlockColor handler, Block... blocks) {
      for(Block block : blocks) {
         if (block != null) {
            blockColors.registerBlockColorHandler(handler, new Block[]{block});
         }
      }

   }

   protected static void registerItemColors(ItemColors itemColors, IItemColor handler, Block... blocks) {
      for(Block block : blocks) {
         if (block != null) {
            Item item = Item.getItemFromBlock(block);
            if (item != Items.AIR) {
               itemColors.registerItemColorHandler(handler, new Item[]{item});
            }
         }
      }

   }

   protected static void registerItemColors(ItemColors itemColors, IItemColor handler, Item... items) {
      for(Item item : items) {
         if (item != null) {
            itemColors.registerItemColorHandler(handler, new Item[]{item});
         }
      }

   }

   protected static void replaceTexturedModel(ModelBakeEvent event, ModelResourceLocation location, String key, boolean item) {
      IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
      IBakedModel standard = (IBakedModel)event.getModelRegistry().getObject(location);
      IBakedModel finalModel = new TextureModel(standard, model, DefaultVertexFormats.BLOCK, key, item);
      event.getModelRegistry().putObject(location, finalModel);
   }

   private static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {
      public final ModelResourceLocation location;

      public FluidStateMapper(Fluid fluid) {
         this.location = new ModelResourceLocation(Util.getResource("fluid_block"), fluid.getName());
      }

      @Nonnull
      protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
         return this.location;
      }

      @Nonnull
      public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
         return this.location;
      }
   }
}
