package com.alcatrazescapee.notreepunching.client;

import com.alcatrazescapee.notreepunching.common.items.ItemRock;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import com.alcatrazescapee.notreepunching.util.types.ToolType;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModTabs {
   public static final CreativeTabs TAB_ITEMS = new CreativeTabs("notreepunching.items") {
      @SideOnly(Side.CLIENT)
      @Nonnull
      public ItemStack getTabIconItem() {
         return new ItemStack(ItemRock.get(Stone.STONE));
      }
   };
   public static final CreativeTabs TAB_TOOLS = new CreativeTabs("notreepunching.tools") {
      @SideOnly(Side.CLIENT)
      @Nonnull
      public ItemStack getTabIconItem() {
         return new ItemStack(ModItems.getFlintTool(ToolType.PICKAXE));
      }
   };

   public ModTabs() {
   }
}
