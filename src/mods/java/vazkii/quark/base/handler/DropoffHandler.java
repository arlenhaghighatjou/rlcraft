package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.api.IDropoffManager;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDisableDropoffClient;
import vazkii.quark.management.feature.ChestButtons;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

public final class DropoffHandler {
   public DropoffHandler() {
   }

   public static void dropoff(EntityPlayer player, boolean smart, boolean useContainer) {
      if (ModuleLoader.isFeatureEnabled(useContainer ? ChestButtons.class : StoreToChests.class) && !player.isSpectator()) {
         if (!useContainer && !player.getEntityWorld().getWorldInfo().getGameRulesInstance().getBoolean("quark_allowDropoff")) {
            disableClientDropoff(player);
         } else {
            (new DropoffHandler.Dropoff(player, smart, useContainer)).execute();
         }
      }
   }

   public static void restock(EntityPlayer player, boolean filtered) {
      if (ModuleLoader.isFeatureEnabled(ChestButtons.class) && !player.isSpectator()) {
         (new DropoffHandler.Restock(player, filtered)).execute();
      }
   }

   public static void disableClientDropoff(EntityPlayer player) {
      if (player instanceof EntityPlayerMP) {
         NetworkHandler.INSTANCE.sendTo(new MessageDisableDropoffClient(), (EntityPlayerMP)player);
      }

   }

   public static IItemHandler getInventory(EntityPlayer player, World world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      if (te == null) {
         return null;
      } else {
         boolean accept = isValidChest(player, te);
         if (accept) {
            Supplier<IItemHandler> supplier = () -> {
               IItemHandler innerRet = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
               if (innerRet == null && te instanceof IInventory) {
                  innerRet = new InvWrapper((IInventory)te);
               }

               return innerRet;
            };
            return IDropoffManager.hasProvider(te) ? IDropoffManager.getProvider(te).getDropoffItemHandler(supplier) : (IItemHandler)supplier.get();
         } else {
            return null;
         }
      }
   }

   private static boolean hasProvider(Object te) {
      return te instanceof TileEntity && IDropoffManager.hasProvider((TileEntity)te);
   }

   private static IDropoffManager getProvider(Object te) {
      return IDropoffManager.getProvider((TileEntity)te);
   }

   private static boolean accepts(Object te, EntityPlayer player) {
      return hasProvider(te) ? getProvider(te).acceptsDropoff(player) : false;
   }

   public static boolean isValidChest(EntityPlayer player, TileEntity te) {
      boolean accept = accepts(te, player);
      if (!accept) {
         accept = ChestButtons.overriddenDropoff(te);
      }

      if (te instanceof IInventory) {
         accept = accept && ((IInventory)te).isUsableByPlayer(player);
      }

      return accept;
   }

   public static boolean isValidChest(EntityPlayer player, IInventory te) {
      if (te instanceof TileEntity) {
         return isValidChest(player, (TileEntity)te);
      } else {
         return accepts(te, player) && te.isUsableByPlayer(player);
      }
   }

   public static boolean isValidChest(EntityPlayer player, IItemHandler te) {
      return accepts(te, player);
   }

   public static class ContainerWrapper extends InvWrapper {
      private final Container container;

      public static IItemHandler provideWrapper(Slot slot, Container container) {
         if (slot instanceof SlotItemHandler) {
            IItemHandler handler = ((SlotItemHandler)slot).getItemHandler();
            return DropoffHandler.hasProvider(handler) ? DropoffHandler.getProvider(handler).getDropoffItemHandler(() -> {
               return handler;
            }) : handler;
         } else {
            return provideWrapper(slot.inventory, container);
         }
      }

      public static IItemHandler provideWrapper(IInventory inv, Container container) {
         return (IItemHandler)(DropoffHandler.hasProvider(inv) ? DropoffHandler.getProvider(inv).getDropoffItemHandler(() -> {
            return new DropoffHandler.ContainerWrapper(inv, container);
         }) : new DropoffHandler.ContainerWrapper(inv, container));
      }

      private ContainerWrapper(IInventory inv, Container container) {
         super(inv);
         this.container = container;
      }

      @Nonnull
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         Slot containerSlot = this.getSlot(slot);
         return containerSlot != null && containerSlot.isItemValid(stack) ? super.insertItem(slot, stack, simulate) : stack;
      }

