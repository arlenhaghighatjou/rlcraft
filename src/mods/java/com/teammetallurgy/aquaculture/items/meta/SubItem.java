package com.teammetallurgy.aquaculture.items.meta;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SubItem {
   public int damage;
   public MetaItem item;
   public String unlocalizedName;

   public SubItem(MetaItem metaItem) {
      this.item = metaItem;
      this.damage = this.item.addSubItem(this);
   }

   public SubItem setCreativeTab(CreativeTabs tab) {
      this.item.setCreativeTab(tab);
      return this;
   }

   public SubItem setTranslationKey(String name) {
      this.unlocalizedName = name;
      return this;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return this.getItemStack(1);
   }

   @Nonnull
   public ItemStack getItemStack(int i) {
      return new ItemStack(this.item, i, this.damage);
   }

   public String getUnlocalizedName(@Nonnull ItemStack stack) {
      return this.unlocalizedName;
   }

   @Nonnull
   public ItemStack onEaten(@Nonnull ItemStack stack, World world, EntityPlayer player) {
      return stack;
   }

   public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
      return 0;
   }

   public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
      return EnumAction.NONE;
   }

   @Nonnull
   public ItemStack onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player) {
      return stack;
   }
}
