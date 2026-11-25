package xzeroair.trinkets.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.ITrinketContainerHandler;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.TrinketContainerProvider;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

public class TrinketInventoryContainer extends Container {
   public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
   public final InventoryCraftResult craftResult = new InventoryCraftResult();
   public ITrinketContainerHandler trinket;
   private final EntityPlayer player;
   private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

   public TrinketInventoryContainer(InventoryPlayer playerInv, boolean client, final EntityPlayer player) {
      this.player = player;
      this.trinket = (ITrinketContainerHandler)player.getCapability(TrinketContainerProvider.containerCap, (EnumFacing)null);
      this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));

      for(int i = 0; i < 2; ++i) {
         for(int j = 0; j < 2; ++j) {
            this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
         }
      }

      for(int k = 0; k < 4; ++k) {
         final EntityEquipmentSlot slot = equipmentSlots[k];
         this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18) {
            public int getSlotStackLimit() {
               return 1;
            }

            public boolean isItemValid(ItemStack stack) {
               return stack.getItem().isValidArmor(stack, slot, player);
            }

            public boolean canTakeStack(EntityPlayer playerIn) {
               ItemStack itemstack = this.getStack();
               return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
            }

            public String getSlotTexture() {
               return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
            }
         });
      }

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
      }

      this.addSlotToContainer(new Slot(playerInv, 40, 77, 62) {
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack);
         }

         public String getSlotTexture() {
            return "minecraft:items/empty_armor_slot_shield";
         }
      });
      int x = TrinketsConfig.CLIENT.GUI.X + 1;
      int y = TrinketsConfig.CLIENT.GUI.Y + 1;
      int slots = TrinketsConfig.SERVER.GUI.guiSlotsRows;
      int X = x;
      int Y = y;
      int c = 0;
      int l = 0;

      for(int i = 0; i < slots; ++i) {
         if (l == 4 || l == 7) {
            Y += 4;
         }

         this.addSlotToContainer(new TrinketSlot(player, this.trinket, i, X - c * 18, Y + l * 18));
         ++l;
         if (l % 8 == 0) {
            l = 0;
            ++c;
            Y = y;
         }
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   public void onCraftMatrixChanged(IInventory par1IInventory) {
      this.slotChangedCraftingGrid(this.player.getEntityWorld(), this.player, this.craftMatrix, this.craftResult);
   }

   public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);
      this.craftResult.clear();
      if (!player.world.isRemote) {
         this.clearContainer(player, player.world, this.craftMatrix);
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = (Slot)this.inventorySlots.get(index);
      ItemStack itemstack = ItemStack.EMPTY;
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
         int slotShift = this.trinket.getSlots();
         if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index >= 1 && index < 5) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 5 && index < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 46 && index < 46 + slotShift) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (entityequipmentslot.getSlotType() == Type.ARMOR && !((Slot)this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack()) {
            int i = 8 - entityequipmentslot.getIndex();
            if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45)).getHasStack()) {
            if (!this.mergeItemStack(itemstack1, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (itemstack.hasCapability(Capabilities.ITEM_TRINKET, (EnumFacing)null) && itemstack.getItem() instanceof IAccessoryInterface) {
            boolean canEquip = ((IAccessoryInterface)itemstack1.getItem()).canEquipAccessory(itemstack1, this.player);
            boolean placeItem = canEquip && !this.mergeItemStack(itemstack1, 46, 46 + slotShift, false);
            if (placeItem) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 9 && index < 36) {
            if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 36 && index < 45) {
            if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty() && !this.trinket.isEventBlocked() && slot instanceof TrinketSlot && itemstack.getItem() instanceof IAccessoryInterface) {
            ((IAccessoryInterface)itemstack.getItem()).onAccessoryUnequipped(itemstack, playerIn);
         }

         if (Trinkets.Baubles && !TrinketsConfig.compat.xatItemsInTrinketGuiOnly) {
            IBaublesItemHandler baubles = (IBaublesItemHandler)this.player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, (EnumFacing)null);
            if (itemstack1.isEmpty() && !baubles.isEventBlocked() && slot instanceof TrinketSlot && itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
               ((IBauble)itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onUnequipped(itemstack, playerIn);
            }
         }

         ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
         if (index == 0) {
            playerIn.dropItem(itemstack2, false);
         }
      }

      return itemstack;
   }

   public boolean canMergeSlot(ItemStack stack, Slot slot) {
      return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }
}
