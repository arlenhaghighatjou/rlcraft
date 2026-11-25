package wiresegal.classyhats.compat.jei;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.container.ContainerHatInventory;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\bH\u0016J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0006\u001a\u00020\u0002H\u0016J\b\u0010\f\u001a\u00020\rH\u0016J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0006\u001a\u00020\u0002H\u0016\u00a8\u0006\u000f"},
   d2 = {"Lwiresegal/classyhats/compat/jei/HatInvRecipeTransferInfo;", "Lmezz/jei/api/recipe/transfer/IRecipeTransferInfo;", "Lwiresegal/classyhats/container/ContainerHatInventory;", "()V", "canHandle", "", "container", "getContainerClass", "Ljava/lang/Class;", "getInventorySlots", "", "Lnet/minecraft/inventory/Slot;", "getRecipeCategoryUid", "", "getRecipeSlots", "classyhats"}
)
public final class HatInvRecipeTransferInfo implements IRecipeTransferInfo<ContainerHatInventory> {
   @NotNull
   public Class<ContainerHatInventory> getContainerClass() {
      return ContainerHatInventory.class;
   }

   @NotNull
   public String getRecipeCategoryUid() {
      return "minecraft.crafting";
   }

   public boolean canHandle(@NotNull ContainerHatInventory container) {
      Intrinsics.checkParameterIsNotNull(container, "container");
      return true;
   }

   @NotNull
   public List<Slot> getRecipeSlots(@NotNull ContainerHatInventory container) {
      Intrinsics.checkParameterIsNotNull(container, "container");
      return container.inventorySlots.subList(1, 5);
   }

   @NotNull
   public List<Slot> getInventorySlots(@NotNull ContainerHatInventory container) {
      Intrinsics.checkParameterIsNotNull(container, "container");
      return container.inventorySlots.subList(10, 46);
   }

   public HatInvRecipeTransferInfo() {
   }
}
