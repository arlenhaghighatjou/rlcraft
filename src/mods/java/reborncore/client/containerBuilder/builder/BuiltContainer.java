package reborncore.client.containerBuilder.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.client.containerBuilder.IRightClickHandler;
import reborncore.client.gui.slots.SlotFake;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.ItemUtils;

public class BuiltContainer extends Container implements IExtendedContainerListener {
   private final String name;
   private final Predicate<EntityPlayer> canInteract;
   private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Short>> shortValues;
   private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Integer>> integerValues;
   private final ArrayList<MutableTriple<LongSupplier, LongConsumer, Long>> longValues;
   private final ArrayList<MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack>> fluidStackValues;
   private final ArrayList<MutableTriple<Supplier<Object>, Consumer<Object>, Object>> objectValues;
   private List<Consumer<InventoryCrafting>> craftEvents;
   private Integer[] integerParts;
   private final RebornMachineTile tile;

   public BuiltContainer(String name, Predicate<EntityPlayer> canInteract, RebornMachineTile tile) {
      this.name = name;
      this.canInteract = canInteract;
      this.shortValues = new ArrayList();
      this.integerValues = new ArrayList();
      this.longValues = new ArrayList();
      this.fluidStackValues = new ArrayList();
      this.objectValues = new ArrayList();
      this.tile = tile;
   }

