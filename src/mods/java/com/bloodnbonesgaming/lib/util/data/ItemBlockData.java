package com.bloodnbonesgaming.lib.util.data;

import com.bloodnbonesgaming.lib.util.JsonToNBTExtended;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.oredict.OreDictionary;

public class ItemBlockData {
   private String location;
   private String stateJson;
   private MinMaxBounds meta = MinMaxBounds.UNBOUNDED;
   public MinMaxBounds count = MinMaxBounds.UNBOUNDED;
   private MinMaxBounds durability = MinMaxBounds.UNBOUNDED;
   private final List<EnchantmentPredicate> enchantmentData = new ArrayList();
   private PotionType potionType;
   private NBTPredicate nbt = NBTPredicate.ANY;
   private String oreDict;
   private NBTPredicate tile = NBTPredicate.ANY;

   public ItemBlockData() {
   }

   public BlockPredicate buildBlockPredicate() throws Exception {
      Block block = null;
      Map<IProperty<?>, Object> map = null;
      if (this.location != null) {
         block = Block.getBlockFromName(this.location);
         if (block == null) {
            throw new Exception(this.location + " is not a valid block resource location.");
         }

         if (this.stateJson != null) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = (JsonObject)parser.parse(this.stateJson);
            BlockStateContainer blockstatecontainer = block.getBlockState();

            for(Entry<String, JsonElement> entry : jsonObj.entrySet()) {
               IProperty<?> iproperty = blockstatecontainer.getProperty((String)entry.getKey());
               if (iproperty == null) {
                  throw new Exception("Unknown block state property '" + (String)entry.getKey() + "'");
               }

               String s = JsonUtils.getString((JsonElement)entry.getValue(), (String)entry.getKey());
               Optional<?> optional = iproperty.parseValue(s);
               if (!optional.isPresent()) {
                  throw new Exception("Invalid block state value '" + s + "' for property '" + (String)entry.getKey() + "'");
               }

               if (map == null) {
                  map = Maps.newHashMap();
               }

               map.put(iproperty, optional.get());
            }
         }
      }

      return new BlockPredicate(block, this.meta, map, this.oreDict, this.tile);
   }

   public IBlockState buildBlockState() throws Exception {
      Block block = null;
      IBlockState state = null;
      if (this.location != null) {
         block = Block.getBlockFromName(this.location);
         if (block == null) {
            throw new Exception(this.location + " is not a valid block resource location.");
         }

         state = this.meta.min != null ? block.getStateFromMeta(this.meta.min.intValue()) : block.getDefaultState();
         if (this.stateJson != null) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = (JsonObject)parser.parse(this.stateJson);
            BlockStateContainer blockstatecontainer = block.getBlockState();

            for(Entry<String, JsonElement> entry : jsonObj.entrySet()) {
               IProperty<?> iproperty = blockstatecontainer.getProperty((String)entry.getKey());
               if (iproperty == null) {
                  throw new Exception("Unknown block state property '" + (String)entry.getKey() + "'");
               }

               String s = JsonUtils.getString((JsonElement)entry.getValue(), (String)entry.getKey());
               Optional<?> optional = iproperty.parseValue(s);
               if (!optional.isPresent()) {
                  throw new Exception("Invalid block state value '" + s + "' for property '" + (String)entry.getKey() + "'");
               }

               state = this.withProperty(state, iproperty, s);
            }
         }
      }

      if (state == null) {
         throw new Exception("State could not be parsed.");
      } else {
         return state;
      }
   }

   private <T extends Comparable<T>> IBlockState withProperty(IBlockState state, IProperty<T> property, String value) {
      return state.withProperty(property, (Comparable)property.parseValue(value).get());
   }

   public ItemPredicate buildItemPredicate() throws Exception {
      Item item = null;
      if (this.location != null) {
         item = Item.getByNameOrId(this.location);
         if (item == null) {
            throw new Exception(this.location + " is not a valid item resource location.");
         }
      }

      return new ExtendedItemPredicate(item, this.meta, this.count, this.durability, (EnchantmentPredicate[])this.enchantmentData.toArray(new EnchantmentPredicate[0]), this.potionType, this.nbt, this.oreDict);
   }

   public ItemStack buildItemStack() throws Exception {
      ItemStack itemStack = null;
      Item item = null;
      if (this.location != null) {
         item = Item.getByNameOrId(this.location);
         if (item == null) {
            throw new Exception(this.location + " is not a valid item resource location.");
         }

         itemStack = new ItemStack(item, this.count.min != null ? this.count.min.intValue() : 1, this.meta.min != null ? this.meta.min.intValue() : 0);
         if (this.nbt != NBTPredicate.ANY) {
            itemStack.setTagCompound(this.nbt.tag);
         }

         for(EnchantmentPredicate pred : this.enchantmentData) {
            if (pred.enchantment != null) {
               this.addEnchantment(itemStack, pred.enchantment, pred.levels.min != null ? pred.levels.min.intValue() : 1);
            }
         }
      }

      return itemStack;
   }

   private void addEnchantment(ItemStack item, Enchantment ench, int level) {
      if (item.getTagCompound() == null) {
         item.setTagCompound(new NBTTagCompound());
      }

      if (!item.getTagCompound().hasKey("ench", 9)) {
         item.getTagCompound().setTag("ench", new NBTTagList());
      }

      NBTTagList nbttaglist = item.getTagCompound().getTagList("ench", 10);
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setShort("id", (short)Enchantment.getEnchantmentID(ench));
      nbttagcompound.setShort("lvl", (short)level);
      nbttaglist.appendTag(nbttagcompound);
   }

   public void setTileNBT(String json) throws NBTException {
      this.tile = new NBTPredicateExtended(JsonToNBTExtended.getTagFromJson(json));
   }

   public void setStateJson(String json) {
      this.stateJson = json;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public void setMeta(MinMaxBounds meta) {
      this.meta = meta;
   }

   public void setCount(MinMaxBounds count) {
      this.count = count;
   }

   public void setDurability(MinMaxBounds durability) {
      this.durability = durability;
   }

   public void addEnchantment(EnchantmentPredicate predicate) {
      this.enchantmentData.add(predicate);
   }

   public void setPotion(String potion) throws Exception {
      PotionType type = PotionType.getPotionTypeForName(potion);
      if (type != null) {
         this.potionType = type;
      } else {
         throw new Exception(potion + " is not a valid potion type.");
      }
   }

   public void setNBT(String json) throws NBTException {
      this.nbt = new NBTPredicateExtended(JsonToNBTExtended.getTagFromJson(json));
   }

   public void setOre(String ore) throws Exception {
      if (OreDictionary.doesOreNameExist(ore)) {
         this.oreDict = ore;
      } else {
         throw new Exception(ore + " is not a valid ore dictionary name.");
      }
   }

   public ItemBlockData meta(int meta) {
      this.setMeta(new MinMaxBounds((float)meta, (float)meta));
      return this;
   }

   public ItemBlockData state(String state) {
      this.setStateJson(state);
      return this;
   }
}
