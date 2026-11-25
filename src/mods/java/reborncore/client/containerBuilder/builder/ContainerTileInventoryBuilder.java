package reborncore.client.containerBuilder.builder;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.RebornCore;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.containerBuilder.builder.slot.FilteredSlot;
import reborncore.client.containerBuilder.builder.slot.UpgradeSlot;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;
import reborncore.client.gui.slots.SlotOutput;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;

public class ContainerTileInventoryBuilder {
   private final IInventory tile;
   private final ContainerBuilder parent;
   private final int rangeStart;

   ContainerTileInventoryBuilder(ContainerBuilder parent, IInventory tile) {
      this.tile = tile;
      this.parent = parent;
      this.rangeStart = parent.slots.size();
      if (tile instanceof IUpgradeable) {
         this.upgradeSlots((IUpgradeable)tile);
      }

   }

   public ContainerTileInventoryBuilder slot(int index, int x, int y) {
      this.parent.slots.add(new BaseSlot(this.tile, index, x, y));
      return this;
   }

   public ContainerTileInventoryBuilder slot(int index, int x, int y, Predicate<ItemStack> filter) {
      this.parent.slots.add(new BaseSlot(this.tile, index, x, y, filter));
      return this;
   }

   public ContainerTileInventoryBuilder outputSlot(int index, int x, int y) {
      this.parent.slots.add(new SlotOutput(this.tile, index, x, y));
      return this;
   }

   public ContainerTileInventoryBuilder fakeSlot(int index, int x, int y) {
      this.parent.slots.add(new SlotFake(this.tile, index, x, y, false, false, Integer.MAX_VALUE));
      return this;
   }

   public ContainerTileInventoryBuilder filterSlot(int index, int x, int y, Predicate<ItemStack> filter) {
      this.parent.slots.add((new FilteredSlot(this.tile, index, x, y)).setFilter(filter));
      return this;
   }

   public ContainerTileInventoryBuilder energySlot(int index, int x, int y) {
      this.parent.slots.add((new FilteredSlot(this.tile, index, x, y)).setFilter(ExternalPowerSystems::isPoweredItem));
      return this;
   }

   public ContainerTileInventoryBuilder fluidSlot(int index, int x, int y) {
      this.parent.slots.add((new FilteredSlot(this.tile, index, x, y)).setFilter((stack) -> {
         return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
      }));
      return this;
   }

   public ContainerTileInventoryBuilder fuelSlot(int index, int x, int y) {
      this.parent.slots.add(new SlotFurnaceFuel(this.tile, index, x, y));
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ContainerTileInventoryBuilder upgradeSlot(int index, int x, int y) {
      this.parent.slots.add((new FilteredSlot(this.tile, index, x, y)).setFilter((stack) -> {
         return stack.getItem() instanceof IUpgrade;
      }));
      return this;
   }

   private ContainerTileInventoryBuilder upgradeSlots(IUpgradeable upgradeable) {
      if (upgradeable.canBeUpgraded()) {
         for(int i = 0; i < upgradeable.getUpgradeSlotCount(); ++i) {
            this.parent.slots.add(new UpgradeSlot(upgradeable.getUpgradeInventory(), i, -18, i * 18 + 12));
         }
      }

      return this;
   }

   public ContainerTileInventoryBuilder syncShortValue(IntSupplier supplier, IntConsumer setter) {
      this.parent.shortValues.add(Pair.of(supplier, setter));
      return this;
   }

   public ContainerTileInventoryBuilder syncIntegerValue(IntSupplier supplier, IntConsumer setter) {
      this.parent.integerValues.add(Pair.of(supplier, setter));
      return this;
   }

   public ContainerTileInventoryBuilder syncLongValue(LongSupplier supplier, LongConsumer setter) {
      this.parent.longValues.add(Pair.of(supplier, setter));
      return this;
   }

   public ContainerTileInventoryBuilder syncTank(Supplier<FluidStack> supplier, Consumer<FluidStack> setter) {
      this.parent.fluidStackValues.add(Pair.of(supplier, setter));
      return this;
   }

   public ContainerTileInventoryBuilder sync(Supplier<Object> supplier, Consumer<Object> setter) {
      this.parent.objectValues.add(Pair.of(supplier, setter));
      return this;
   }

   public ContainerTileInventoryBuilder syncEnergyValue() {
      if (this.tile instanceof TilePowerAcceptor) {
         IntSupplier var10001 = () -> {
            return (int)((TilePowerAcceptor)this.tile).getEnergy();
         };
         TilePowerAcceptor var10002 = (TilePowerAcceptor)this.tile;
         ((TilePowerAcceptor)this.tile).getClass();
         ContainerTileInventoryBuilder var10000 = this.syncIntegerValue(var10001, var10002::setEnergy);
         var10001 = () -> {
            return (int)((TilePowerAcceptor)this.tile).extraPowerStoage;
         };
         var10002 = (TilePowerAcceptor)this.tile;
         ((TilePowerAcceptor)this.tile).getClass();
         var10000 = var10000.syncIntegerValue(var10001, var10002::setExtraPowerStoage);
         var10001 = () -> {
            return (int)((TilePowerAcceptor)this.tile).getPowerChange();
         };
         var10002 = (TilePowerAcceptor)this.tile;
         ((TilePowerAcceptor)this.tile).getClass();
         return var10000.syncIntegerValue(var10001, var10002::setPowerChange);
      } else {
         RebornCore.logHelper.error(this.tile + " is not an instance of TilePowerAcceptor! Energy cannot be synced.");
         return this;
      }
   }

   public ContainerTileInventoryBuilder syncCrafterValue() {
      if (this.tile instanceof IRecipeCrafterProvider) {
         return this.syncIntegerValue(() -> {
            return ((IRecipeCrafterProvider)this.tile).getRecipeCrafter().currentTickTime;
         }, (currentTickTime) -> {
            ((IRecipeCrafterProvider)this.tile).getRecipeCrafter().currentTickTime = currentTickTime;
         }).syncIntegerValue(() -> {
            return ((IRecipeCrafterProvider)this.tile).getRecipeCrafter().currentNeededTicks;
         }, (currentNeededTicks) -> {
            ((IRecipeCrafterProvider)this.tile).getRecipeCrafter().currentNeededTicks = currentNeededTicks;
         });
      } else {
         RebornCore.logHelper.error(this.tile + " is not an instance of IRecipeCrafterProvider! Craft progress cannot be synced.");
         return this;
      }
   }

   public ContainerTileInventoryBuilder onCraft(Consumer<InventoryCrafting> onCraft) {
      this.parent.craftEvents.add(onCraft);
      return this;
   }

   public ContainerBuilder addInventory() {
      this.parent.tileInventoryRanges.add(Range.between(Integer.valueOf(this.rangeStart), Integer.valueOf(this.parent.slots.size() - 1)));
      return this.parent;
   }
}
