package com.creativemd.creativecore.common.gui.controls.gui.custom;

import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.type.HashMapList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GuiStackSelectorAll extends GuiStackSelector {
   public GuiStackSelectorAll.StackCollector collector;

   public GuiStackSelectorAll(String name, int x, int y, int width, EntityPlayer player, GuiStackSelectorAll.StackCollector collector, boolean searchBar) {
      super(name, x, y, width, player, searchBar);
      this.collector = collector;
      this.updateCollectedStacks();
      this.selectFirst();
   }

   public GuiStackSelectorAll(String name, int x, int y, int width, EntityPlayer player, GuiStackSelectorAll.StackCollector collector) {
      this(name, x, y, width, player, collector, false);
   }

   protected HashMapList<String, ItemStack> collectItems() {
      return this.collector != null ? this.collector.collect(this.getPlayer()) : null;
   }

   public void setSelectedForce(ItemStack stack) {
      if (!this.setSelected(stack)) {
         String display;
         try {
            display = stack.getDisplayName();
         } catch (Exception var4) {
            display = ((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).toString();
         }

         this.caption = display;
         this.selected = stack;
         this.raiseEvent(new GuiControlChangedEvent(this));
      }

   }

   public static GuiStackSelectorAll.StackCollector getCollectorFromPlayer(EntityPlayer player, GuiStackSelectorAll.StackSelector selector) {
      return (GuiStackSelectorAll.StackCollector)(player.isCreative() ? new GuiStackSelectorAll.CreativeCollector(selector) : new GuiStackSelectorAll.InventoryCollector(selector));
   }

   public static boolean contains(String search, ItemStack stack) {
      if (search.equals("")) {
         return true;
      } else if (getItemName(stack).toLowerCase().contains(search)) {
         return true;
      } else {
         for(String line : stack.getTooltip((EntityPlayer)null, TooltipFlags.NORMAL)) {
            if (line.toLowerCase().contains(search)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getItemName(ItemStack stack) {
      String itemName = "";

      try {
         itemName = stack.getDisplayName();
      } catch (Exception var3) {
         if (Block.getBlockFromItem(stack.getItem()) != null) {
            itemName = ((ResourceLocation)Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem()))).toString();
         } else {
            itemName = ((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).toString();
         }
      }

      return itemName;
   }

   public static class CreativeCollector extends GuiStackSelectorAll.InventoryCollector {
      public CreativeCollector(GuiStackSelectorAll.StackSelector selector) {
         super(selector);
      }

      public HashMapList<String, ItemStack> collect(EntityPlayer player) {
         HashMapList<String, ItemStack> stacks = super.collect(player);
         NonNullList<ItemStack> tempStacks = NonNullList.create();

         for(Item item : Item.REGISTRY) {
            if (item != null && item.getCreativeTab() != null) {
               item.getSubItems(CreativeTabs.SEARCH, tempStacks);
            }
         }

         List<ItemStack> newStacks = new ArrayList();

         for(ItemStack stack : tempStacks) {
            if (!stack.isEmpty() && this.selector.allow(stack)) {
               newStacks.add(stack);
            }
         }

         stacks.add("selector.all", newStacks);
         return stacks;
      }
   }

   public static class GuiBlockSelector extends GuiStackSelectorAll.SearchSelector {
      public GuiBlockSelector() {
      }

      public boolean allow(ItemStack stack) {
         if (!super.allow(stack)) {
            return false;
         } else {
            return Block.getBlockFromItem(stack.getItem()) != null && !(Block.getBlockFromItem(stack.getItem()) instanceof BlockAir);
         }
      }
   }

   public static class InventoryCollector extends GuiStackSelectorAll.StackCollector {
      public InventoryCollector(GuiStackSelectorAll.StackSelector selector) {
         super(selector);
      }

      public HashMapList<String, ItemStack> collect(EntityPlayer player) {
         HashMapList<String, ItemStack> stacks = new HashMapList<String, ItemStack>();
         if (player != null) {
            List<ItemStack> tempStacks = new ArrayList();

            for(ItemStack stack : player.inventory.mainInventory) {
               if (!stack.isEmpty() && this.selector.allow(stack)) {
                  tempStacks.add(stack.copy());
               } else if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
                  this.collect((IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null), tempStacks);
               }
            }

            stacks.add("selector.inventory", tempStacks);
         }

         return stacks;
      }

      protected void collect(IItemHandler inventory, List<ItemStack> stacks) {
         for(int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && this.selector.allow(stack)) {
               stacks.add(stack.copy());
            } else if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
               this.collect((IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null), stacks);
            }
         }

      }
   }

   public static class SearchSelector extends GuiStackSelectorAll.StackSelector {
      public String search = "";

      public SearchSelector() {
      }

      public boolean allow(ItemStack stack) {
         return GuiStackSelectorAll.contains(this.search, stack);
      }
   }

   public abstract static class StackCollector {
      public GuiStackSelectorAll.StackSelector selector;

      public StackCollector(GuiStackSelectorAll.StackSelector selector) {
         this.selector = selector;
      }

      public abstract HashMapList<String, ItemStack> collect(EntityPlayer var1);
   }

   public abstract static class StackSelector {
      public StackSelector() {
      }

      public abstract boolean allow(ItemStack var1);
   }
}
