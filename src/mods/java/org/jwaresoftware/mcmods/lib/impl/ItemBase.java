package org.jwaresoftware.mcmods.lib.impl;

import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.api.Gidable;
import org.jwaresoftware.mcmods.lib.api.IBurnable;

public abstract class ItemBase extends Item implements Gidable {
   protected final String _oid;
   protected boolean _hidden;
   protected EnumRarity _rarity;
   protected Boolean _glimmer;

   protected ItemBase(String oid, boolean hidden) {
      this._hidden = false;
      this._rarity = null;
      this._glimmer = null;
      this._oid = oid;
      this._hidden = hidden;
      this.setUnlocalizedName(this.itemid_prefix() + this._oid);
      if (!hidden) {
         this.setCreativeTab(SharedGlue.CreativeTabs_materials);
      }

   }

   protected ItemBase(String oid) {
      this(oid, false);
   }

   protected abstract String itemid_prefix();

   public final String oid() {
      return this._oid;
   }

   public int getItemBurnTime(ItemStack stack) {
      int bT = -1;
      if (!stack.isEmpty() && stack.getItem() instanceof IBurnable) {
         bT = ((IBurnable)stack.getItem()).burnTime(stack);
      }

      return bT;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (!this._hidden) {
         super.getSubItems(tab, items);
      }

   }

   public int getColorFrom(ItemStack stack, int tintIndex) {
      return -1;
   }

   public EnumRarity getRarity(ItemStack stack) {
      EnumRarity r = super.getRarity(stack);
      return this._rarity != null && this._rarity.ordinal() > r.ordinal() ? this._rarity : r;
   }

   public final ItemBase setRarity(@Nullable EnumRarity rarity) {
      this._rarity = rarity;
      return this;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return this._glimmer != null ? this._glimmer.booleanValue() : super.hasEffect(stack);
   }

   public final ItemBase setGlimmerEffect(boolean setting) {
      this._glimmer = setting;
      return this;
   }
}
