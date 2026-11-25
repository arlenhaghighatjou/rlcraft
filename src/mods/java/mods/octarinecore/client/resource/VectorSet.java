package mods.octarinecore.client.resource;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import mods.octarinecore.common.Double3;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0002\u0010\u0007J\u0011\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0003H\u0086\u0002J\b\u0010\u0013\u001a\u00020\u0014H\u0016R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0019\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0015"},
   d2 = {"Lmods/octarinecore/client/resource/VectorSet;", "Lmods/octarinecore/client/resource/IConfigChangeListener;", "num", "", "init", "Lkotlin/Function1;", "Lmods/octarinecore/common/Double3;", "(ILkotlin/jvm/functions/Function1;)V", "getInit", "()Lkotlin/jvm/functions/Function1;", "models", "", "getModels", "()[Lmods/octarinecore/common/Double3;", "[Lmods/octarinecore/common/Double3;", "getNum", "()I", "get", "idx", "onConfigChange", "", "BetterFoliage-MC1.12"}
)
public final class VectorSet implements IConfigChangeListener {
   @NotNull
   private final Double3[] models;
   private final int num;
   @NotNull
   private final Function1<Integer, Double3> init;

   @NotNull
   public final Double3[] getModels() {
      return this.models;
   }

   public void onConfigChange() {
      Iterable $this$forEach$iv = (Iterable)RangesKt.until(0, this.num);
      int $i$f$forEach = 0;

      int element$iv;
      for(Iterator var3 = $this$forEach$iv.iterator(); var3.hasNext(); this.models[element$iv] = (Double3)this.init.invoke(Integer.valueOf(element$iv))) {
         element$iv = ((IntIterator)var3).nextInt();
         int $i$a$-forEach-VectorSet$onConfigChange$1 = 0;
      }

   }

   @NotNull
   public final Double3 get(int idx) {
      return this.models[idx % this.num];
   }

   public final int getNum() {
      return this.num;
   }

   @NotNull
   public final Function1<Integer, Double3> getInit() {
      return this.init;
   }

   public VectorSet(int num, @NotNull Function1<? super Integer, Double3> init) {
      Intrinsics.checkParameterIsNotNull(init, "init");
      super();
      this.num = num;
      this.init = init;
      int var3 = this.num;
      Double3[] var4 = new Double3[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         int $i$a$-<init>-VectorSet$models$1 = 0;
         Double3 var11 = (Double3)this.init.invoke(Integer.valueOf(var5));
         var4[var5] = var11;
      }

      this.models = var4;
   }
}
