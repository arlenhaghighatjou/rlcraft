package ejektaflex.bountiful.registry;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.compat.FacadeGameStages;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J*\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0005¨\u0006\f"},
   d2 = {"Lejektaflex/bountiful/registry/RewardRegistry;", "Lejektaflex/bountiful/registry/ValueRegistry;", "Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "()V", "validRewards", "", "world", "Lnet/minecraft/world/World;", "worth", "", "alreadyPicked", "", "Bountiful"}
)
public final class RewardRegistry extends ValueRegistry<PickedEntryStack> {
   public static final RewardRegistry INSTANCE;

   @NotNull
   public final List<PickedEntryStack> validRewards(@NotNull World world, int worth, @NotNull List<String> alreadyPicked) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(alreadyPicked, "alreadyPicked");
      Iterable $receiver$iv = (Iterable)this.validRewards(world);
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $receiver$iv) {
         PickedEntryStack it = (PickedEntryStack)element$iv$iv;
         if (it.getGenericPick().getMinValueOfPick() <= worth && !alreadyPicked.contains(it.getContent())) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      $receiver$iv = (Iterable)((List)destination$iv$iv);
      Comparator var14 = new RewardRegistry$validRewards$$inlined$sortedBy$1();
      return CollectionsKt.sortedWith($receiver$iv, var14);
   }

   @NotNull
   public final List<PickedEntryStack> validRewards(@NotNull World world) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      if (world.isRemote) {
         return CollectionsKt.emptyList();
      } else {
         List var10000;
         if (Bountiful.INSTANCE.getConfig().isRunningGameStages()) {
            Iterable $receiver$iv = (Iterable)this.getItems();
            Collection destination$iv$iv = (Collection)(new ArrayList());

            for(Object element$iv$iv : $receiver$iv) {
               PickedEntryStack it = (PickedEntryStack)element$iv$iv;
               if (FacadeGameStages.INSTANCE.anyPlayerHas(world, it.getGenericPick().requiredStages())) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            var10000 = (List)destination$iv$iv;
         } else {
            var10000 = this.getItems();
         }

         return var10000;
      }
   }

   private RewardRegistry() {
   }

   static {
      RewardRegistry var0 = new RewardRegistry();
      INSTANCE = var0;
   }
}
