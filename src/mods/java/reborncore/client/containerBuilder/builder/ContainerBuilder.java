package reborncore.client.containerBuilder.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.common.tile.RebornMachineTile;

public class ContainerBuilder {
   private final String name;
   private Predicate<EntityPlayer> canInteract = (player) -> {
      return true;
   };
   final List<Slot> slots;
   final List<Range<Integer>> playerInventoryRanges;
   final List<Range<Integer>> tileInventoryRanges;
   final List<Pair<IntSupplier, IntConsumer>> shortValues;
   final List<Pair<IntSupplier, IntConsumer>> integerValues;
   final List<Pair<LongSupplier, LongConsumer>> longValues;
   final List<Pair<Supplier<FluidStack>, Consumer<FluidStack>>> fluidStackValues;
   final List<Pair<Supplier<Object>, Consumer<Object>>> objectValues;
   final List<Consumer<InventoryCrafting>> craftEvents;

   public ContainerBuilder(String name) {
      this.name = name;
      this.slots = new ArrayList();
      this.playerInventoryRanges = new ArrayList();
      this.tileInventoryRanges = new ArrayList();
      this.shortValues = new ArrayList();
      this.integerValues = new ArrayList();
      this.longValues = new ArrayList();
      this.fluidStackValues = new ArrayList();
      this.objectValues = new ArrayList();
      this.craftEvents = new ArrayList();
   }

   public ContainerBuilder interact(Predicate<EntityPlayer> canInteract) {
      this.canInteract = canInteract;
      return this;
   }

   public ContainerPlayerInventoryBuilder player(InventoryPlayer player) {
      return new ContainerPlayerInventoryBuilder(this, player);
   }

   public ContainerTileInventoryBuilder tile(IInventory tile) {
      return new ContainerTileInventoryBuilder(this, tile);
   }

   void addPlayerInventoryRange(Range<Integer> range) {
      this.playerInventoryRanges.add(range);
   }

   void addTileInventoryRange(Range<Integer> range) {
      this.tileInventoryRanges.add(range);
   }

   /** @deprecated */
   @Deprecated
   public BuiltContainer create() {
      BuiltContainer built = new BuiltContainer(this.name, this.canInteract, (RebornMachineTile)null);
      if (!this.shortValues.isEmpty()) {
         built.addShortSync(this.shortValues);
      }

      if (!this.integerValues.isEmpty()) {
         built.addIntegerSync(this.integerValues);
      }

      if (!this.longValues.isEmpty()) {
         built.addLongSync(this.longValues);
      }

      if (!this.fluidStackValues.isEmpty()) {
         built.addFluidStackSync(this.fluidStackValues);
      }

      if (!this.objectValues.isEmpty()) {
         built.addObjectSync(this.objectValues);
      }

      if (!this.craftEvents.isEmpty()) {
         built.addCraftEvents(this.craftEvents);
      }

      this.slots.forEach(built::addSlot);
      this.slots.clear();
      return built;
   }

   public BuiltContainer create(RebornMachineTile tile) {
      BuiltContainer built = new BuiltContainer(this.name, this.canInteract, tile);
      if (!this.shortValues.isEmpty()) {
         built.addShortSync(this.shortValues);
      }

      if (!this.integerValues.isEmpty()) {
         built.addIntegerSync(this.integerValues);
      }

      if (!this.longValues.isEmpty()) {
         built.addLongSync(this.longValues);
      }

      if (!this.fluidStackValues.isEmpty()) {
         built.addFluidStackSync(this.fluidStackValues);
      }

      if (!this.objectValues.isEmpty()) {
         built.addObjectSync(this.objectValues);
      }

      if (!this.craftEvents.isEmpty()) {
         built.addCraftEvents(this.craftEvents);
      }

      this.slots.forEach(built::addSlot);
      this.slots.clear();
      return built;
   }
}