      private Slot getSlot(int slot) {
         return this.container.getSlotFromInventory(this.getInv(), slot);
      }
   }

   public static class Dropoff {
      public final EntityPlayer player;
      public final boolean smart;
      public final boolean useContainer;
      public final List<Pair<IItemHandler, Double>> itemHandlers = new ArrayList();

      public Dropoff(EntityPlayer player, boolean smart, boolean useContainer) {
         this.player = player;
         this.useContainer = useContainer;
         this.smart = smart;
      }

      public void execute() {
         this.locateItemHandlers();
         if (!this.itemHandlers.isEmpty()) {
            if (this.smart) {
               this.smartDropoff();
            } else {
               this.roughDropoff();
            }

            this.player.inventoryContainer.detectAndSendChanges();
            if (this.useContainer) {
               this.player.openContainer.detectAndSendChanges();
            }

         }
      }

      public void smartDropoff() {
         this.dropoff((stack, handler) -> {
            int slots = handler.getSlots();

            for(int i = 0; i < slots; ++i) {
               ItemStack stackAt = handler.getStackInSlot(i);
               if (!stackAt.isEmpty()) {
                  boolean itemEqual = stack.getItem() == stackAt.getItem();
                  boolean damageEqual = stack.getItemDamage() == stackAt.getItemDamage();
                  boolean nbtEqual = ItemStack.areItemStackTagsEqual(stackAt, stack);
                  if (itemEqual && damageEqual && nbtEqual) {
                     return true;
                  }

                  if (!stack.getHasSubtypes() && stack.isItemStackDamageable() && stack.getMaxStackSize() == 1 && itemEqual && nbtEqual) {
                     return true;
                  }
               }
            }

            return false;
         });
      }

      public void roughDropoff() {
         this.dropoff((stack, handler) -> {
            return true;
         });
      }

      public void locateItemHandlers() {
         if (this.useContainer) {
            Container c = this.player.openContainer;

            for(Slot s : c.inventorySlots) {
               IInventory inv = s.inventory;
               if (inv != this.player.inventory) {
                  this.itemHandlers.add(Pair.of(DropoffHandler.ContainerWrapper.provideWrapper(s, c), Double.valueOf(0.0D)));
                  break;
               }
            }
         } else {
            BlockPos playerPos = this.player.getPosition();
            int range = 6;

            for(int i = -range; i < range * 2 + 1; ++i) {
               for(int j = -range; j < range * 2 + 1; ++j) {
                  for(int k = -range; k < range * 2 + 1; ++k) {
                     BlockPos pos = playerPos.add(i, j, k);
                     this.findHandler(pos);
                  }
               }
            }

            this.itemHandlers.sort(Comparator.comparingDouble(Pair::getRight));
         }

      }

      public void findHandler(BlockPos pos) {
         IItemHandler handler = DropoffHandler.getInventory(this.player, this.player.getEntityWorld(), pos);
         if (handler != null) {
            this.itemHandlers.add(Pair.of(handler, Double.valueOf(this.player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D))));
         }

      }

      public void dropoff(DropoffHandler.DropoffPredicate predicate) {
         InventoryPlayer inv = this.player.inventory;

         for(int i = InventoryPlayer.getHotbarSize(); i < inv.mainInventory.size(); ++i) {
            ItemStack stackAt = inv.getStackInSlot(i);
            if (!stackAt.isEmpty() && !FavoriteItems.isItemFavorited(stackAt)) {
               ItemStack ret = this.insert(stackAt, predicate);
               if (!ItemStack.areItemStacksEqual(stackAt, ret)) {
                  inv.setInventorySlotContents(i, ret);
               }
            }
         }

      }

      public ItemStack insert(ItemStack stack, DropoffHandler.DropoffPredicate predicate) {
         ItemStack ret = stack.copy();

         for(Pair<IItemHandler, Double> pair : this.itemHandlers) {
            IItemHandler handler = (IItemHandler)pair.getLeft();
            ret = this.insertInHandler(handler, ret, predicate);
            if (ret.isEmpty()) {
               return ItemStack.EMPTY;
            }
         }

         return ret;
      }

      public ItemStack insertInHandler(IItemHandler handler, ItemStack stack, DropoffHandler.DropoffPredicate predicate) {
         if (predicate.apply(stack, handler)) {
            ItemStack retStack = ItemHandlerHelper.insertItemStacked(handler, stack, false);
            if (!retStack.isEmpty()) {
               retStack = retStack.copy();
               return retStack;
            } else {
               return retStack;
            }
         } else {
            return stack;
         }
      }
   }

   public interface DropoffPredicate {
      boolean apply(ItemStack var1, IItemHandler var2);
   }

   public static class PlayerInvWrapper extends InvWrapper {
      public PlayerInvWrapper(IInventory inv) {
         super(inv);
      }

      @Nonnull
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         if (stack.isEmpty()) {
            stack = stack.copy();
         }

         return super.insertItem(slot, stack, simulate);
      }

      public int getSlots() {
         return super.getSlots() - 5;
      }
   }

   public static class Restock extends DropoffHandler.Dropoff {
      public Restock(EntityPlayer player, boolean filtered) {
         super(player, filtered, true);
      }

      public void dropoff(DropoffHandler.DropoffPredicate predicate) {
         IItemHandler inv = (IItemHandler)((Pair)this.itemHandlers.get(0)).getLeft();
         IItemHandler playerInv = new DropoffHandler.PlayerInvWrapper(this.player.inventory);

         for(int i = inv.getSlots() - 1; i >= 0; --i) {
            ItemStack stackAt = inv.getStackInSlot(i);
            if (!stackAt.isEmpty()) {
               ItemStack copy = stackAt.copy();
               ItemStack ret = this.insertInHandler(playerInv, copy, predicate);
               if (!ItemStack.areItemStacksEqual(stackAt, ret)) {
                  inv.extractItem(i, stackAt.getMaxStackSize(), false);
                  if (!ret.isEmpty()) {
                     inv.insertItem(i, ret, false);
                  }
               }
            }
         }

      }
   }
}
