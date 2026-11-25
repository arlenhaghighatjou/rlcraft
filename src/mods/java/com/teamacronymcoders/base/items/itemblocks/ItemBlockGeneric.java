package com.teamacronymcoders.base.items.itemblocks;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.IModAware;
import com.teamacronymcoders.base.items.IHasOreDict;
import com.teamacronymcoders.base.items.IHasSubItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGeneric<T extends Block> extends ItemBlock implements IModAware, IHasSubItems, IHasOreDict {
   private T actualBlock;
   private IBaseMod mod;

   public ItemBlockGeneric(T block) {
      super(block);
      this.actualBlock = block;
      this.setHasSubtypes(true);
   }

   public T getActualBlock() {
      return this.actualBlock;
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      if (this.actualBlock instanceof IHasSubItems) {
         itemStacks.addAll(((IHasSubItems)this.actualBlock).getAllSubItems(new ArrayList()));
      } else {
         itemStacks.add(new ItemStack(this));
      }

      return itemStacks;
   }

   public int getMetadata(int damage) {
      return Math.min(15, Math.max(0, damage));
   }

   public Item getItem() {
      return this;
   }

   @Nonnull
   public Map<ItemStack, String> getOreDictNames(@Nonnull Map<ItemStack, String> names) {
      return this.actualBlock instanceof IHasOreDict ? ((IHasOreDict)this.actualBlock).getOreDictNames(names) : names;
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }
}
