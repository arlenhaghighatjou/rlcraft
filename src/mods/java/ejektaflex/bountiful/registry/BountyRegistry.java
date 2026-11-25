package ejektaflex.bountiful.registry;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.compat.FacadeGameStages;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
   d2 = {"Lejektaflex/bountiful/registry/BountyRegistry;", "Lejektaflex/bountiful/registry/ValueRegistry;", "Lejektaflex/bountiful/api/logic/pickable/PickableEntry;", "()V", "validBounties", "", "world", "Lnet/minecraft/world/World;", "Bountiful"}
)
public final class BountyRegistry extends ValueRegistry<PickableEntry> {
   public static final BountyRegistry INSTANCE;

   @NotNull
   public final List<PickableEntry> validBounties(@NotNull World world) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      List var10000;
      if (Bountiful.INSTANCE.getConfig().isRunningGameStages()) {
         Iterable $receiver$iv = (Iterable)this.getItems();
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $receiver$iv) {
            PickableEntry it = (PickableEntry)element$iv$iv;
            if (FacadeGameStages.INSTANCE.anyPlayerHas(world, it.requiredStages())) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         var10000 = (List)destination$iv$iv;
      } else {
         var10000 = this.getItems();
      }

      return var10000;
   }

   private BountyRegistry() {
   }

   static {
      BountyRegistry var0 = new BountyRegistry();
      INSTANCE = var0;
   }
}
