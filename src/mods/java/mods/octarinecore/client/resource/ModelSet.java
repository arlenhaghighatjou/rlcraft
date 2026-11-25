package mods.octarinecore.client.resource;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import mods.octarinecore.client.render.Model;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\t\u0018\u00002\u00020\u0001B,\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u001d\u0010\u0004\u001a\u0019\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\b\b\u00a2\u0006\u0002\u0010\tJ\u0011\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0003H\u0086\u0002J\b\u0010\u0015\u001a\u00020\u0007H\u0016R(\u0010\u0004\u001a\u0019\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\b\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0019\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\r\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0016"},
   d2 = {"Lmods/octarinecore/client/resource/ModelSet;", "Lmods/octarinecore/client/resource/IConfigChangeListener;", "num", "", "init", "Lkotlin/Function2;", "Lmods/octarinecore/client/render/Model;", "", "Lkotlin/ExtensionFunctionType;", "(ILkotlin/jvm/functions/Function2;)V", "getInit", "()Lkotlin/jvm/functions/Function2;", "models", "", "getModels", "()[Lmods/octarinecore/client/render/Model;", "[Lmods/octarinecore/client/render/Model;", "getNum", "()I", "get", "idx", "onConfigChange", "BetterFoliage-MC1.12"}
)
public final class ModelSet implements IConfigChangeListener {
   @NotNull
   private final Model[] models;
   private final int num;
   @NotNull
   private final Function2<Model, Integer, Unit> init;

   @NotNull
   public final Model[] getModels() {
      return this.models;
   }

   public void onConfigChange() {
      Iterable $this$forEach$iv = (Iterable)RangesKt.until(0, this.num);
      int $i$f$forEach = 0;

      int element$iv;
      Model var7;
      Model[] var9;
      for(Iterator var3 = $this$forEach$iv.iterator(); var3.hasNext(); var9[element$iv] = var7) {
         element$iv = ((IntIterator)var3).nextInt();
         int $i$a$-forEach-ModelSet$onConfigChange$1 = 0;
         Model[] var10000 = this.models;
         var7 = new Model();
         var9 = var10000;
         boolean var10 = false;
         boolean var11 = false;
         int $i$a$-apply-ModelSet$onConfigChange$1$1 = 0;
         this.init.invoke(var7, Integer.valueOf(element$iv));
      }

   }

   @NotNull
   public final Model get(int idx) {
      return this.models[idx % this.num];
   }

   public final int getNum() {
      return this.num;
   }

   @NotNull
   public final Function2<Model, Integer, Unit> getInit() {
      return this.init;
   }

   public ModelSet(int num, @NotNull Function2<? super Model, ? super Integer, Unit> init) {
      Intrinsics.checkParameterIsNotNull(init, "init");
      super();
      this.num = num;
      this.init = init;
      int var3 = this.num;
      Model[] var4 = new Model[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         int $i$a$-<init>-ModelSet$models$1 = 0;
         Model var8 = new Model();
         boolean var9 = false;
         boolean var10 = false;
         int $i$a$-apply-ModelSet$models$1$1 = 0;
         this.init.invoke(var8, Integer.valueOf(var5));
         var4[var5] = var8;
      }

      this.models = var4;
   }
}
