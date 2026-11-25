package com.teamacronymcoders.contenttweaker.modules.vanilla.items;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabContent extends CreativeTabs {
   private CreativeTabRepresentation representation;
   private ItemStack iconStack;

   public CreativeTabContent(CreativeTabRepresentation representation) {
      super(representation.getUnlocalizedName());
      this.representation = representation;
      this.setFields();
   }

   private void setFields() {
      this.setBackgroundImageName(this.representation.getBackGroundImage());
      if (this.representation.isNoScrollBar()) {
         this.setNoScrollbar();
      }

      if (this.representation.isNoTitle()) {
         this.setNoTitle();
      }

   }

   public boolean hasSearchBar() {
      return this.representation.isHasSearch();
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public ItemStack getTabIconItem() {
      return this.iconStack = (ItemStack)Optional.ofNullable(this.iconStack).orElseGet(() -> {
         return this.representation.getInternalIconStack();
      });
   }
}
