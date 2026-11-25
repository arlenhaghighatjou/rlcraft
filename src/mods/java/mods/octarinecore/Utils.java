package mods.octarinecore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import mods.betterfoliage.loader.Refs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000|\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u001c\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001aK\u0010\u0002\u001a\u00020\u0003\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00040\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00050\u00072\u0018\u0010\t\u001a\u0014\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u0005\u0012\u0004\u0012\u00020\u00030\nH\u0086\b\u001a\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f\u001a\u0016\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0001\u001a&\u0010\u0011\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\f\u001a'\u0010\u0015\u001a\u0002H\u0016\"\u0004\b\u0000\u0010\u00162\u0006\u0010\u0017\u001a\u0002H\u00162\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00160\u0019¢\u0006\u0002\u0010\u001a\u001a<\u0010\u001b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002H\u001f0\u001d0\u001c\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010\u001f*\b\u0012\u0004\u0012\u0002H\u001e0\u00072\f\u0010 \u001a\b\u0012\u0004\u0012\u0002H\u001f0\u0007\u001a)\u0010!\u001a\u00020\u0003\"\u0004\b\u0000\u0010\u0016*\b\u0012\u0004\u0012\u0002H\u00160\"2\u0006\u0010#\u001a\u00020\f2\u0006\u0010$\u001a\u00020\fH\u0086\b\u001a3\u0010%\u001a\u000e\u0012\u0004\u0012\u0002H'\u0012\u0004\u0012\u0002H(0&\"\u0004\b\u0000\u0010'\"\u0004\b\u0001\u0010(*\u0010\u0012\u0004\u0012\u0002H'\u0012\u0006\u0012\u0004\u0018\u0001H(0&H\u0086\b\u001a>\u0010)\u001a\u0004\u0018\u0001H*\"\u0006\b\u0000\u0010\u0016\u0018\u0001\"\u0004\b\u0001\u0010**\b\u0012\u0004\u0012\u0002H\u00160\u00072\u0014\u0010\t\u001a\u0010\u0012\u0004\u0012\u0002H\u0016\u0012\u0006\u0012\u0004\u0018\u0001H*0+H\u0086\b¢\u0006\u0002\u0010,\u001a;\u0010-\u001a\u00020\u0003\"\u0006\b\u0000\u0010\u0016\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00160\u00072\u001e\u0010\t\u001a\u001a\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u0002H\u0016\u0012\u0004\u0012\u0002H\u0016\u0012\u0004\u0012\u00020\u00030.H\u0086\b\u001a\u0014\u0010/\u001a\u0004\u0018\u000100*\u0002012\u0006\u00102\u001a\u000203\u001a\u0012\u00104\u001a\u000205*\u0002012\u0006\u00102\u001a\u000203\u001a?\u00106\u001a\b\u0012\u0004\u0012\u0002H*0\u001c\"\u0004\b\u0000\u00107\"\u0004\b\u0001\u0010*\"\u0004\b\u0002\u0010\u0016*\b\u0012\u0004\u0012\u0002H\u00160\u00072\u0012\u00108\u001a\u000e\u0012\u0004\u0012\u0002H7\u0012\u0004\u0012\u0002H*0+H\u0086\b\u001a\u001a\u00109\u001a\u00020\u0001*\u00020\u00012\u0006\u0010:\u001a\u00020\u00012\u0006\u0010;\u001a\u00020\u0001\u001a\u001a\u00109\u001a\u00020\f*\u00020\f2\u0006\u0010:\u001a\u00020\f2\u0006\u0010;\u001a\u00020\f\u001a/\u0010<\u001a\u00020\u0003\"\u0006\b\u0000\u0010\u0016\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00160\"2\u0012\u00108\u001a\u000e\u0012\u0004\u0012\u0002H\u0016\u0012\u0004\u0012\u0002H\u00160+H\u0086\b\u001a\u0015\u0010=\u001a\u00020>*\u00020>2\u0006\u0010?\u001a\u00020>H\u0086\b\u001a\u0015\u0010=\u001a\u00020@*\u00020@2\u0006\u0010?\u001a\u00020>H\u0086\b\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n\u0000¨\u0006A"},
   d2 = {"PI2", "", "forEachNested", "", "T1", "T2", "list1", "", "list2", "func", "Lkotlin/Function2;", "nextPowerOf2", "", "x", "random", "min", "max", "semiRandom", "y", "z", "seed", "tryDefault", "T", "default", "work", "Lkotlin/Function0;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "cross", "", "Lkotlin/Pair;", "A", "B", "other", "exchange", "", "idx1", "idx2", "filterValuesNotNull", "", "K", "V", "findFirst", "R", "Lkotlin/Function1;", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "forEachPairIndexed", "Lkotlin/Function3;", "getTileEntitySafe", "Lnet/minecraft/tileentity/TileEntity;", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "isBlockLoaded", "", "mapAs", "C", "transform", "minmax", "minVal", "maxVal", "replace", "stripStart", "", "str", "Lnet/minecraft/util/ResourceLocation;", "BetterFoliage-MC1.12"}
)
@JvmName(
   name = "Utils"
)
public final class Utils {
   public static final double PI2 = 6.283185307179586D;

