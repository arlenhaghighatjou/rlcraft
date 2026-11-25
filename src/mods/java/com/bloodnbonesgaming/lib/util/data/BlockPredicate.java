package com.bloodnbonesgaming.lib.util.data;

import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class BlockPredicate {
   public static final BlockPredicate ANY = new BlockPredicate();
   private final Block block;
   private final MinMaxBounds meta;
   private final Map<IProperty<?>, Object> properties;
   private final String oreDict;
   private final NBTPredicate tile;

   public BlockPredicate() {
      this.block = null;
      this.properties = null;
      this.oreDict = null;
      this.meta = MinMaxBounds.UNBOUNDED;
      this.tile = NBTPredicate.ANY;
   }

   public BlockPredicate(@Nullable Block block, MinMaxBounds meta, @Nullable Map<IProperty<?>, Object> properties, @Nullable String oreDict, NBTPredicate tile) {
      this.block = block;
      this.meta = meta;
      this.properties = properties;
      this.oreDict = oreDict;
      this.tile = tile;
   }

   public boolean test(IBlockState state) {
      return this.test(state, (TileEntity)null);
   }

   public boolean test(IBlockState state, TileEntity tile) {
      if (this.block != null && state.getBlock() != this.block) {
         return false;
      } else {
         if (this.properties != null) {
            for(Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
               if (state.getValue((IProperty)entry.getKey()) != entry.getValue()) {
                  return false;
               }
            }
         }

         if (!this.meta.test((float)state.getBlock().getMetaFromState(state))) {
            return false;
         } else {
            if (this.oreDict != null) {
               boolean foundOreDict = false;
               Block block = state.getBlock();
               Item item = Item.getItemFromBlock(block);
               if (item != Items.AIR) {
                  ItemStack itemStack = new ItemStack(item, 1, block.damageDropped(state));
                  NonNullList<ItemStack> ores = OreDictionary.getOres(this.oreDict, false);
                  if (!ores.isEmpty()) {
                     for(ItemStack stack : ores) {
                        if (OreDictionary.itemMatches(stack, itemStack, false)) {
                           foundOreDict = true;
                           break;
                        }
                     }
                  }
               }

               if (!foundOreDict) {
                  return false;
               }
            }

            return this.tile == null || this.tile == NBTPredicate.ANY || tile != null && this.tile.test(tile.serializeNBT());
         }
      }
   }
}