   public void addShortSync(List<Pair<IntSupplier, IntConsumer>> syncables) {
      for(Pair<IntSupplier, IntConsumer> syncable : syncables) {
         this.shortValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), Short.valueOf((short)0)));
      }

      this.shortValues.trimToSize();
   }

   public void addLongSync(List<Pair<LongSupplier, LongConsumer>> syncables) {
      for(Pair<LongSupplier, LongConsumer> syncable : syncables) {
         this.longValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), Long.valueOf(0L)));
      }

      this.longValues.trimToSize();
   }

   public void addIntegerSync(List<Pair<IntSupplier, IntConsumer>> syncables) {
      for(Pair<IntSupplier, IntConsumer> syncable : syncables) {
         this.integerValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), Integer.valueOf(0)));
      }

      this.integerValues.trimToSize();
      this.integerParts = new Integer[this.integerValues.size()];
   }

   public void addFluidStackSync(List<Pair<Supplier<FluidStack>, Consumer<FluidStack>>> syncables) {
      for(Pair<Supplier<FluidStack>, Consumer<FluidStack>> syncable : syncables) {
         this.fluidStackValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (Object)null));
      }

      this.fluidStackValues.trimToSize();
   }

   public void addObjectSync(List<Pair<Supplier<Object>, Consumer<Object>>> syncables) {
      for(Pair<Supplier<Object>, Consumer<Object>> syncable : syncables) {
         this.objectValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (Object)null));
      }

      this.objectValues.trimToSize();
   }

   public void addCraftEvents(List<Consumer<InventoryCrafting>> craftEvents) {
      this.craftEvents = craftEvents;
   }

   public void addSlot(Slot slot) {
      this.addSlotToContainer(slot);
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tile != null ? this.tile.isUsableByPlayer(playerIn) : this.canInteract.test(playerIn);
   }

   public final void onCraftMatrixChanged(IInventory inv) {
      if (!this.craftEvents.isEmpty()) {
         this.craftEvents.forEach((consumer) -> {
            consumer.accept((InventoryCrafting)inv);
         });
      }

   }

   public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
      if (dragType == 1 && slotId > 0 && slotId < 1000) {
         Slot slot = (Slot)this.inventorySlots.get(slotId);
         if (slot instanceof IRightClickHandler && ((IRightClickHandler)slot).handleRightClick(slot.getSlotIndex(), player, this)) {
            return ItemStack.EMPTY;
         }
      }

      return super.slotClick(slotId, dragType, clickTypeIn, player);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(IContainerListener listener : this.listeners) {
         int i = 0;
         if (!this.shortValues.isEmpty()) {
            for(MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues) {
               short supplied = (short)((IntSupplier)value.getLeft()).getAsInt();
               if (supplied != ((Short)value.getRight()).shortValue()) {
                  listener.sendWindowProperty(this, i, supplied);
                  value.setRight(Short.valueOf(supplied));
               }

               ++i;
            }
         }

         if (!this.integerValues.isEmpty()) {
            for(MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues) {
               int supplied = ((IntSupplier)value.getLeft()).getAsInt();
               if (supplied != ((Integer)value.getRight()).intValue()) {
                  listener.sendWindowProperty(this, i, supplied >> 16);
                  listener.sendWindowProperty(this, i + 1, (short)(supplied & '\uffff'));
                  value.setRight(Integer.valueOf(supplied));
               }

               i += 2;
            }
         }

         if (!this.longValues.isEmpty()) {
            int longs = 0;

            for(MutableTriple<LongSupplier, LongConsumer, Long> value : this.longValues) {
               long supplied = ((LongSupplier)value.getLeft()).getAsLong();
               if (supplied != ((Long)value.getRight()).longValue()) {
                  this.sendLong(listener, this, longs, supplied);
                  value.setRight(Long.valueOf(supplied));
               }

               ++longs;
            }
         }

         if (!this.fluidStackValues.isEmpty()) {
            int fluidStacks = 0;

            for(MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack> value : this.fluidStackValues) {
               FluidStack supplied = (FluidStack)((Supplier)value.getLeft()).get();
               this.sendFluidStack(listener, this, fluidStacks, supplied);
               value.setRight(supplied);
               ++fluidStacks;
            }
         }

         if (!this.objectValues.isEmpty()) {
            int objects = 0;

            for(MutableTriple<Supplier<Object>, Consumer<Object>, Object> value : this.objectValues) {
               Object supplied = value.getLeft();
               if (((Supplier)supplied).get() != value.getRight()) {
                  this.sendObject(listener, this, objects, ((Supplier)supplied).get());
                  value.setRight(((Supplier)supplied).get());
               }

               ++objects;
            }
         }
      }

   }

   public void addListener(IContainerListener listener) {
      super.addListener(listener);
      int i = 0;
      if (!this.shortValues.isEmpty()) {
         for(MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues) {
            short supplied = (short)((IntSupplier)value.getLeft()).getAsInt();
            listener.sendWindowProperty(this, i, supplied);
            value.setRight(Short.valueOf(supplied));
            ++i;
         }
      }

      if (!this.integerValues.isEmpty()) {
         for(MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues) {
            int supplied = ((IntSupplier)value.getLeft()).getAsInt();
            listener.sendWindowProperty(this, i, supplied >> 16);
            listener.sendWindowProperty(this, i + 1, (short)(supplied & '\uffff'));
            value.setRight(Integer.valueOf(supplied));
            i += 2;
         }
      }

      if (!this.longValues.isEmpty()) {
         int longs = 0;

         for(MutableTriple<LongSupplier, LongConsumer, Long> value : this.longValues) {
            long supplied = ((LongSupplier)value.getLeft()).getAsLong();
            this.sendLong(listener, this, longs, supplied);
            value.setRight(Long.valueOf(supplied));
            ++longs;
         }
      }

      if (!this.fluidStackValues.isEmpty()) {
         int fluidStacks = 0;

         for(MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack> value : this.fluidStackValues) {
            FluidStack supplied = (FluidStack)((Supplier)value.getLeft()).get();
            this.sendFluidStack(listener, this, fluidStacks, supplied);
            value.setRight(supplied);
            ++fluidStacks;
         }
      }

      if (!this.objectValues.isEmpty()) {
         int objects = 0;

         for(MutableTriple<Supplier<Object>, Consumer<Object>, Object> value : this.objectValues) {
            Object supplied = value.getLeft();
            this.sendObject(listener, this, objects, ((Supplier)supplied).get());
            value.setRight(supplied);
            ++objects;
         }
      }

   }

   public void handleLong(int var, long value) {
      ((LongConsumer)((MutableTriple)this.longValues.get(var)).getMiddle()).accept(value);
   }

   public void handleFluidStack(int var, FluidStack value) {
      ((Consumer)((MutableTriple)this.fluidStackValues.get(var)).getMiddle()).accept(value);
   }

   public void handleObject(int var, Object value) {
      ((Consumer)((MutableTriple)this.objectValues.get(var)).getMiddle()).accept(value);
   }

   @SideOnly(Side.CLIENT)
   public void updateProgressBar(int id, int value) {
      if (id < this.shortValues.size()) {
         ((IntConsumer)((MutableTriple)this.shortValues.get(id)).getMiddle()).accept((short)value);
         ((MutableTriple)this.shortValues.get(id)).setRight(Short.valueOf((short)value));
      } else if (id - this.shortValues.size() < this.integerValues.size() * 2) {
         if ((id - this.shortValues.size()) % 2 == 0) {
            this.integerParts[(id - this.shortValues.size()) / 2] = value;
         } else {
            ((IntConsumer)((MutableTriple)this.integerValues.get((id - this.shortValues.size()) / 2)).getMiddle()).accept((this.integerParts[(id - this.shortValues.size()) / 2].intValue() & '\uffff') << 16 | value & '\uffff');
         }
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
      Slot sourceSlot = (Slot)this.inventorySlots.get(sourceSlotIndex);
      if (sourceSlot != null && sourceSlot.getHasStack()) {
         ItemStack sourceItemStack = sourceSlot.getStack();
         int oldSourceItemStackSize = ItemUtils.getSize(sourceItemStack);
         ItemStack destinationStack = sourceSlot.inventory == player.inventory ? this.handlePlayerSlotShiftClick(player, sourceItemStack) : this.handleGUISlotShiftClick(player, sourceItemStack);
         if (ItemUtils.isEmpty(destinationStack) || ItemUtils.getSize(destinationStack) != oldSourceItemStackSize) {
            sourceSlot.putStack(destinationStack);
            sourceSlot.onTake(player, sourceItemStack);
            if (!player.getEntityWorld().isRemote) {
               this.detectAndSendChanges();
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public String getName() {
      return this.name;
   }

   protected ItemStack handlePlayerSlotShiftClick(EntityPlayer player, ItemStack sourceItemStack) {
      for(int pass = 0; pass < 4 && !ItemUtils.isEmpty(sourceItemStack); ++pass) {
         for(Slot targetSlot : this.inventorySlots) {
            if (targetSlot.inventory != player.inventory && isValidTargetSlot(targetSlot, sourceItemStack, pass % 2 == 1, pass < 2)) {
               sourceItemStack = transfer(sourceItemStack, targetSlot);
               if (ItemUtils.isEmpty(sourceItemStack)) {
                  break;
               }
            }
         }
      }

      return sourceItemStack;
   }

   protected ItemStack handleGUISlotShiftClick(EntityPlayer player, ItemStack sourceItemStack) {
      for(int pass = 0; pass < 2 && !ItemUtils.isEmpty(sourceItemStack); ++pass) {
         ListIterator<Slot> it = this.inventorySlots.listIterator(this.inventorySlots.size());

         while(it.hasPrevious()) {
            Slot targetSlot = (Slot)it.previous();
            if (targetSlot.inventory == player.inventory && isValidTargetSlot(targetSlot, sourceItemStack, pass == 1, false)) {
               sourceItemStack = transfer(sourceItemStack, targetSlot);
               if (ItemUtils.isEmpty(sourceItemStack)) {
                  break;
               }
            }
         }
      }

      return sourceItemStack;
   }

   protected static boolean isValidTargetSlot(Slot slot, ItemStack itemStack, boolean allowEmpty, boolean requireInputOnly) {
      if (slot instanceof SlotFake) {
         return false;
      } else if (!slot.isItemValid(itemStack)) {
         return false;
      } else if (!allowEmpty && !slot.getHasStack()) {
         return false;
      } else {
         return !requireInputOnly || slot.isItemValid(itemStack);
      }
   }

   protected static ItemStack transfer(ItemStack itemStack, Slot destination) {
      int amount = getTransferAmount(itemStack, destination);
      if (amount <= 0) {
         return itemStack;
      } else {
         ItemStack destinationStack = destination.getStack();
         destination.putStack(ItemUtils.isEmpty(destinationStack) ? ItemUtils.copyWithSize(itemStack, amount) : ItemUtils.increaseSize(destinationStack, amount));
         return ItemUtils.decreaseSize(itemStack, amount);
      }
   }

   protected static int getTransferAmount(ItemStack itemStack, Slot destination) {
      int maxAmount = Math.min(destination.inventory.getInventoryStackLimit(), destination.getSlotStackLimit());
      maxAmount = Math.min(maxAmount, itemStack.isStackable() ? itemStack.getMaxStackSize() : 1);
      ItemStack destinationStack = destination.getStack();
      if (!ItemUtils.isEmpty(destinationStack)) {
         if (!ItemUtils.isItemEqual(itemStack, destinationStack, true, true)) {
            return 0;
         }

         maxAmount -= ItemUtils.getSize(destinationStack);
      }

      return Math.min(maxAmount, ItemUtils.getSize(itemStack));
   }
}
