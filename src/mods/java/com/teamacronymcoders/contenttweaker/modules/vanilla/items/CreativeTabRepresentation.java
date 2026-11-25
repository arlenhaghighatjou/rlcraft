package com.teamacronymcoders.contenttweaker.modules.vanilla.items;

import com.teamacronymcoders.contenttweaker.api.IRepresentation;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemStackSupplier;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabRepresentation implements IRepresentation<CreativeTabs>, ICreativeTab {
   private String unlocalizedName;
   private IItemStackSupplier supplyItemStack;
   private CreativeTabContent creativeTabContent;
   private boolean hasSearch = false;
   private String backGroundImage = "items.png";
   private boolean noScrollBar = false;
   private boolean noTitle = false;

   public CreativeTabRepresentation() {
   }

   public String getUnlocalizedName() {
      return this.unlocalizedName;
   }

   public void setUnlocalizedName(String unlocalizedName) {
      this.unlocalizedName = unlocalizedName;
   }

   public IItemStack getIconStack() {
      return this.getSupplyItemStack().getItemStack();
   }

   public ItemStack getInternalIconStack() {
      return (ItemStack)Optional.ofNullable(this.getSupplyItemStack().getItemStack()).map(IIngredient::getInternal).map((iItemStack) -> {
         return (ItemStack)iItemStack;
      }).orElse(ItemStack.EMPTY);
   }

   public void setIconStack(IItemStack iconStack) {
      this.supplyItemStack = () -> {
         return iconStack;
      };
   }

   public IItemStackSupplier getIconStackSupplier() {
      return this.supplyItemStack;
   }

   public void setIconStackSupplier(IItemStackSupplier stackSupplier) {
      this.supplyItemStack = stackSupplier;
   }

   public void setHasSearch() {
      this.hasSearch = true;
   }

   public String getName() {
      return this.getUnlocalizedName();
   }

   public String getTypeName() {
      return "Creative Tab";
   }

   public void register() {
      if (this.creativeTabContent == null) {
         this.creativeTabContent = new CreativeTabContent(this);
      }

   }

   public void setBackgroundImageName(String backgroundImage) {
      this.backGroundImage = backgroundImage;
   }

   public int getSearchBarWidth() {
      return 0;
   }

   public String getTabLabel() {
      return this.getUnlocalizedName();
   }

   public void setNoScrollBar() {
      this.noScrollBar = true;
   }

   public void setNoTitle() {
      this.noTitle = true;
   }

   public CreativeTabs getInternal() {
      if (this.creativeTabContent == null) {
         this.creativeTabContent = new CreativeTabContent(this);
      }

      return this.creativeTabContent;
   }

   public IItemStackSupplier getSupplyItemStack() {
      return this.supplyItemStack;
   }

   public boolean isHasSearch() {
      return this.hasSearch;
   }

   public String getBackGroundImage() {
      return this.backGroundImage;
   }

   public boolean isNoScrollBar() {
      return this.noScrollBar;
   }

   public boolean isNoTitle() {
      return this.noTitle;
   }
}
