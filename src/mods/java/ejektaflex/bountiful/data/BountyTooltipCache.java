package ejektaflex.bountiful.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010%\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0005J\u0011\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0002H\u0096\u0002J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\u0002H\u0016J\u001e\u0010\r\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\u00022\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0016R \u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"},
   d2 = {"Lejektaflex/bountiful/data/BountyTooltipCache;", "Lejektaflex/bountiful/data/ICache;", "Lnet/minecraft/item/ItemStack;", "", "", "()V", "cache", "", "contains", "", "item", "invalidate", "", "store", "data", "Bountiful"}
)
public final class BountyTooltipCache implements ICache<ItemStack, List<String>> {
   private static final Map<ItemStack, List<String>> cache = (Map)(new LinkedHashMap());
   public static final BountyTooltipCache INSTANCE;

   public boolean contains(@NotNull ItemStack item) {
      Intrinsics.checkParameterIsNotNull(item, "item");
      return cache.keySet().contains(item);
   }

   public void store(@NotNull ItemStack item, @NotNull List<String> data) {
      Intrinsics.checkParameterIsNotNull(item, "item");
      Intrinsics.checkParameterIsNotNull(data, "data");
      Map var3 = cache;
      var3.put(item, data);
   }

   public void invalidate(@NotNull ItemStack item) {
      Intrinsics.checkParameterIsNotNull(item, "item");
      if (this.contains(item)) {
         cache.remove(item);
      }

   }

   private BountyTooltipCache() {
   }

   static {
      BountyTooltipCache var0 = new BountyTooltipCache();
      INSTANCE = var0;
   }
}
