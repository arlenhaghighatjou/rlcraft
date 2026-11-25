package invtweaks.container;

import invtweaks.api.container.ContainerSection;
import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IContainerManager {
   int DROP_SLOT = -999;

   boolean move(ContainerSection var1, int var2, ContainerSection var3, int var4);

   boolean moveSome(ContainerSection var1, int var2, ContainerSection var3, int var4, int var5);

   default boolean drop(ContainerSection srcSection, int srcIndex) {
      return this.move(srcSection, srcIndex, (ContainerSection)null, -999);
   }

   default boolean dropSome(ContainerSection srcSection, int srcIndex, int amount) {
      return this.moveSome(srcSection, srcIndex, (ContainerSection)null, -999, amount);
   }

   boolean putHoldItemDown(ContainerSection var1, int var2);

   default void leftClick(ContainerSection section, int index) {
      this.click(section, index, false);
   }

   default void rightClick(ContainerSection section, int index) {
      this.click(section, index, true);
   }

   void click(ContainerSection var1, int var2, boolean var3);

   boolean hasSection(ContainerSection var1);

   List<Slot> getSlots(ContainerSection var1);

   int getSize();

   int getSize(ContainerSection var1);

   int getFirstEmptyIndex(ContainerSection var1);

   boolean isSlotEmpty(ContainerSection var1, int var2);

   @Nullable
   Slot getSlot(ContainerSection var1, int var2);

   default int getSlotIndex(int slotNumber) {
      return this.getSlotIndex(slotNumber, false);
   }

   int getSlotIndex(int var1, boolean var2);

   @Nullable
   ContainerSection getSlotSection(int var1);

   @NotNull
   ItemStack getItemStack(ContainerSection var1, int var2);

   Container getContainer();

   void applyChanges();
}
