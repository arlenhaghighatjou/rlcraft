package crafttweaker.mc1120.container;

import crafttweaker.api.container.IContainer;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class MCContainer implements IContainer {
   private final Container container;

   public MCContainer(Container container) {
      this.container = container;
   }

   public int getContainerSize() {
      return this.container.inventorySlots.size();
   }

   public IItemStack getStack(int i) {
      return CraftTweakerMC.getIItemStack((ItemStack)this.container.inventoryItemStacks.get(i));
   }

   public void setStack(int i, IItemStack stack) {
      this.container.putStackInSlot(i, CraftTweakerMC.getItemStack(stack));
   }

   public Container getInternal() {
      return this.container;
   }

   public Iterator<IItemStack> iterator() {
      return this.container.inventoryItemStacks.stream().map(CraftTweakerMC::getIItemStack).iterator();
   }

   public String asString() {
      return this.container.toString();
   }

   public String toString() {
      return "MCContainer: " + this.asString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCContainer that = (MCContainer)o;
         return Objects.equals(this.container, that.container);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.container});
   }
}
