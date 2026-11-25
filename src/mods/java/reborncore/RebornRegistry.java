package reborncore;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;
import reborncore.common.LootManager;
import reborncore.common.util.RebornCraftingHelper;

public class RebornRegistry {
   public static LootManager.InnerPool lp = new LootManager.InnerPool();

   public RebornRegistry() {
   }

   public static void registerBlock(Block block, String name) {
      block.setRegistryName(name);
      GameData.register_impl(block);
      ItemBlock itemBlock = new ItemBlock(block);
      itemBlock.setRegistryName(block.getRegistryName());
      GameData.register_impl(itemBlock);
   }

   public static void registerBlock(Block block, ResourceLocation name) {
      block.setRegistryName(name);
      ItemBlock itemBlock = new ItemBlock(block);
      itemBlock.setRegistryName(block.getRegistryName());
      GameData.register_impl(itemBlock);
      GameData.register_impl(block);
   }

   public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass, String name) {
      block.setRegistryName(name);
      GameData.register_impl(block);

      try {
         ItemBlock itemBlock = (ItemBlock)itemclass.getConstructor(Block.class).newInstance(block);
         itemBlock.setRegistryName(name);
         GameData.register_impl(itemBlock);
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException var4) {
         var4.printStackTrace();
      }

   }

   public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass, ResourceLocation name) {
      block.setRegistryName(name);
      GameData.register_impl(block);

      try {
         ItemBlock itemBlock = (ItemBlock)itemclass.getConstructor(Block.class).newInstance(block);
         itemBlock.setRegistryName(name);
         GameData.register_impl(itemBlock);
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException var4) {
         var4.printStackTrace();
      }

   }

   public static void registerBlock(Block block, ItemBlock itemBlock, ResourceLocation name) {
      block.setRegistryName(name);
      GameData.register_impl(block);
      itemBlock.setRegistryName(name);
      GameData.register_impl(itemBlock);
   }

   public static void registerBlockNoItem(Block block, ResourceLocation name) {
      block.setRegistryName(name);
      GameData.register_impl(block);
   }

   public static void registerBlock(Block block) {
      GameData.register_impl(block);
      ItemBlock itemBlock = new ItemBlock(block);
      itemBlock.setRegistryName(block.getRegistryName());
      GameData.register_impl(itemBlock);
   }

   public static void registerItem(Item item) {
      GameData.register_impl(item);
   }

   public static void registerItem(Item item, ResourceLocation name) {
      item.setRegistryName(name);
      GameData.register_impl(item);
   }

   public static void addShapedOreRecipe(ItemStack output, Object parm) {
      RebornCraftingHelper.addShapedOreRecipe(output, parm);
   }

   public static void addShapelessOreRecipe(ItemStack output, Object parm) {
      RebornCraftingHelper.addShapelessOreRecipe(output, parm);
   }

   public static void addLoot(Item item, double chance, ResourceLocation list) {
      lp.addItem(LootManager.createLootEntry(item, chance, list));
   }

   public static void addLoot(Item item, int minSize, int maxSize, double chance, ResourceLocation list) {
      lp.addItem(LootManager.createLootEntry(item, minSize, maxSize, chance, list));
   }

   public static void addLoot(Item item, int ordinal, int minSize, int maxSize, double chance, ResourceLocation list) {
      lp.addItem(LootManager.createLootEntry(item, ordinal, minSize, maxSize, chance, list));
   }

   @SideOnly(Side.CLIENT)
   public static void registerItemModel(Item i, int meta) {
      ResourceLocation loc = i.getRegistryName();
      ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "inventory"));
   }

   @SideOnly(Side.CLIENT)
   public static void registerItemModel(Block b, int meta) {
      registerItemModel(Item.getItemFromBlock(b), meta);
   }

   @SideOnly(Side.CLIENT)
   public static void registerItemModel(Item i, int meta, String variant) {
      ResourceLocation loc = i.getRegistryName();
      ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "type=" + variant));
   }
}
