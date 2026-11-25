package com.teamacronymcoders.contenttweaker.modules.vanilla.resources.creativetab;

import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemStackSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ICreativeTab;
import crafttweaker.api.item.IItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MCCreativeTab extends crafttweaker.mc1120.creativetabs.MCCreativeTab implements ICreativeTab {
   public MCCreativeTab(CreativeTabs creativeTabs) {
      super(creativeTabs, (String)ReflectionHelper.getPrivateValue(CreativeTabs.class, creativeTabs, new String[]{"tabLabel", "tabLabel"}));
   }

   public String getUnlocalizedName() {
      return this.getTabLabel();
   }

   public void setUnlocalizedName(String unlocalizedName) {
      throw new IllegalStateException("Can't set Unlocalized name for existing Creative Tab");
   }

   public IItemStack getIconStack() {
      return null;
   }

   public void setIconStack(IItemStack iconStack) {
      this.setIconStackSupplier(() -> {
         return iconStack;
      });
   }

   public IItemStackSupplier getIconStackSupplier() {
      return this::getIconStack;
   }

   public void setIconStackSupplier(IItemStackSupplier stackSupplier) {
      throw new IllegalStateException("Can't set Icon Stack for existing Creative Tab");
   }

   public void setHasSearch() {
   }

   public void register() {
      throw new IllegalStateException("Can't register an already existing Creative Tab");
   }
}
