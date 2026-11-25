package com.creativemd.creativecore.common.gui.container;

import com.creativemd.creativecore.common.gui.ContainerControl;
import com.creativemd.creativecore.common.gui.CoreControl;
import com.creativemd.creativecore.common.gui.controls.container.SlotControl;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ContainerParent extends ContainerControl implements IControlParent {
   public ArrayList<ContainerControl> controls = new ArrayList();

   public ContainerParent(String name) {
      super(name);
   }

   public List getControls() {
      return this.controls;
   }

   public CoreControl get(String name) {
      for(int i = 0; i < this.controls.size(); ++i) {
         if (((ContainerControl)this.controls.get(i)).name.equalsIgnoreCase(name)) {
            return (CoreControl)this.controls.get(i);
         }
      }

      return null;
   }

   public boolean has(String name) {
      for(int i = 0; i < this.controls.size(); ++i) {
         if (((ContainerControl)this.controls.get(i)).name.equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   public void onOpened() {
      for(int i = 0; i < this.controls.size(); ++i) {
         ((ContainerControl)this.controls.get(i)).parent = this;
         ((ContainerControl)this.controls.get(i)).onOpened();
      }

      this.refreshControls();
   }

   public void onClosed() {
      for(int i = 0; i < this.controls.size(); ++i) {
         ((ContainerControl)this.controls.get(i)).onClosed();
      }

   }

   public void updateEqualContainers() {
      if (this.parent != null) {
         this.getParent().updateEqualContainers();
      }

   }

   public void refreshControls() {
      for(int i = 0; i < this.controls.size(); ++i) {
         ((ContainerControl)this.controls.get(i)).parent = this;
         ((ContainerControl)this.controls.get(i)).setID(i);
      }

   }

   public void sendNBTUpdate(ContainerControl control, NBTTagCompound nbt) {
      if (this.parent != null) {
         this.getParent().sendNBTUpdate(control, nbt);
      }

   }

   public void onTick() {
      for(int i = 0; i < this.controls.size(); ++i) {
         ((ContainerControl)this.controls.get(i)).onTick();
      }

   }

   public ArrayList<Slot> getSlots() {
      ArrayList<Slot> slots = new ArrayList();

      for(int i = 0; i < this.controls.size(); ++i) {
         if (this.controls.get(i) instanceof SlotControl) {
            slots.add(((SlotControl)this.controls.get(i)).slot);
         }
      }

      return slots;
   }

   public void addSlotToContainerUninteractable(Slot slot) {
      this.controls.add((ContainerControl)(new SlotControl(slot)).setEnabled(false));
   }

   public void addSlotToContainer(Slot slot) {
      this.controls.add(new SlotControl(slot));
   }

   public void addPlayerSlotsToContainer(EntityPlayer player) {
      this.addPlayerSlotsToContainer(player, -1);
   }

   public void addPlayerSlotsToContainer(EntityPlayer player, int indexUninteractable) {
      this.addPlayerSlotsToContainer(player, 8, 84, indexUninteractable);
   }

   public void addPlayerSlotsToContainer(EntityPlayer player, int x, int y) {
      this.addPlayerSlotsToContainer(player, x, y, -1);
   }

   public void addPlayerSlotsToContainer(EntityPlayer player, int x, int y, int indexUninteractable) {
      for(int l = 0; l < 3; ++l) {
         for(int i1 = 0; i1 < 9; ++i1) {
            int index = i1 + l * 9 + 9;
            if (index == indexUninteractable) {
               this.addSlotToContainer(new Slot(player.inventory, index, i1 * 18 + x, l * 18 + y) {
                  public boolean canTakeStack(EntityPlayer playerIn) {
                     return false;
                  }

                  public boolean isItemValid(ItemStack stack) {
                     return false;
                  }
               });
            } else {
               this.addSlotToContainer(new Slot(player.inventory, index, i1 * 18 + x, l * 18 + y));
            }
         }
      }

      for(int var8 = 0; var8 < 9; ++var8) {
         if (var8 == indexUninteractable) {
            this.addSlotToContainerUninteractable(new Slot(player.inventory, var8, var8 * 18 + x, 58 + y) {
               public boolean canTakeStack(EntityPlayer playerIn) {
                  return false;
               }

               public boolean isItemValid(ItemStack stack) {
                  return false;
               }
            });
         } else {
            this.addSlotToContainer(new Slot(player.inventory, var8, var8 * 18 + x, 58 + y));
         }
      }

   }
}
