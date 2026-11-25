package mods.octarinecore.common.config;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001d\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0007J(\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0012H\u0016J\u0011\u0010\u0014\u001a\u00020\u0002*\u00020\rH\u0016\u00a2\u0006\u0002\u0010\u0015J\u0014\u0010\u0016\u001a\u00020\u0017*\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0002H\u0016R\u0011\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t\u00a8\u0006\u0019"},
   d2 = {"Lmods/octarinecore/common/config/ConfigPropertyLong;", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "", "min", "", "max", "default", "(III)V", "getDefault", "()I", "getMax", "getMin", "resolve", "Lnet/minecraftforge/common/config/Property;", "kotlin.jvm.PlatformType", "target", "Lnet/minecraftforge/common/config/Configuration;", "category", "", "name", "read", "(Lnet/minecraftforge/common/config/Property;)Ljava/lang/Long;", "write", "", "value", "BetterFoliage-MC1.12"}
)
public final class ConfigPropertyLong extends ConfigPropertyDelegate<Long> {
   private final int min;
   private final int max;
   private final int default;

   public Property resolve(@NotNull Configuration target, @NotNull String category, @NotNull String name) {
      Intrinsics.checkParameterIsNotNull(target, "target");
      Intrinsics.checkParameterIsNotNull(category, "category");
      Intrinsics.checkParameterIsNotNull(name, "name");
      Property var4 = target.get(category, name, this.default, (String)null);
      boolean var5 = false;
      boolean var6 = false;
      int $i$a$-apply-ConfigPropertyLong$resolve$1 = 0;
      var4.setMinValue(this.min);
      var4.setMaxValue(this.max);
      return var4;
   }

   @NotNull
   public Long read(@NotNull Property $this$read) {
      Intrinsics.checkParameterIsNotNull($this$read, "$this$read");
      Property var10000 = this.getProperty();
      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      return var10000.getLong();
   }

   public void write(@NotNull Property $this$write, long value) {
      Intrinsics.checkParameterIsNotNull($this$write, "$this$write");
      Property var10000 = this.getProperty();
      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      var10000.set(value);
   }

   public final int getMin() {
      return this.min;
   }

   public final int getMax() {
      return this.max;
   }

   public final int getDefault() {
      return this.default;
   }

   public ConfigPropertyLong(int min, int max, int var3) {
      this.min = min;
      this.max = max;
      this.default = default;
   }
}
