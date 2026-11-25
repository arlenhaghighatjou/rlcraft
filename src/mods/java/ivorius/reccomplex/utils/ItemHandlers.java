package ivorius.reccomplex.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class ItemHandlers {
   public ItemHandlers() {
   }

   public static boolean has(ICapabilityProvider provider) {
      return provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null) || Arrays.stream(EnumFacing.VALUES).anyMatch((f) -> {
         return provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f);
      });
   }

   public static CombinedInvWrapper complete(ICapabilityProvider provider) {
      List<IItemHandlerModifiable> handlers = new ArrayList();
      Arrays.stream(EnumFacing.VALUES).forEach((f) -> {
         if (provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f)) {
            IItemHandler capability = (IItemHandler)provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f);
            if (capability instanceof IItemHandlerModifiable) {
               handlers.add((IItemHandlerModifiable)capability);
            }
         }

      });
      return new CombinedInvWrapper((IItemHandlerModifiable[])handlers.toArray(new IItemHandlerModifiable[0]));
   }

   public static boolean hasModifiable(@Nonnull ICapabilityProvider provider, @Nullable EnumFacing side) {
      return getModifiable(provider, side) != null;
   }

   public static IItemHandlerModifiable getModifiable(@Nonnull ICapabilityProvider provider, @Nullable EnumFacing side) {
      if (!provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
         return null;
      } else {
         IItemHandler capability = (IItemHandler)provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
         return capability instanceof IItemHandlerModifiable ? (IItemHandlerModifiable)capability : null;
      }
   }

   public static boolean hasModifiable(@Nonnull ICapabilityProvider provider) {
      return hasModifiable(provider, (EnumFacing)null);
   }

   public static IItemHandlerModifiable getModifiable(ICapabilityProvider provider) {
      return getModifiable(provider, (EnumFacing)null);
   }

   public static void clear(IItemHandlerModifiable itemHandler) {
      for(int i = 0; i < itemHandler.getSlots() * 5; ++i) {
         itemHandler.setStackInSlot(Math.max(i, itemHandler.getSlots()), ItemStack.EMPTY);
      }

   }
}
