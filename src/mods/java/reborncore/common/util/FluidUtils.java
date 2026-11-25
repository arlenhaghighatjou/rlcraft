package reborncore.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

public class FluidUtils {
   public FluidUtils() {
   }

   public static boolean fluidEquals(Fluid f1, Fluid f2) {
      if (f1.equals(f2)) {
         return true;
      } else if (f1 != null && f2 != null) {
         String s1 = f1.getName();
         String s2 = f2.getName();
         if (s1.startsWith("fluid")) {
            s1 = s1.replaceFirst("fluid", "");
         }

         if (s2.startsWith("fluid")) {
            s2 = s2.replaceFirst("fluid", "");
         }

         return s1.equals(s2);
      } else {
         return false;
      }
   }

   public static boolean fillContainers(IFluidHandler source, IInventory inventory, int inputSlot, int outputSlot, Fluid fluidToFill) {
      ItemStack inputStack = inventory.getStackInSlot(inputSlot);
      ItemStack outputStack = inventory.getStackInSlot(outputSlot);
      if (inputStack.isEmpty()) {
         return false;
      } else if (outputStack.getCount() >= outputStack.getMaxStackSize()) {
         return false;
      } else {
         ItemStack copy = inputStack.copy();
         copy.setCount(1);
         IFluidHandlerItem target = getFluidHandler(copy);
         if (target == null) {
            return false;
         } else {
            int targetCapacity = target.getTankProperties()[0].getCapacity();
            FluidStack simulated = FluidUtil.tryFluidTransfer(target, source, targetCapacity, false);
            if (simulated != null && simulated.amount >= targetCapacity) {
               if (!outputStack.isEmpty() && !ItemUtils.isItemEqual(getFilledContainer(fluidToFill, inputStack.copy()), outputStack, true, true)) {
                  return false;
               } else {
                  FluidUtil.tryFluidTransfer(target, source, targetCapacity, true);
                  inventory.setInventorySlotContents(inputSlot, ItemUtils.decreaseSize(inputStack));
                  if (outputStack.isEmpty()) {
                     inventory.setInventorySlotContents(outputSlot, target.getContainer());
                  } else {
                     ItemUtils.increaseSize(outputStack);
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static boolean drainContainers(IFluidHandler target, IInventory inventory, int inputSlot, int outputSlot) {
      ItemStack inputStack = inventory.getStackInSlot(inputSlot);
      ItemStack outputStack = inventory.getStackInSlot(outputSlot);
      if (inputStack.isEmpty()) {
         return false;
      } else if (outputStack.getCount() >= outputStack.getMaxStackSize()) {
         return false;
      } else {
         ItemStack copy = inputStack.copy();
         copy.setCount(1);
         IFluidHandlerItem source = getFluidHandler(copy);
         if (source == null) {
            return false;
         } else {
            int sourceCapacity = source.getTankProperties()[0].getCapacity();
            FluidStack simulated = FluidUtil.tryFluidTransfer(target, source, sourceCapacity, false);
            if (simulated != null && simulated.amount >= sourceCapacity) {
               if (!outputStack.isEmpty() && !ItemUtils.isItemEqual(getEmptyContainer(inputStack.copy()), outputStack, true, true)) {
                  return false;
               } else {
                  FluidUtil.tryFluidTransfer(target, source, sourceCapacity, true);
                  inventory.setInventorySlotContents(inputSlot, ItemUtils.decreaseSize(inputStack));
                  if (outputStack.isEmpty()) {
                     inventory.setInventorySlotContents(outputSlot, source.getContainer());
                  } else {
                     ItemUtils.increaseSize(outputStack);
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   @Nonnull
   public static ItemStack getEmptyContainer(ItemStack filled) {
      if (filled.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(filled);
         if (fluidHandler != null) {
            fluidHandler.drain(fluidHandler.getTankProperties()[0].getCapacity(), true);
         }

         return filled;
      }
   }

   @Nonnull
   public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
      if (fluid != null && !empty.isEmpty()) {
         IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(empty);
         fluidHandler.fill(new FluidStack(fluid, fluidHandler.getTankProperties()[0].getCapacity()), true);
         return empty;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Nullable
   public static IFluidHandlerItem getFluidHandler(@Nonnull ItemStack stack) {
      return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null) ? (IFluidHandlerItem)stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null) : null;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public static FluidStack getFluidStackInContainer(@Nonnull ItemStack container) {
      if (!container.isEmpty()) {
         container = container.copy();
         container.setCount(1);
         IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(container);
         if (fluidHandler != null) {
            return fluidHandler.drain(1000, false);
         }
      }

      return null;
   }

   @Nullable
   public static FluidStack getFluidContained(@Nonnull ItemStack container) {
      if (!container.isEmpty()) {
         container = ItemHandlerHelper.copyStackWithSize(container, 1);
         IFluidHandlerItem fluidHandler = getFluidHandler(container);
         if (fluidHandler != null) {
            return fluidHandler.drain(Integer.MAX_VALUE, false);
         }
      }

      return null;
   }

   public static String toFormattedString(FluidStack fluidStack) {
      return fluidStack.getFluid() == null ? fluidStack.amount + "(mb)x(null)@(unknown)" : fluidStack.toString();
   }
}
