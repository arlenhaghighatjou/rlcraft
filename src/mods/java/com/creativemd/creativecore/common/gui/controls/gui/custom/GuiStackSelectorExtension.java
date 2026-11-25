package com.creativemd.creativecore.common.gui.controls.gui.custom;

import com.creativemd.creativecore.common.gui.controls.container.SlotControlNoSync;
import com.creativemd.creativecore.common.gui.controls.container.client.GuiSlotControl;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.common.slots.SlotPreview;
import com.creativemd.creativecore.common.utils.type.HashMapList;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import java.util.ArrayList;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class GuiStackSelectorExtension extends GuiComboBoxExtension {
   public String search = "";

   public GuiStackSelectorExtension(String name, EntityPlayer player, int x, int y, int width, int height, GuiStackSelector comboBox) {
      super(name, comboBox, x, y, width, height, new ArrayList());
   }

   public void onOpened() {
      super.onOpened();
      this.addListener(this);
   }

   public void onClosed() {
      super.onClosed();
      this.removeListener(this);
   }

   public void reloadControls() {
      if (this.comboBox != null) {
         HashMapList<String, ItemStack> stacks = this.search != null && !this.search.equals("") ? new HashMapList() : ((GuiStackSelector)this.comboBox).getStacks();
         if (this.search != null && !this.search.equals("")) {
            for(Entry<String, ArrayList<ItemStack>> entry : ((GuiStackSelector)this.comboBox).getStacks().entrySet()) {
               for(ItemStack stack : (ArrayList)entry.getValue()) {
                  if (GuiStackSelectorAll.contains(this.search, stack)) {
                     stacks.add(entry.getKey(), stack);
                  }
               }
            }
         }

         int height = 0;
         GuiTextfield textfield = null;
         if (((GuiStackSelector)this.comboBox).searchBar && this.controls.size() > 0) {
            textfield = (GuiTextfield)this.controls.get(0);
         }

         this.controls.clear();
         if (((GuiStackSelector)this.comboBox).searchBar) {
            height = height + 4;
            if (textfield == null) {
               textfield = new GuiTextfield("searchBar", this.search == null ? "" : this.search, 3, height, this.width - 20, 10);
            }

            this.controls.add(textfield);
            height = height + textfield.height;
            textfield.focused = true;
         }

         for(Entry<String, ArrayList<ItemStack>> entry : stacks.entrySet()) {
            GuiLabel label = new GuiLabel(translate((String)entry.getKey()), 3, height);
            label.width = this.width - 20;
            label.height = 14;
            this.controls.add(label);
            height = height + label.height;
            int SlotsPerRow = (this.width - 20) / 18;
            InventoryBasic basic = new InventoryBasic((String)entry.getKey(), false, ((ArrayList)entry.getValue()).size());
            int i = 0;

            for(ItemStack stack : (ArrayList)entry.getValue()) {
               basic.setInventorySlotContents(i, stack);
               int row = i / SlotsPerRow;
               this.addControl((new SlotControlNoSync(new SlotPreview(basic, i, (i - row * SlotsPerRow) * 18, height + row * 18))).getGuiControl());
               ++i;
            }

            height = (int)((double)height + Math.ceil((double)i / (double)SlotsPerRow) * 18.0D);
         }

      }
   }

   @CustomEventSubscribe
   public void onChanged(GuiControlChangedEvent event) {
      if (event.source.is("searchBar")) {
         this.search = ((GuiTextfield)event.source).text;
         this.reloadControls();
         this.refreshControls();
      }

   }

   @CustomEventSubscribe
   public void onLabelClicked(GuiControlClickEvent event) {
      if (event.source instanceof GuiSlotControl && event.source.parent == this) {
         ((GuiStackSelector)this.comboBox).setSelected(((GuiSlotControl)event.source).slot.slot.getStack());
         this.comboBox.closeBox();
      }

   }

   public boolean mouseScrolled(int posX, int posY, int scrolled) {
      return super.mouseScrolled(posX, posY, scrolled);
   }
}
