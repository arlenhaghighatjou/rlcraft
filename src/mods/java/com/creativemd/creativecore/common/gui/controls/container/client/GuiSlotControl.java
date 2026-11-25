package com.creativemd.creativecore.common.gui.controls.container.client;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.client.style.TextureDisplayStyle;
import com.creativemd.creativecore.common.gui.controls.container.SlotControl;
import com.creativemd.creativecore.common.slots.SlotInput;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiSlotControl extends GuiControl {
   public static Style slotStyle;
   public SlotControl slot;
   public ItemStack dragged = null;
   public boolean isRightClick = false;
   public GuiSlotControl startSlot = null;
   public int[] stackSize = null;
   public int index = -1;

   public GuiSlotControl(int x, int y, SlotControl slot) {
      super(slot.slot.inventory.getName() + slot.slot.slotNumber, x, y, 14, 14);
      this.slot = slot;
      this.width -= this.marginWidth;
      this.height -= this.marginWidth;
      this.marginWidth = 0;
   }

   public Style getDefaultStyle() {
      return slotStyle;
   }

   protected ItemStack getStackToRender() {
      return this.slot.slot.getStack();
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      if (this.isDragged()) {
         style.getMouseOverBackground(this).renderStyle(helper, width, height);
      }

      ItemStack stack = this.getStackToRender();
      ItemStack newStack = null;
      if (this.isDragged() && this.stackSize.length > 1) {
         this.updateHandStack();
         newStack = this.getItemStackByIndex(this.index);
      } else if (this.startSlot != null) {
         this.updateStackSize();
         newStack = this.startSlot.getItemStackByIndex(this.index);
      }

      if (newStack != null) {
         if (stack != null) {
            newStack.grow(stack.getCount());
         }

         stack = newStack;
      }

      if (this.slot.slot instanceof SlotInput) {
         ItemStack backgroundStack = null;
         if (((SlotInput)this.slot.slot).input != null) {
            backgroundStack = ((SlotInput)this.slot.slot).input.getItemStack(1);
         }

         if (backgroundStack != null && !backgroundStack.isEmpty()) {
            try {
               helper.drawItemStackAndOverlay(backgroundStack, 0, 0, 16, 16);
            } catch (Exception var10) {
               helper.font.drawString("X", 0, 0, -1);
            }
         }
      }

      if (stack != null && !stack.isEmpty()) {
         try {
            helper.drawItemStackAndOverlay(stack, 0, 0, 16, 16);
         } catch (Exception var9) {
            helper.font.drawString("X", 0, 0, -1);
         }
      }

   }

   public List<String> getTooltip() {
      return this.slot.slot.getHasStack() && this.getPlayer().inventory.getItemStack().isEmpty() ? getTooltip(this.slot.slot.getStack()) : null;
   }

   public static List<String> getTooltip(ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      } else {
         List<String> tips = new ArrayList();
         List<String> list = null;

         try {
            list = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);

            for(int k = 0; k < list.size(); ++k) {
               if (k == 0) {
                  list.set(k, stack.getRarity().rarityColor + (String)list.get(k));
               } else {
                  list.set(k, ChatFormatting.GRAY + (String)list.get(k));
               }

               tips.add(list.get(k));
            }
         } catch (Exception var6) {
            try {
               tips.add(((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).toString());
               tips.add("Damage: " + stack.getItemDamage());
               tips.add("NBT: " + (stack.hasTagCompound() ? "null" : stack.getTagCompound().toString()));
            } catch (Exception var5) {
               tips.add("<ERRORED>");
            }
         }

         return tips;
      }
   }

   public boolean onKeyPressed(char character, int key) {
      if (key == mc.gameSettings.keyBindDrop.getKeyCode() && this.isMouseOver()) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setBoolean("ctrl", GuiScreen.isCtrlKeyDown());
         nbt.setInteger("type", 2);
         this.slot.sendPacket(nbt);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(int posX, int posY, int scrolled) {
      if (!GuiContainer.isShiftKeyDown()) {
         return false;
      } else {
         this.sendClickedPacket(3, scrolled);
         return true;
      }
   }

   public void sendClickedPacket(int button, int scrolled) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setInteger("button", button);
      boolean shift = GuiScreen.isShiftKeyDown();
      nbt.setBoolean("shift", shift);
      nbt.setInteger("scrolled", scrolled);
      nbt.setInteger("type", 1);
      this.slot.sendPacket(nbt);
      this.slot.onSlotClicked(button, shift, scrolled);
   }

   public boolean isDragged() {
      return this.dragged != null;
   }

   public boolean mousePressed(int posX, int posY, int button) {
      for(int i = 0; i < this.getParent().controls.size(); ++i) {
         if (this.getParent().controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl)this.getParent().controls.get(i)).isDragged()) {
            return true;
         }
      }

      ItemStack stack = this.getPlayer().inventory.getItemStack();
      if (!stack.isEmpty() && button < 2) {
         this.dragged = stack.copy();
         this.isRightClick = button == 1;
         boolean canStack = Container.canAddItemToSlot(this.slot.slot, this.dragged, true);
         int stackSize = Math.min(this.slot.slot.getSlotStackLimit(), this.dragged.getMaxStackSize());
         if (this.slot.slot.getHasStack()) {
            stackSize -= this.slot.slot.getStack().getCount();
         }

         if (!this.slot.slot.isItemValid(this.dragged)) {
            stackSize = 0;
         }

         if (stackSize > 0 && canStack) {
            this.stackSize = new int[1];
            this.stackSize[0] = stackSize;
            this.index = 0;
            return true;
         }

         this.dragged = null;
      }

      this.sendClickedPacket(button, 0);
      return true;
   }

   public void mouseMove(int posX, int posY, int button) {
      if (this.startSlot == null && !this.isDragged() && this.isMouseOver(posX, posY)) {
         for(int i = 0; i < this.getParent().controls.size(); ++i) {
            if (this.getParent().controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl)this.getParent().controls.get(i)).isDragged()) {
               this.startSlot = (GuiSlotControl)this.getParent().controls.get(i);
               boolean canStack = Container.canAddItemToSlot(this.slot.slot, this.startSlot.dragged, true);
               int stackSize = Math.min(this.slot.slot.getSlotStackLimit(), this.startSlot.dragged.getMaxStackSize());
               if (this.slot.slot.getHasStack()) {
                  stackSize -= this.slot.slot.getStack().getCount();
               }

               if (!this.slot.slot.isItemValid(this.dragged)) {
                  stackSize = 0;
               }

               if (stackSize > 0 && canStack) {
                  int[] newStackSize = new int[this.startSlot.stackSize.length + 1];

                  for(int j = 0; j < this.startSlot.stackSize.length; ++j) {
                     newStackSize[j] = this.startSlot.stackSize[j];
                  }

                  newStackSize[this.startSlot.stackSize.length] = stackSize;
                  this.index = this.startSlot.stackSize.length;
                  this.startSlot.stackSize = newStackSize;
               } else {
                  this.startSlot = null;
               }
            }
         }
      }

   }

   public void mouseReleased(int posX, int posY, int button) {
      if (this.isDragged() && this.stackSize.length > 1) {
         NBTTagCompound nbt = new NBTTagCompound();
         int[] slotArray = new int[this.stackSize.length];
         slotArray[this.index] = this.slot.getID();

         for(int i = 0; i < this.getParent().controls.size(); ++i) {
            if (this.getParent().controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl)this.getParent().controls.get(i)).startSlot != null) {
               slotArray[((GuiSlotControl)this.getParent().controls.get(i)).index] = ((GuiSlotControl)this.getParent().controls.get(i)).slot.getID();
               ((GuiSlotControl)this.getParent().controls.get(i)).startSlot = null;
               ((GuiSlotControl)this.getParent().controls.get(i)).index = -1;
            }
         }

         nbt.setIntArray("slots", slotArray);
         nbt.setBoolean("right", this.isRightClick);
         nbt.setInteger("type", 3);
         this.slot.sendPacket(nbt);
         this.slot.splitStack(slotArray, this.dragged, this.isRightClick);
      } else if (this.isDragged()) {
         this.sendClickedPacket(this.isRightClick ? 1 : 0, 0);
      }

      this.dragged = null;
      this.stackSize = null;
   }

   public void updateStackSize() {
      if (this.startSlot != null) {
         int stackSize = Math.min(this.slot.slot.getSlotStackLimit(), this.startSlot.dragged.getMaxStackSize());
         if (this.slot.slot.getHasStack()) {
            stackSize -= this.slot.slot.getStack().getCount();
         }

         this.startSlot.stackSize[this.index] = stackSize;
      }

   }

   public ItemStack getItemStackByIndex(int index) {
      if (index >= this.dragged.getCount()) {
         return null;
      } else {
         ItemStack result = this.dragged.copy();
         if (this.isRightClick) {
            result.setCount(1);
         } else {
            int used = 0;
            int StackPerSlot = MathHelper.floor((float)result.getCount() / (float)this.stackSize.length);

            for(int i = 0; i < index; ++i) {
               used += Math.min(StackPerSlot, this.stackSize[i]);
            }

            if (used < result.getCount()) {
               result.setCount(Math.min(StackPerSlot, this.stackSize[index]));
            }
         }

         return result;
      }
   }

   public void updateHandStack() {
      int used = 0;
      int StackPerSlot = MathHelper.floor((float)this.dragged.getCount() / (float)this.stackSize.length);
      if (this.isRightClick) {
         StackPerSlot = 1;
      }

      for(int i = 0; i < this.stackSize.length; ++i) {
         used += Math.min(StackPerSlot, this.stackSize[i]);
      }

      int left = this.dragged.getCount() - used;
      ItemStack hand = this.dragged.copy();
      hand.setCount(left);
      this.getPlayer().inventory.setItemStack(hand);
   }

   static {
      slotStyle = new Style("slot", new TextureDisplayStyle(guiUtilsImage, 176, 0), DisplayStyle.emptyDisplay, new ColoredDisplayStyle(197, 197, 197), new ColoredDisplayStyle(139, 139, 139), new ColoredDisplayStyle(0, 0, 0, 100));
   }
}
