package knightminer.inspirations.building;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockEnlightenedBush;
import knightminer.inspirations.building.block.BlockFlower;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.client.BookshelfModel;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.BlockItemStateMapper;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class BuildingClientProxy extends ClientProxy {
   public static final Minecraft mc = Minecraft.getMinecraft();

   public BuildingClientProxy() {
   }

   public void preInit() {
      super.preInit();
      if (Config.enableBookshelf) {
         IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
         if (manager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)manager).registerReloadListener((l) -> {
               BookshelfModel.BOOK_CACHE.invalidateAll();
            });
         } else {
            Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
         }
      }

   }

   public void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, this::registerModels);
      bus.register(net.minecraftforge.client.event.ColorHandlerEvent.Block.class, this::registerBlockColors);
      bus.register(Item.class, this::registerItemColors);
      bus.register(ModelBakeEvent.class, this::onModelBake);
   }

   public void registerModels(ModelRegistryEvent event) {
      setModelStateMapper(InspirationsBuilding.glassDoor, (new Builder()).ignore(new IProperty[]{BlockDoor.POWERED}).build());
      setModelStateMapper(InspirationsBuilding.flower, new BlockItemStateMapper(new IProperty[0]));
      setModelStateMapper(InspirationsBuilding.rope, new PropertyStateMapper(BlockRope.TYPE, new IProperty[0]));
      registerItemMetaDynamic(InspirationsBuilding.books);
      registerItemModel(InspirationsBuilding.glassDoorItem);
      registerItemModel(InspirationsBuilding.glassTrapdoor);
      this.registerRopeModels(InspirationsBuilding.rope);
      registerItemBlockMeta(InspirationsBuilding.mulch);
      registerItemBlockMeta(InspirationsBuilding.path);
      registerItemBlockMeta(InspirationsBuilding.enlightenedBush);
      this.registerFlowerModels(InspirationsBuilding.flower);
      this.registerBookshelfModels(InspirationsBuilding.bookshelf);
   }

   private void registerBookshelfModels(Block bookshelf) {
      if (bookshelf != null) {
         for(BlockBookshelf.BookshelfType type : BlockBookshelf.BookshelfType.values()) {
            registerItemModel(bookshelf, type.getMeta(), "facing=south,type=" + type.getName());
         }
      }

   }

   private void registerRopeModels(Block rope) {
      if (rope != null) {
         for(BlockRope.RopeType type : BlockRope.RopeType.values()) {
            registerItemModel(rope, type.getMeta(), type.getName());
         }
      }

   }

   private void registerFlowerModels(Block flower) {
      if (flower != null) {
         for(BlockFlower.FlowerType type : BlockFlower.FlowerType.values()) {
            registerItemModel(flower, type.getMeta(), "block=false,type=" + type.getName());
         }
      }

   }

   public void registerBlockColors(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
      BlockColors blockColors = event.getBlockColors();
      registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
         if (state.getValue(BlockBookshelf.TYPE) == BlockBookshelf.BookshelfType.NORMAL && tintIndex > 0 && tintIndex <= 14) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileBookshelf) {
               ItemStack stack = ((TileBookshelf)te).getStackInSlot(tintIndex - 1);
               if (!stack.isEmpty()) {
                  int color = ClientUtil.getStackColor(stack);
                  int itemColors = mc.getItemColors().colorMultiplier(stack, 0);
                  if (itemColors > -1) {
                     color = Util.combineColors(color, itemColors, 3);
                  }

                  return color;
               }
            }
         }

         return -1;
      }, new Block[]{InspirationsBuilding.bookshelf});
      registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
         if (state.getValue(BlockRope.TYPE) == BlockRope.RopeType.VINE) {
            return world != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(world, pos) : ColorizerFoliage.getFoliageColorBasic();
         } else {
            return -1;
         }
      }, new Block[]{InspirationsBuilding.rope});
      registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
         if (tintIndex != 0) {
            return -1;
         } else {
            int color = ((BlockEnlightenedBush.LightsType)state.getValue(BlockEnlightenedBush.LIGHTS)).getColor();
            if (color > -1) {
               return color;
            } else {
               TileEntity te = world.getTileEntity(pos);
               if (te != null) {
                  ItemStack stack = new ItemStack(TextureBlockUtil.getTextureBlock(te));
                  return ClientUtil.getStackBlockColorsSafe(stack, world, pos, 0);
               } else {
                  return ColorizerFoliage.getFoliageColorBasic();
               }
            }
         }
      }, new Block[]{InspirationsBuilding.enlightenedBush});
   }

   public void registerItemColors(Item event) {
      ItemColors itemColors = event.getItemColors();
      registerItemColors(itemColors, (stack, tintIndex) -> {
         return BlockBookshelf.BookshelfType.fromMeta(stack.getMetadata()) == BlockBookshelf.BookshelfType.NORMAL && tintIndex > 0 && tintIndex <= 14 ? 6638359 : -1;
      }, new Block[]{InspirationsBuilding.bookshelf});
      registerItemColors(itemColors, (stack, tintIndex) -> {
         int meta = stack.getItemDamage();
         return tintIndex == 0 && meta < 16 ? EnumDyeColor.byMetadata(meta).getColorValue() : -1;
      }, new net.minecraft.item.Item[]{InspirationsBuilding.books});
      registerItemColors(itemColors, (stack, tintIndex) -> {
         if (tintIndex != 0) {
            return -1;
         } else {
            int color = BlockEnlightenedBush.LightsType.fromMeta(stack.getMetadata()).getColor();
            if (color > -1) {
               return color;
            } else {
               ItemStack textureStack = TextureBlockUtil.getStackTexture(stack);
               return !textureStack.isEmpty() && textureStack.getItem() != net.minecraft.item.Item.getItemFromBlock(InspirationsBuilding.enlightenedBush) ? itemColors.colorMultiplier(textureStack, 0) : ColorizerFoliage.getFoliageColorBasic();
            }
         }
      }, new Block[]{InspirationsBuilding.enlightenedBush});
      registerItemColors(itemColors, (stack, tintIndex) -> {
         return ClientUtil.getStackBlockColors(stack, (IBlockAccess)null, (BlockPos)null, tintIndex);
      }, new Block[]{InspirationsBuilding.rope});
   }

   public void onModelBake(ModelBakeEvent event) {
      if (InspirationsBuilding.bookshelf != null) {
         ResourceLocation bookshelfLoc = InspirationsBuilding.bookshelf.getRegistryName();

         for(BlockBookshelf.BookshelfType type : BlockBookshelf.BookshelfType.values()) {
            for(EnumFacing facing : EnumFacing.HORIZONTALS) {
               replaceBookshelfModel(event, new ModelResourceLocation(bookshelfLoc, HString.format("facing=%s,type=%s", facing.getName(), type.getName())));
            }
         }
      }

      if (InspirationsBuilding.enlightenedBush != null) {
         ResourceLocation location = InspirationsBuilding.enlightenedBush.getRegistryName();

         for(BlockEnlightenedBush.LightsType type : BlockEnlightenedBush.LightsType.values()) {
            replaceTexturedModel(event, new ModelResourceLocation(location, HString.format("lights=%s", type.getName())), "leaves", true);
         }
      }

   }

   private static void replaceBookshelfModel(ModelBakeEvent event, ModelResourceLocation location) {
      IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
      IBakedModel standard = (IBakedModel)event.getModelRegistry().getObject(location);
      IBakedModel finalModel = new BookshelfModel(standard, model, DefaultVertexFormats.BLOCK);
      event.getModelRegistry().putObject(location, finalModel);
   }
}
