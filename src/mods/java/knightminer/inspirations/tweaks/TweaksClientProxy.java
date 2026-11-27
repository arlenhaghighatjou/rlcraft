package knightminer.inspirations.tweaks;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.NameStateMapper;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.recipes.RecipesClientProxy;
import knightminer.inspirations.tweaks.block.BlockBetterFlowerPot;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.client.PortalColorHandler;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent.Block;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class TweaksClientProxy extends ClientProxy {
   private static final ResourceLocation CARPET_MODEL = Util.getResource("carpet");
   private static final ResourceLocation CAULDRON_ITEM_MODEL = Util.getResource("cauldron_item");
   private static final ResourceLocation ENCHANTED_BOOK = Util.getResource("enchanted_book");
   private static final ResourceLocation FIREWORKS = Util.getResource("fireworks");
   private static final ResourceLocation PORTAL = Util.getResource("portal");
   private static final ModelResourceLocation FLOWER_POT_MODEL = new ModelResourceLocation(Util.getResource("flower_pot"), "normal");

   public TweaksClientProxy() {
   }

   public void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, this::registerModels);
      bus.register(Block.class, this::registerBlockColors);
      bus.register(Item.class, this::registerItemColors);
      bus.register(ModelBakeEvent.class, this::onModelBake);
      bus.register(Load.class, this::onWorldLoad);
      bus.register(Unload.class, this::onWorldUnload);
   }

   public void registerModels(ModelRegistryEvent event) {
      setModelStateMapper(InspirationsTweaks.carpet, new PropertyStateMapper(CARPET_MODEL, BlockCarpet.COLOR, new IProperty[]{BlockFittedCarpet.NORTHWEST, BlockFittedCarpet.NORTHEAST, BlockFittedCarpet.SOUTHWEST, BlockFittedCarpet.SOUTHEAST}));
      setModelStateMapper(InspirationsTweaks.flowerPot, new TweaksClientProxy.FlowerPotStateMapper());
      if (Config.customPortalColor) {
         setModelStateMapper(Blocks.PORTAL, new NameStateMapper(PORTAL, new IProperty[0]));
      }

      registerItemModel(InspirationsTweaks.cactusSeeds);
      registerItemModel(InspirationsTweaks.carrotSeeds);
      registerItemModel(InspirationsTweaks.potatoSeeds);
      registerItemModel(InspirationsTweaks.sugarCaneSeeds);
      if (Config.betterCauldronItem) {
         ResourceLocation model = CAULDRON_ITEM_MODEL;
         if (PulseBase.isRecipesLoaded() && Config.enableExtendedCauldron) {
            model = RecipesClientProxy.CAULDRON_MODEL;
         }

         registerItemModel(Items.CAULDRON, 0, model);
      }

      if (Config.coloredEnchantedRibbons) {
         registerItemModel(Items.ENCHANTED_BOOK, ENCHANTED_BOOK);
      }

      if (Config.coloredFireworkItems) {
         registerItemModel(Items.FIREWORKS, 0, FIREWORKS);
      }

   }

   public void registerBlockColors(Block event) {
      BlockColors colors = event.getBlockColors();
      if (Config.betterFlowerPot) {
         registerBlockColors(colors, (state, world, pos, index) -> {
            if (world != null && pos != null) {
               TileEntity tileentity = world.getTileEntity(pos);
               if (tileentity instanceof TileEntityFlowerPot) {
                  ItemStack stack = ((TileEntityFlowerPot)tileentity).getFlowerItemStack();
                  return ClientUtil.getStackBlockColorsSafe(stack, world, pos, 0);
               }
            }

            return -1;
         }, new net.minecraft.block.Block[]{Blocks.FLOWER_POT});
      }

      if (Config.customPortalColor) {
         registerBlockColors(colors, PortalColorHandler.INSTANCE, new net.minecraft.block.Block[]{Blocks.PORTAL});
      }

      registerBlockColors(colors, (state, world, pos, index) -> {
         return world != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(world, pos) : -1;
      }, new net.minecraft.block.Block[]{InspirationsTweaks.sugarCaneCrop});
   }

   public void registerItemColors(Item event) {
      ItemColors itemColors = event.getItemColors();
      if (Config.coloredEnchantedRibbons) {
         registerItemColors(itemColors, (stack, tintIndex) -> {
            if (tintIndex == 0) {
               Rarity rarity = Rarity.COMMON;

               for(NBTBase tag : ItemEnchantedBook.getEnchantments(stack)) {
                  if (tag.getId() == 10) {
                     int id = ((NBTTagCompound)tag).getShort("id");
                     Enchantment enchantment = Enchantment.getEnchantmentByID(id);
                     if (enchantment != null) {
                        Rarity newRarity = enchantment.getRarity();
                        if (newRarity != null && newRarity.getWeight() < rarity.getWeight()) {
                           rarity = newRarity;
                        }
                     }
                  }
               }

               switch(rarity) {
               case COMMON:
                  return 16720209;
               case UNCOMMON:
                  return 14845997;
               case RARE:
                  return 65313;
               case VERY_RARE:
                  return 10452991;
               }
            }

            return -1;
         }, new net.minecraft.item.Item[]{Items.ENCHANTED_BOOK});
      }

      if (Config.coloredFireworkItems) {
         registerItemColors(itemColors, (stack, tintIndex) -> {
            NBTTagCompound nbt = stack.getSubCompound("Fireworks");
            if (tintIndex == 2) {
               if (nbt != null && nbt.hasKey("Flight", 99)) {
                  byte flight = nbt.getByte("Flight");
                  switch(flight) {
                  case 1:
                     return 8421504;
                  case 2:
                     return 6316128;
                  case 3:
                     return 3158064;
                  default:
                     if (flight > 3) {
                        return 0;
                     }
                  }
               }

               return 10526880;
            } else if (tintIndex != 0 && tintIndex != 1) {
               return -1;
            } else {
               int missing = tintIndex == 1 ? 13410704 : 12632256;
               if (nbt == null) {
                  return missing;
               } else {
                  NBTTagList stars = nbt.getTagList("Explosions", 10);
                  if (tintIndex >= stars.tagCount()) {
                     return missing;
                  } else {
                     NBTTagCompound star = stars.getCompoundTagAt(tintIndex);
                     int[] colors = star.getIntArray("Colors");
                     return colors.length > 0 ? colors[0] : missing;
                  }
               }
            }
         }, new net.minecraft.item.Item[]{Items.FIREWORKS});
      }

   }

   public void onModelBake(ModelBakeEvent event) {
      if (InspirationsTweaks.flowerPot != null) {
         replaceTexturedModel(event, FLOWER_POT_MODEL, "plant", false);
      }

   }

   public void onWorldLoad(Load event) {
      if (Config.customPortalColor) {
         World world = event.getWorld();
         int dimension = world.provider.getDimension();
         if (dimension == DimensionType.OVERWORLD.getId() || dimension == DimensionType.NETHER.getId()) {
            world.addEventListener(PortalColorHandler.INSTANCE);
         }

      }
   }

   public void onWorldUnload(Unload event) {
      if (Config.customPortalColor) {
         event.getWorld().removeEventListener(PortalColorHandler.INSTANCE);
      }
   }

   private static class FlowerPotStateMapper extends StateMapperBase {
      private FlowerPotStateMapper() {
      }

      protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
         if (((Boolean)state.getValue(BlockBetterFlowerPot.EXTRA)).booleanValue()) {
            return TweaksClientProxy.FLOWER_POT_MODEL;
         } else {
            LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
            map.remove(BlockBetterFlowerPot.EXTRA);
            map.remove(BlockBetterFlowerPot.LEGACY_DATA);
            return new ModelResourceLocation(state.getBlock().getRegistryName(), this.getPropertyString(map));
         }
      }
   }
}
