package com.teamacronymcoders.base.items;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.IModAware;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemBaseNoModel extends Item implements IModAware, IHasSubItems {
   protected String texturePath;
   protected String name;
   private IBaseMod mod;
   boolean creativeTabSet;

   public ItemBaseNoModel(String name) {
      this("", name);
   }

   public ItemBaseNoModel(String texturePath, String name) {
      this.creativeTabSet = false;
      this.name = name;
      this.texturePath = texturePath;
      if (!texturePath.isEmpty() && !texturePath.endsWith("/")) {
         this.texturePath = this.texturePath + "/";
      }

      this.setUnlocalizedName(name);
   }

   @Nonnull
   public Item setCreativeTab(@Nonnull CreativeTabs tab) {
      if (!this.creativeTabSet) {
         super.setCreativeTab(tab);
         this.creativeTabSet = true;
      }

      return this;
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public Item getItem() {
      return this;
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

   @Nullable
   public RayTraceResult rayTrace(@Nonnull World world, @Nonnull EntityPlayer player, boolean useLiquids) {
      return super.rayTrace(world, player, useLiquids);
   }
}
