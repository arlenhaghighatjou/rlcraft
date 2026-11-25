package ivorius.reccomplex.world.gen.feature.structure.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.utils.FMLRemapper;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MCRegistrySpecial implements MCRegistry {
   public static final String HIDDEN_ITEM_TAG = "RC_HIDDEN_ITEM";
   private static final Item DUMMY_ITEM = Items.COAL;
   protected final BiMap<ResourceLocation, Item> itemMap = HashBiMap.create();
   protected final BiMap<ResourceLocation, Block> blockMap = HashBiMap.create();
   protected final Map<ResourceLocation, Class<? extends TileEntity>> tileEntityMap = new HashMap();
   protected MCRegistry parent;
   protected FMLRemapper remapper;
   protected MCRegistrySpecial.ItemHidingRegistry itemHidingRegistry = new MCRegistrySpecial.ItemHidingRegistry();

   public MCRegistrySpecial(MCRegistry parent, FMLRemapper remapper) {
      this.parent = parent;
      this.remapper = remapper;
   }

   public void register(ResourceLocation id, Item item) {
      this.itemMap.put(id, item);
   }

   public void register(ResourceLocation id, Block block) {
      this.blockMap.put(id, block);
   }

   public void register(ResourceLocation id, Class<? extends TileEntity> tileEntity) {
      this.tileEntityMap.put(id, tileEntity);
   }

   public MCRegistrySpecial.ItemHidingRegistry itemHidingMode() {
      return this.itemHidingRegistry;
   }

   public Item itemFromID(ResourceLocation itemID) {
      itemID = this.remapper.mapItem(itemID);
      Item item = (Item)this.itemMap.get(itemID);
      return item != null ? item : this.parent.itemFromID(itemID);
   }

   public ResourceLocation idFromItem(Item item) {
      ResourceLocation id = (ResourceLocation)this.itemMap.inverse().get(item);
      return id != null ? id : this.parent.idFromItem(item);
   }

   public void modifyItemStackCompound(NBTTagCompound compound, ResourceLocation itemID) {
      this.parent.modifyItemStackCompound(compound, itemID);
   }

   public boolean isSafe(Item item) {
      return this.itemMap.isEmpty() || !this.itemMap.containsValue(item);
   }

   public boolean isItemSafe(ResourceLocation itemID) {
      return this.itemMap.isEmpty() || !this.itemMap.containsKey(itemID);
   }

   public Block blockFromID(ResourceLocation blockID) {
      blockID = this.remapper.mapBlock(blockID);
      Block block = (Block)this.blockMap.get(blockID);
      return block != null ? block : this.parent.blockFromID(blockID);
   }

   public ResourceLocation idFromBlock(Block block) {
      ResourceLocation id = (ResourceLocation)this.blockMap.inverse().get(block);
      return id != null ? id : this.parent.idFromBlock(block);
   }

   public boolean isSafe(Block block) {
      return this.blockMap.isEmpty() || !this.blockMap.containsValue(block);
   }

   public boolean isBlockSafe(ResourceLocation blockID) {
      return this.blockMap.isEmpty() || !this.blockMap.containsKey(blockID);
   }

   public TileEntity loadTileEntity(World world, NBTTagCompound compound) {
      try {
         Class oclass = (Class)this.tileEntityMap.get(this.remapper.mapTileEntity(new ResourceLocation(compound.getString("id"))));
         if (oclass != null) {
            TileEntity tileEntity = (TileEntity)oclass.newInstance();
            tileEntity.readFromNBT(compound);
            return tileEntity;
         }
      } catch (Exception var5) {
         RecurrentComplex.logger.error("Error loading special TileEntity", var5);
      }

      return this.parent.loadTileEntity(world, compound);
   }

   public boolean isSafe(TileEntity tileEntity) {
      return this.tileEntityMap.isEmpty() || !this.tileEntityMap.containsValue(tileEntity.getClass());
   }

   public class ItemHidingRegistry implements MCRegistry {
      public ItemHidingRegistry() {
      }

      public Item itemFromID(ResourceLocation itemID) {
         itemID = MCRegistrySpecial.this.remapper.mapItem(itemID);
         return !MCRegistrySpecial.this.isItemSafe(itemID) ? MCRegistrySpecial.DUMMY_ITEM : MCRegistrySpecial.this.parent.itemFromID(itemID);
      }

      public ResourceLocation idFromItem(Item item) {
         return MCRegistrySpecial.this.idFromItem(item);
      }

      public ResourceLocation containedItemID(ItemStack stack) {
         return MCRegistrySpecial.this.idFromItem(this.containedItem(stack));
      }

      public Item containedItem(ItemStack stack) {
         Item hidden = this.hiddenItem(stack);
         return hidden != null ? hidden : stack.getItem();
      }

      @Nullable
      public Item hiddenItem(ItemStack stack) {
         return stack.hasTagCompound() && stack.getTagCompound().hasKey("RC_HIDDEN_ITEM", 8) ? MCRegistrySpecial.this.itemFromID(new ResourceLocation(stack.getTagCompound().getString("RC_HIDDEN_ITEM"))) : null;
      }

      public ItemStack constructItemStack(ResourceLocation itemID, int stackSize, int metadata) {
         return this.constructItemStack(MCRegistrySpecial.this.itemFromID(itemID), stackSize, metadata);
      }

      public ItemStack constructItemStack(Item item, int stackSize, int metadata) {
         ResourceLocation hiddenID = (ResourceLocation)MCRegistrySpecial.this.itemMap.inverse().get(item);
         if (hiddenID != null) {
            ItemStack stack = new ItemStack(MCRegistrySpecial.DUMMY_ITEM, stackSize, metadata);
            stack.setTagInfo("RC_HIDDEN_ITEM", new NBTTagString(hiddenID.toString()));
            return stack;
         } else {
            return new ItemStack(item, stackSize, metadata);
         }
      }

      public void modifyItemStackCompound(NBTTagCompound compound, ResourceLocation itemID) {
         Item item = (Item)MCRegistrySpecial.this.itemMap.get(MCRegistrySpecial.this.remapper.mapItem(itemID));
         if (item != null) {
            NBTTagCompound stackNBT;
            if (compound.hasKey("tag", 10)) {
               stackNBT = compound.getCompoundTag("tag");
            } else {
               compound.setTag("tag", stackNBT = new NBTTagCompound());
            }

            stackNBT.setString("RC_HIDDEN_ITEM", itemID.toString());
            compound.setString("id", (itemID = this.idFromItem(MCRegistrySpecial.DUMMY_ITEM)).toString());
         }

         MCRegistrySpecial.this.parent.modifyItemStackCompound(compound, itemID);
      }

      public Block blockFromID(ResourceLocation blockID) {
         return MCRegistrySpecial.this.blockFromID(blockID);
      }

      public ResourceLocation idFromBlock(Block block) {
         return MCRegistrySpecial.this.idFromBlock(block);
      }

      public TileEntity loadTileEntity(World world, NBTTagCompound compound) {
         return MCRegistrySpecial.this.loadTileEntity(world, compound);
      }
   }
}
