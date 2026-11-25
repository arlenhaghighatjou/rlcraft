package ejektaflex.bountiful.api.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000.\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\u0015\u0010\u000f\u001a\u00020\f*\u00020\b2\u0006\u0010\u0010\u001a\u00020\u0002H\u0086\u0002\u001a\u001d\u0010\u0011\u001a\u00020\u0012*\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\fH\u0086\u0002\"\u001b\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\"\u0015\u0010\u0006\u001a\u00020\u0007*\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\n\"\u001b\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0001*\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0014"},
   d2 = {"filledSlots", "", "", "Lnet/minecraftforge/items/IItemHandlerModifiable;", "getFilledSlots", "(Lnet/minecraftforge/items/IItemHandlerModifiable;)Ljava/util/List;", "slotRange", "Lkotlin/ranges/IntRange;", "Lnet/minecraftforge/items/IItemHandler;", "getSlotRange", "(Lnet/minecraftforge/items/IItemHandler;)Lkotlin/ranges/IntRange;", "stacks", "Lnet/minecraft/item/ItemStack;", "getStacks", "(Lnet/minecraftforge/items/IItemHandler;)Ljava/util/List;", "get", "slotNum", "set", "", "stack", "Bountiful_api"}
)
public final class ExtItemHandlerKt {
   @NotNull
   public static final ItemStack get(@NotNull IItemHandler $receiver, int slotNum) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      ItemStack var10000 = $receiver.getStackInSlot(slotNum);
      Intrinsics.checkExpressionValueIsNotNull(var10000, "getStackInSlot(slotNum)");
      return var10000;
   }

   public static final void set(@NotNull IItemHandlerModifiable $receiver, int slotNum, @NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      $receiver.setStackInSlot(slotNum, stack);
   }

   @NotNull
   public static final List<ItemStack> getStacks(@NotNull IItemHandler $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Iterable $receiver$iv = (Iterable)getSlotRange($receiver);
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
      Iterator var4 = $receiver$iv.iterator();

      while(var4.hasNext()) {
         int item$iv$iv = ((IntIterator)var4).nextInt();
         ItemStack var11 = get($receiver, item$iv$iv);
         destination$iv$iv.add(var11);
      }

      return (List)destination$iv$iv;
   }

   @NotNull
   public static final IntRange getSlotRange(@NotNull IItemHandler $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      return RangesKt.until(0, $receiver.getSlots());
   }

   @NotNull
   public static final List<Integer> getFilledSlots(@NotNull IItemHandlerModifiable $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Iterable $receiver$iv = (Iterable)getSlotRange((IItemHandler)$receiver);
      Collection destination$iv$iv = (Collection)(new ArrayList());
      Iterator var5 = $receiver$iv.iterator();

      while(var5.hasNext()) {
         int element$iv$iv$iv = ((IntIterator)var5).nextInt();
         ItemStack itis = get((IItemHandler)$receiver, element$iv$iv$iv);
         Integer var10000 = !Intrinsics.areEqual(itis, ItemStack.EMPTY) && !Intrinsics.areEqual(itis.getItem(), Items.AIR) ? element$iv$iv$iv : null;
         if (var10000 != null) {
            Integer var11 = var10000;
            destination$iv$iv.add(var11);
         }
      }

      return (List)destination$iv$iv;
   }
}