   @NotNull
   public static final String stripStart(@NotNull String $this$stripStart, @NotNull String str) {
      int $i$f$stripStart = 0;
      Intrinsics.checkParameterIsNotNull($this$stripStart, "$this$stripStart");
      Intrinsics.checkParameterIsNotNull(str, "str");
      String var10000;
      if (StringsKt.startsWith$default($this$stripStart, str, false, 2, (Object)null)) {
         int var4 = str.length();
         boolean var5 = false;
         var10000 = $this$stripStart.substring(var4);
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).substring(startIndex)");
      } else {
         var10000 = $this$stripStart;
      }

      return var10000;
   }

   @NotNull
   public static final ResourceLocation stripStart(@NotNull ResourceLocation $this$stripStart, @NotNull String str) {
      int $i$f$stripStart = 0;
      Intrinsics.checkParameterIsNotNull($this$stripStart, "$this$stripStart");
      Intrinsics.checkParameterIsNotNull(str, "str");
      String var10000 = $this$stripStart.getResourceDomain();
      String var10001 = $this$stripStart.getResourcePath();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "path");
      String $this$stripStart$iv = var10001;
      String var10 = var10000;
      int $i$f$stripStart = 0;
      if (StringsKt.startsWith$default($this$stripStart$iv, str, false, 2, (Object)null)) {
         int var6 = str.length();
         boolean var7 = false;
         if ($this$stripStart$iv == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         var10000 = $this$stripStart$iv.substring(var6);
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).substring(startIndex)");
      } else {
         var10000 = $this$stripStart$iv;
      }

      String var11 = var10000;
      return new ResourceLocation(var10, var11);
   }

   public static final <T> void exchange(@NotNull List<T> $this$exchange, int idx1, int idx2) {
      int $i$f$exchange = 0;
      Intrinsics.checkParameterIsNotNull($this$exchange, "$this$exchange");
      Object e = $this$exchange.get(idx1);
      $this$exchange.set(idx1, $this$exchange.get(idx2));
      $this$exchange.set(idx2, e);
   }

   @NotNull
   public static final <A, B> List<Pair<A, B>> cross(@NotNull Iterable<? extends A> $this$cross, @NotNull Iterable<? extends B> other) {
      Intrinsics.checkParameterIsNotNull($this$cross, "$this$cross");
      Intrinsics.checkParameterIsNotNull(other, "other");
      int $i$f$flatMap = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$flatMapTo = 0;

      for(Object element$iv$iv : $this$cross) {
         Object a = element$iv$iv;
         int $i$a$-flatMap-Utils$cross$1 = 0;
         int $i$f$map = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(other, 10)));
         int $i$f$mapTo = 0;

         for(Object item$iv$iv : other) {
            int $i$a$-map-Utils$cross$1$1 = 0;
            Pair var21 = TuplesKt.to(a, item$iv$iv);
            destination$iv$iv.add(var21);
         }

         a = (Iterable)((List)destination$iv$iv);
         CollectionsKt.addAll(destination$iv$iv, (Iterable)a);
      }

      return (List)destination$iv$iv;
   }

   @NotNull
   public static final <C, R, T> List<R> mapAs(@NotNull Iterable<? extends T> $this$mapAs, @NotNull Function1<? super C, ? extends R> transform) {
      int $i$f$mapAs = 0;
      Intrinsics.checkParameterIsNotNull($this$mapAs, "$this$mapAs");
      Intrinsics.checkParameterIsNotNull(transform, "transform");
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$mapAs, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$mapAs) {
         int $i$a$-map-Utils$mapAs$1 = 0;
         Object var13 = transform.invoke(item$iv$iv);
         destination$iv$iv.add(var13);
      }

      return (List)destination$iv$iv;
   }

   public static final <T1, T2> void forEachNested(@NotNull Iterable<? extends T1> list1, @NotNull Iterable<? extends T2> list2, @NotNull Function2<? super T1, ? super T2, Unit> func) {
      int $i$f$forEachNested = 0;
      Intrinsics.checkParameterIsNotNull(list1, "list1");
      Intrinsics.checkParameterIsNotNull(list2, "list2");
      Intrinsics.checkParameterIsNotNull(func, "func");
      int $i$f$forEach = 0;

      for(Object element$iv : list1) {
         Object e1 = element$iv;
         int $i$a$-forEach-Utils$forEachNested$1 = 0;
         int $i$f$forEach = 0;

         for(Object element$iv : list2) {
            int $i$a$-forEach-Utils$forEachNested$1$1 = 0;
            func.invoke(e1, element$iv);
         }
      }

   }

   @NotNull
   public static final <K, V> Map<K, V> filterValuesNotNull(@NotNull Map<K, ? extends V> $this$filterValuesNotNull) {
      int $i$f$filterValuesNotNull = 0;
      Intrinsics.checkParameterIsNotNull($this$filterValuesNotNull, "$this$filterValuesNotNull");
      int $i$f$filterValues = 0;
      LinkedHashMap result$iv = new LinkedHashMap();
      boolean var6 = false;

      for(Entry entry$iv : $this$filterValuesNotNull.entrySet()) {
         Object it = entry$iv.getValue();
         int $i$a$-filterValues-Utils$filterValuesNotNull$1 = 0;
         if (it != null) {
            result$iv.put(entry$iv.getKey(), entry$iv.getValue());
         }
      }

      return (Map)result$iv;
   }

   public static final <T> T tryDefault(T var0, @NotNull Function0<? extends T> work) {
      Intrinsics.checkParameterIsNotNull(work, "work");

      Object var2;
      try {
         var2 = work.invoke();
      } catch (Throwable var4) {
         var2 = default;
      }

      return (T)var2;
   }

   public static final double random(double min, double max) {
      double var4 = Math.random();
      boolean var6 = false;
      boolean var7 = false;
      int $i$a$-let-Utils$random$1 = 0;
      return min + (max - min) * var4;
   }

   public static final int semiRandom(int x, int y, int z, int seed) {
      int value = x * x + y * y + z * z + x * y + y * z + z * x + seed * seed & 63;
      value = 3 * x * value + 5 * y * value + 7 * z * value + 11 * seed & 63;
      return value;
   }

   public static final double minmax(double $this$minmax, double minVal, double maxVal) {
      return Math.min(Math.max($this$minmax, minVal), maxVal);
   }

   public static final int minmax(int $this$minmax, int minVal, int maxVal) {
      return Math.min(Math.max($this$minmax, minVal), maxVal);
   }

   public static final int nextPowerOf2(int x) {
      return 1 << (x == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(x - 1));
   }

   public static final boolean isBlockLoaded(@NotNull IBlockAccess $this$isBlockLoaded, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull($this$isBlockLoaded, "$this$isBlockLoaded");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      boolean var10000;
      if ($this$isBlockLoaded instanceof World) {
         var10000 = ((World)$this$isBlockLoaded).isBlockLoaded(pos, false);
      } else if ($this$isBlockLoaded instanceof ChunkCache) {
         var10000 = ((ChunkCache)$this$isBlockLoaded).world.isBlockLoaded(pos, false);
      } else if (Refs.INSTANCE.getOptifineChunkCache().isInstance($this$isBlockLoaded)) {
         Object var2 = Refs.INSTANCE.getCCOFChunkCache().get($this$isBlockLoaded);
         if (var2 == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.world.ChunkCache");
         }

         var10000 = ((ChunkCache)var2).world.isBlockLoaded(pos, false);
      } else {
         var10000 = false;
      }

      return var10000;
   }

   @Nullable
   public static final TileEntity getTileEntitySafe(@NotNull final IBlockAccess $this$getTileEntitySafe, @NotNull final BlockPos pos) {
      Intrinsics.checkParameterIsNotNull($this$getTileEntitySafe, "$this$getTileEntitySafe");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      return (TileEntity)tryDefault((TileEntity)null, new Function0<TileEntity>() {
         @Nullable
         public final TileEntity invoke() {
            return Utils.isBlockLoaded($this$getTileEntitySafe, pos) ? $this$getTileEntitySafe.getTileEntity(pos) : null;
         }
      });
   }
}
