package reborncore.common.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.RebornCore;
import reborncore.api.tile.IContainerLayout;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;
import reborncore.common.util.ItemUtils;

public abstract class RebornContainer extends Container {
   private static HashMap<String, RebornContainer> containerMap = new HashMap();
   public HashMap<Integer, BaseSlot> slotMap = new HashMap();
   private Optional<TileEntity> baseTile = Optional.empty();

   /** @deprecated */
   @Deprecated
   public RebornContainer() {
   }

   public RebornContainer(TileEntity tileEntity) {
      this.baseTile = Optional.of(tileEntity);
   }

   @Nullable
   public static RebornContainer getContainerFromClass(Class<? extends RebornContainer> clazz, TileEntity tileEntity) {
      return createContainer(clazz, tileEntity, RebornCore.proxy.getPlayer());
   }

   public static RebornContainer createContainer(Class<? extends RebornContainer> clazz, TileEntity tileEntity, EntityPlayer player) {
      if (player == null && containerMap.containsKey(clazz.getCanonicalName())) {
         return (RebornContainer)containerMap.get(clazz.getCanonicalName());
      } else {
         try {
            RebornContainer container = null;

            for(Constructor<?> constructor : clazz.getConstructors()) {
               if (constructor.getParameterCount() == 0) {
                  container = (RebornContainer)clazz.newInstance();
                  if (container instanceof IContainerLayout) {
                     ((IContainerLayout)container).setTile(tileEntity);
                     ((IContainerLayout)container).addInventorySlots();
                  }
               } else if (constructor.getParameterCount() == 2) {
                  Class[] paramTypes = constructor.getParameterTypes();
                  if (paramTypes[0].isInstance(tileEntity) && paramTypes[1] == EntityPlayer.class) {
                     container = (RebornContainer)clazz.getDeclaredConstructor(tileEntity.getClass(), EntityPlayer.class).newInstance(tileEntity, player);
                  } else if (paramTypes[0] == EntityPlayer.class && paramTypes[1].isInstance(tileEntity)) {
                     container = (RebornContainer)clazz.getDeclaredConstructor(EntityPlayer.class, tileEntity.getClass()).newInstance(player, tileEntity);
                  }
               }
            }

            if (container == null) {
               RebornCore.logHelper.error("Failed to create container for " + clazz.getName() + " bad things may happen, please report to devs");
            }

            containerMap.put(clazz.getCanonicalName(), container);
            return container;
         } catch (InstantiationException var9) {
            var9.printStackTrace();
         } catch (IllegalAccessException var10) {
            var10.printStackTrace();
         } catch (InvocationTargetException var11) {
            var11.printStackTrace();
         } catch (NoSuchMethodException var12) {
            var12.printStackTrace();
         }

         return null;
      }
   }

   public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
      if (!stack1.isEmpty() && !stack2.isEmpty()) {
         if (!stack1.isItemEqual(stack2)) {
            return false;
         } else {
            return ItemStack.areItemStackTagsEqual(stack1, stack2);
         }
      } else {
         return false;
      }
   }

   protected Slot addSlotToContainer(Slot slotIn) {
      Slot slot = super.addSlotToContainer(slotIn);
      if (slot instanceof BaseSlot) {
         this.slotMap.put(Integer.valueOf(slot.getSlotIndex()), (BaseSlot)slot);
      }

      return slot;
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

   public void drawPlayersInv(EntityPlayer player, int x, int y) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new BaseSlot(player.inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
         }
      }

   }

   public void drawPlayersHotBar(EntityPlayer player, int x, int y) {
      for(int i = 0; i < 9; ++i) {
         this.addSlotToContainer(new BaseSlot(player.inventory, i, x + i * 18, y));
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      if (!this.baseTile.isPresent()) {
         return true;
      } else {
         World world = player.getEntityWorld();
         BlockPos pos = ((TileEntity)this.baseTile.get()).getPos();
         return world.getTileEntity(pos) == this.baseTile.get() && player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
      }
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
