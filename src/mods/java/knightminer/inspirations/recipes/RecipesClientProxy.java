package knightminer.inspirations.recipes;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent.Block;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RecipesClientProxy extends ClientProxy {
   private static final ResourceLocation POTION_MODEL = new ResourceLocation("bottle_drinkable");
   public static final ResourceLocation CAULDRON_MODEL = Util.getResource("cauldron");

   public RecipesClientProxy() {
   }

   @SubscribeEvent
   public void registerModels(ModelRegistryEvent event) {
      setModelStateMapper(InspirationsRecipes.cauldron, new RecipesClientProxy.CauldronStateMapper(CAULDRON_MODEL));
      registerItemModel(InspirationsRecipes.potatoSoupItem);
      if (Config.enableCauldronDyeing) {
         registerItemModel(InspirationsRecipes.dyedWaterBottle, POTION_MODEL);
      }

   }

   @SubscribeEvent
   public void registerBlockColors(Block event) {
      BlockColors blockColors = event.getBlockColors();
      registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
         if (tintIndex == 1) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileCauldron) {
               return ((TileCauldron)te).getColor();
            }
         }

         return -1;
      }, new net.minecraft.block.Block[]{InspirationsRecipes.cauldron});
   }

   @SubscribeEvent
   public void registerItemColors(Item event) {
      ItemColors itemColors = event.getItemColors();
      registerItemColors(itemColors, (stack, tintIndex) -> {
         return tintIndex == 0 ? InspirationsRecipes.dyedWaterBottle.getColor(stack) : -1;
      }, new net.minecraft.item.Item[]{InspirationsRecipes.dyedWaterBottle});
   }

   @SubscribeEvent
   public void onModelBake(ModelBakeEvent event) {
      if (InspirationsRecipes.cauldron != null) {
         boolean boiling = false;

         while(true) {
            replaceTexturedModel(event, new ModelResourceLocation(CAULDRON_MODEL, String.format("boiling=%s,contents=fluid,level=empty", boiling)), "water", false);

            for(int i = Config.enableBiggerCauldron ? 0 : 1; i <= 3; ++i) {
               replaceTexturedModel(event, new ModelResourceLocation(CAULDRON_MODEL, String.format("boiling=%s,contents=fluid,level=%s", boiling, i)), "water", false);
            }

            boiling = !boiling;
            if (!boiling) {
               break;
            }
         }

      }
   }

   @SubscribeEvent
   public void registerTextures(Pre event) {
      TextureMap map = event.getMap();
      registerFluidTexture(map, InspirationsRecipes.milk);
   }

   public static class CauldronStateMapper extends StateMapperBase {
      private static final PropertyEnum<RecipesClientProxy.CauldronStateMapper.CauldronLevel> LEVEL = PropertyEnum.create("level", RecipesClientProxy.CauldronStateMapper.CauldronLevel.class);
      private ResourceLocation location;

      public CauldronStateMapper(ResourceLocation location) {
         this.location = location;
      }

      @Nonnull
      protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
         LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
         if (Config.enableBiggerCauldron) {
            map.put(LEVEL, RecipesClientProxy.CauldronStateMapper.CauldronLevel.forLevel(((Integer)state.getValue(BlockEnhancedCauldron.LEVELS)).intValue()));
            map.remove(BlockCauldron.LEVEL);
            map.remove(BlockEnhancedCauldron.LEVELS);
         } else {
            map.put(LEVEL, RecipesClientProxy.CauldronStateMapper.CauldronLevel.forLevel(((Integer)state.getValue(BlockCauldron.LEVEL)).intValue()));
            map.remove(BlockCauldron.LEVEL);
         }

         return new ModelResourceLocation(this.location, this.getPropertyString(map));
      }

      private static enum CauldronLevel implements IStringSerializable {
         EMPTY,
         LEVEL0,
         LEVEL1,
         LEVEL2,
         LEVEL3;

         private CauldronLevel() {
         }

         public static RecipesClientProxy.CauldronStateMapper.CauldronLevel forLevel(int level) {
            if (level < 0 || level > 4) {
               level = 0;
            }

            if (!Config.enableBiggerCauldron && level != 0) {
               ++level;
            }

            return values()[level];
         }

         public String getName() {
            return this == EMPTY ? "empty" : "" + (this.ordinal() - 1);
         }
      }
   }
}
