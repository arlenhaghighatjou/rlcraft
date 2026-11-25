package cursedflames.bountifulbaubles.util;

import cursedflames.bountifulbaubles.block.GenericBlock;
import cursedflames.bountifulbaubles.block.GenericTileEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegistryHelper {
   final String modID;
   List<GenericBlock> blocks = new ArrayList();
   List<ItemBlock> itemBlocks = new ArrayList();
   List<Item> items = new ArrayList();
   List<Item> defaultItemModels = new ArrayList();
   List<GenericBlock> defaultItemBlockModels = new ArrayList();
   Map<String, Class<? extends TileEntity>> tileEntities = new HashMap();
   GenericBlock recentBlock = null;
   Item recentItem = null;
   boolean autoaddItemModels = false;
   boolean autoaddItemBlocks = false;
   public boolean useOldTileEntityNaming = true;

   public boolean doesAutoaddItemModels() {
      return this.autoaddItemModels;
   }

   public void setAutoaddItemModels(boolean autoaddItemModels) {
      this.autoaddItemModels = autoaddItemModels;
   }

   public boolean doesAutoaddItemBlocks() {
      return this.autoaddItemBlocks;
   }

   public void setAutoaddItemBlocks(boolean autoaddItemBlocks) {
      this.autoaddItemBlocks = autoaddItemBlocks;
   }

   public RegistryHelper(String modID) {
      this.modID = modID;
   }

   public RegistryHelper addBlock(GenericBlock block) {
      this.blocks.add(block);
      this.recentBlock = block;
      if (this.autoaddItemBlocks) {
         this.addItemBlock(block);
      }

      return this;
   }

   public RegistryHelper addItemBlock() {
      return this.addItemBlock(this.recentBlock);
   }

   public RegistryHelper addItemBlock(GenericBlock block) {
      this.itemBlocks.add((ItemBlock)(new ItemBlock(block)).setRegistryName(block.getRegistryName()));
      return this;
   }

   public RegistryHelper addItemBlock(ItemBlock block) {
      this.itemBlocks.add(block);
      return this;
   }

   public RegistryHelper addItem(Item item) {
      this.items.add(item);
      this.recentItem = item;
      if (this.autoaddItemModels) {
         this.addItemModel(item);
      }

      return this;
   }

   public RegistryHelper addItemModel() {
      return this.addItemModel(this.recentItem);
   }

   public RegistryHelper addItemModel(Item item) {
      this.defaultItemModels.add(item);
      return this;
   }

   public RegistryHelper addItemBlockModel() {
      return this.addItemBlockModel(this.recentBlock);
   }

   public RegistryHelper addItemBlockModel(GenericBlock block) {
      this.defaultItemBlockModels.add(block);
      return this;
   }

   public RegistryHelper addTileEntity(String name, Class<? extends GenericTileEntity> te) {
      this.tileEntities.put(name, te);
      return this;
   }

   public void registerBlocks(Register<Block> event) {
      for(GenericBlock block : this.blocks) {
         event.getRegistry().register(block);
      }

      for(Entry<String, Class<? extends TileEntity>> te : this.tileEntities.entrySet()) {
         GameRegistry.registerTileEntity((Class)te.getValue(), this.modID + (this.useOldTileEntityNaming ? "_" : ":") + "tileentity_" + (String)te.getKey());
      }

   }

   public void registerItems(Register<Item> event) {
      for(Item block : this.itemBlocks) {
         event.getRegistry().register(block);
      }

      for(Item item : this.items) {
         event.getRegistry().register(item);
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      for(GenericBlock block : this.defaultItemBlockModels) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
      }

      for(Item item : this.defaultItemModels) {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      }

   }
}
