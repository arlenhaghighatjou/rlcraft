package vazkii.arl.util;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IExtraVariantHolder;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.interf.IModBlock;
import vazkii.arl.interf.IVariantHolder;
import vazkii.arl.item.ItemMod;

@SideOnly(Side.CLIENT)
public final class ModelHandler {
   public static final HashMap<String, ModelResourceLocation> resourceLocations = new HashMap();

   public ModelHandler() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, ModelHandler::onRegister);
      bus.register(Item.class, ModelHandler::onItemColorRegister);
      bus.register(net.minecraftforge.client.event.ColorHandlerEvent.Block.class, ModelHandler::onBlockColorRegister);
   }

   public static void onRegister(ModelRegistryEvent event) {
      for(IVariantHolder holder : ItemMod.variantHolders) {
         registerModels(holder);
      }

   }

   public static void onItemColorRegister(Item event) {
      Iterator var1 = ItemMod.variantHolders.iterator();

      while(true) {
         IVariantHolder holder;
         net.minecraft.item.Item item;
         while(true) {
            if (!var1.hasNext()) {
               return;
            }

            holder = (IVariantHolder)var1.next();
            if (holder instanceof IItemColorProvider) {
               if (holder instanceof Block) {
                  item = net.minecraft.item.Item.getItemFromBlock((Block)holder);
                  break;
               }

               if (holder instanceof net.minecraft.item.Item) {
                  item = (net.minecraft.item.Item)holder;
                  break;
               }
            }
         }

         if (item != Items.AIR) {
            event.getItemColors().registerItemColorHandler(((IItemColorProvider)holder).getItemColor(), new net.minecraft.item.Item[]{item});
         }
      }
   }

   public static void onBlockColorRegister(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
      Iterator var1 = ItemMod.variantHolders.iterator();

      while(true) {
         Block block;
         while(true) {
            if (!var1.hasNext()) {
               return;
            }

            IVariantHolder holder = (IVariantHolder)var1.next();
            if (holder instanceof IBlockColorProvider) {
               if (holder instanceof ItemBlock) {
                  block = ((ItemBlock)holder).getBlock();
                  break;
               }

               if (holder instanceof Block) {
                  block = (Block)holder;
                  break;
               }
            }
         }

         event.getBlockColors().registerBlockColorHandler(((IBlockColorProvider)block).getBlockColor(), new Block[]{block});
      }
   }

   public static void registerModels(IVariantHolder holder) {
      if (holder instanceof net.minecraft.item.Item) {
         String unique = holder.getUniqueModel();
         String prefix = holder.getPrefix();
         net.minecraft.item.Item i = (net.minecraft.item.Item)holder;
         ItemMeshDefinition def = holder.getCustomMeshDefinition();
         if (def != null) {
            ModelLoader.setCustomMeshDefinition((net.minecraft.item.Item)holder, def);
         } else {
            registerModels(i, prefix, holder.getVariants(), unique, false);
         }

         if (holder instanceof IExtraVariantHolder) {
            IExtraVariantHolder extra = (IExtraVariantHolder)holder;
            registerModels(i, prefix, extra.getExtraVariants(), unique, true);
         }
      } else if (holder instanceof Block) {
         registerBlock((Block)holder);
      }

   }

   public static void registerBlock(Block block) {
      IModBlock quarkBlock = (IModBlock)block;
      IStateMapper mapper = quarkBlock.getStateMapper();
      IProperty[] ignored = quarkBlock.getIgnoredProperties();
      if (mapper != null || ignored != null && ignored.length > 0) {
         if (mapper != null) {
            ModelLoader.setCustomStateMapper(block, mapper);
         } else {
            ModelLoader.setCustomStateMapper(block, (new Builder()).ignore(ignored).build());
         }
      }

   }

   public static void registerModels(net.minecraft.item.Item item, String prefix, String[] variants, String uniqueVariant, boolean extra) {
      if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof IModBlock) {
         IModBlock modBlock = (IModBlock)((ItemBlock)item).getBlock();
         if (modBlock.useBlockstateForItem()) {
            Class clazz = modBlock.getVariantEnum();
            if (clazz != null) {
               IProperty variantProp = modBlock.getVariantProp();
               boolean variantIgnored = false;
               IStateMapper mapper = modBlock.getStateMapper();
               IProperty[] ignored = modBlock.getIgnoredProperties();
               if (mapper == null && ignored != null && ignored.length > 0) {
                  for(IProperty p : ignored) {
                     if (p == variantProp) {
                        variantIgnored = true;
                        break;
                     }
                  }
               }

               if (!variantIgnored) {
                  registerVariantsDefaulted(item, (Block)modBlock, clazz, variantProp.getName());
                  return;
               }
            }
         }
      }

      for(int i = 0; i < variants.length; ++i) {
         String var = variants[i];
         if (!extra && uniqueVariant != null) {
            var = uniqueVariant;
         }

         String name = prefix + var;
         ModelResourceLocation loc = new ModelResourceLocation(name, "inventory");
         if (!extra) {
            ModelLoader.setCustomModelResourceLocation(item, i, loc);
            resourceLocations.put(getKey(item, i), loc);
         } else {
            ModelBakery.registerItemVariants(item, new ResourceLocation[]{loc});
            resourceLocations.put(variants[i], loc);
         }
      }

   }

   private static <T extends Enum<T> & IStringSerializable> void registerVariantsDefaulted(net.minecraft.item.Item item, Block b, Class<T> enumclazz, String variantHeader) {
      String baseName = ((ResourceLocation)Objects.requireNonNull(b.getRegistryName())).toString();

      for(T e : enumclazz.getEnumConstants()) {
         String variantName = variantHeader + "=" + ((IStringSerializable)e).getName();
         ModelResourceLocation loc = new ModelResourceLocation(baseName, variantName);
         int i = e.ordinal();
         ModelLoader.setCustomModelResourceLocation(item, i, loc);
         resourceLocations.put(getKey(item, i), loc);
      }

   }

   public static ModelResourceLocation getModelLocation(ItemStack stack) {
      return !stack.isEmpty() ? null : getModelLocation(stack.getItem(), stack.getItemDamage());
   }

   public static ModelResourceLocation getModelLocation(net.minecraft.item.Item item, int meta) {
      String key = getKey(item, meta);
      return resourceLocations.containsKey(key) ? (ModelResourceLocation)resourceLocations.get(key) : null;
   }

   private static String getKey(net.minecraft.item.Item item, int meta) {
      return "i_" + item.getRegistryName() + "@" + meta;
   }
}
