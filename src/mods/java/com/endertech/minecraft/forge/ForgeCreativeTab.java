package com.endertech.minecraft.forge;

import com.endertech.common.CommonCollect;
import com.endertech.common.CommonTime;
import com.endertech.minecraft.forge.units.IForgeUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeCreativeTab extends CreativeTabs {
   private ItemStack iconItem;
   private CommonTime.Stamp lastIconChange;

   public ForgeCreativeTab(String modId, IForgeUnit icon) {
      this(modId);
      this.setIconFrom(icon);
   }

   public ForgeCreativeTab(String modId) {
      super(modId);
      this.iconItem = ItemStack.EMPTY;
      this.lastIconChange = CommonTime.Stamp.now();
   }

   /** @deprecated */
   @SideOnly(Side.CLIENT)
   @Deprecated
   public ItemStack getIconItemStack() {
      return this.getTabIconItem();
   }

   @SideOnly(Side.CLIENT)
   public ItemStack getTabIconItem() {
      return this.iconItem;
   }

   public void setIconFrom(IForgeUnit unit) {
      if (unit instanceof Block) {
         this.iconItem = new ItemStack((Block)unit);
      } else if (unit instanceof Item) {
         this.iconItem = new ItemStack((Item)unit);
      }

      this.lastIconChange = CommonTime.Stamp.now();
   }

   public <T extends IForgeUnit> void setRandomIconFrom(Collection<T> collection) {
      List<IForgeUnit> registered = new ArrayList();

      for(IForgeUnit unit : collection) {
         if (unit.isRegistered()) {
            registered.add(unit);
         }
      }

      IForgeUnit unit = (IForgeUnit)CommonCollect.getRandomElementFrom(registered);
      if (unit != null) {
         this.setIconFrom(unit);
      }

   }

   public boolean isTimeToChangeIcon(CommonTime.Interval changeInterval) {
      return CommonTime.Interval.passedFrom(this.lastIconChange).moreThan(changeInterval);
   }
}
