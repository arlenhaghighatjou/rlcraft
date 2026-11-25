package shadows.placebo.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PlaceboUtil {
   public PlaceboUtil() {
   }

   public static void sMRL(Item k, int meta, String variant) {
      ModelLoader.setCustomModelResourceLocation(k, meta, new ModelResourceLocation(k.getRegistryName(), variant));
   }

   public static void sMRL(Block k, int meta, String variant) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(k), meta, new ModelResourceLocation(k.getRegistryName(), variant));
   }

   public static void sMRL(String statePath, Block k, int meta, String variant) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(k), meta, new ModelResourceLocation(k.getRegistryName().getResourceDomain() + ":" + statePath, variant));
   }

   public static void sMRL(String statePath, Item k, int meta, String variant) {
      ModelLoader.setCustomModelResourceLocation(k, meta, new ModelResourceLocation(k.getRegistryName().getResourceDomain() + ":" + statePath, variant));
   }

   public static void sMRL(String domain, String statePath, Block k, int meta, String variant) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(k), meta, new ModelResourceLocation(domain + ":" + statePath, variant));
   }

   public static void sMRL(String domain, String statePath, Item k, int meta, String variant) {
      ModelLoader.setCustomModelResourceLocation(k, meta, new ModelResourceLocation(domain + ":" + statePath, variant));
   }

   public static boolean isOwnedBy(IForgeRegistryEntry<?> thing, String owner) {
      return thing.getRegistryName().getResourceDomain().equals(owner);
   }

   public static Item getItemByName(String regname) {
      return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(regname));
   }

   public static Item[] getItemsByNames(String... regnames) {
      Item[] items = new Item[regnames.length];

      for(int i = 0; i < regnames.length; ++i) {
         items[i] = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(regnames[i]));
      }

      return items;
   }

   public static void setRegNameIllegally(IForgeRegistryEntry<?> entry, String name) {
      Loader l = Loader.instance();
      ModContainer k = l.activeModContainer();
      l.setActiveModContainer(l.getMinecraftModContainer());
      entry.setRegistryName(new ResourceLocation("minecraft", name));
      l.setActiveModContainer(k);
   }

   public static NBTTagCompound getStackNBT(ItemStack stack) {
      if (stack.isEmpty()) {
         throw new RuntimeException("Tried to get tag compound from empty stack!  This is a bug!");
      } else if (stack.hasTagCompound()) {
         return stack.getTagCompound();
      } else {
         stack.setTagCompound(new NBTTagCompound());
         return stack.getTagCompound();
      }
   }

   @SafeVarargs
   public static <T> List<T> asList(T... objs) {
      ArrayList<T> list = new ArrayList();

      for(T t : objs) {
         list.add(t);
      }

      return list;
   }

   @SafeVarargs
   public static <T extends IForgeRegistryEntry<?>> List<ItemStack> toStackList(Object... objs) {
      ItemStack[] stacks = new ItemStack[objs.length];

      for(int i = 0; i < objs.length; ++i) {
         stacks[i] = convert(objs[i]);
      }

      return NonNullList.from(ItemStack.EMPTY, stacks);
   }

   @SafeVarargs
   public static <T extends IForgeRegistryEntry<?>> ItemStack[] toStackArray(Object... objs) {
      ItemStack[] stacks = new ItemStack[objs.length];

      for(int i = 0; i < objs.length; ++i) {
         stacks[i] = convert(objs[i]);
      }

      return stacks;
   }

   public static ItemStack convert(Object obj) {
      return obj instanceof ItemStack ? (ItemStack)obj : RecipeHelper.makeStack((IForgeRegistryEntry)obj);
   }

   public static boolean setBlockWithMeta(World world, BlockPos pos, Block block, int meta, int flag) {
      return world.setBlockState(pos, block.getStateFromMeta(meta), flag);
   }
}
