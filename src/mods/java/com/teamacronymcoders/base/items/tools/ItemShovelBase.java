package com.teamacronymcoders.base.items.tools;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.IModAware;
import com.teamacronymcoders.base.client.models.IHasModel;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.NonNullList;

public class ItemShovelBase extends ItemSpade implements IHasModel, IModAware {
   boolean creativeTabSet = false;
   String name;
   private IBaseMod<?> mod;

   public ItemShovelBase(String name, ToolMaterial material) {
      super(material);
      this.setUnlocalizedName(name);
      this.name = name;
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.name);
      return modelNames;
   }

   @ParametersAreNonnullByDefault
   public void getSubItems(@Nullable CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (tab != null && tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH) {
         subItems.addAll(this.getAllSubItems(Lists.newArrayList()));
      }

   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      itemStacks.add(new ItemStack(this, 1));
      return itemStacks;
   }

   @Nonnull
   public Item setCreativeTab(@Nonnull CreativeTabs tab) {
      if (!this.creativeTabSet) {
         super.setCreativeTab(tab);
         this.creativeTabSet = true;
      }

      return this;
   }

   public IBaseMod<?> getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public Item getItem() {
      return this;
   }
}
