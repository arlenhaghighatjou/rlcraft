package mods.betterfoliage.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import mods.betterfoliage.client.gui.BiomeListConfigEntry;
import mods.octarinecore.common.config.ConfigPropertyDelegate;
import mods.octarinecore.common.config.ConfigPropertyIntList;
import mods.octarinecore.common.config.DelegatingConfigKt;
import mods.octarinecore.common.config.ObsoleteConfigProperty;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u00008\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0004\u001a\u001a\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u001a\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\tH\u0002\u001a%\u0010\n\u001a\u00020\u0007*\u00020\u00062\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0002\u00a2\u0006\u0002\u0010\u000e\u001a%\u0010\u000f\u001a\u00020\u0007*\u00020\u00062\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0002\u00a2\u0006\u0002\u0010\u0013\u001a%\u0010\u0014\u001a\u00020\u0007*\u00020\u00062\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0002\u00a2\u0006\u0002\u0010\u0013\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"},
   d2 = {"OBSOLETE", "Lmods/octarinecore/common/config/ObsoleteConfigProperty;", "biomeList", "Lmods/octarinecore/common/config/ConfigPropertyIntList;", "defaults", "Lkotlin/Function1;", "Lnet/minecraft/world/biome/Biome;", "", "featureEnable", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "filterClass", "name", "", "", "(Lnet/minecraft/world/biome/Biome;[Ljava/lang/String;)Z", "filterRain", "min", "", "max", "(Lnet/minecraft/world/biome/Biome;Ljava/lang/Float;Ljava/lang/Float;)Z", "filterTemp", "BetterFoliage-MC1.12"}
)
public final class ConfigKt {
   private static final ObsoleteConfigProperty OBSOLETE = new ObsoleteConfigProperty();

   private static final ConfigPropertyDelegate<Boolean> featureEnable() {
      return DelegatingConfigKt.boolean(true).lang("enabled");
   }

   @NotNull
   public static final ConfigPropertyIntList biomeList(@NotNull final Function1<? super Biome, Boolean> defaults) {
      Intrinsics.checkParameterIsNotNull(defaults, "defaults");
      ConfigPropertyIntList var1 = DelegatingConfigKt.intList(new Function0<Integer[]>() {
         @NotNull
         public final Integer[] invoke() {
            RegistryNamespaced var10000 = Biome.REGISTRY;
            if (Biome.REGISTRY == null) {
               throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Iterable<net.minecraft.world.biome.Biome>");
            } else {
               Iterable $this$filter$iv = (Iterable)var10000;
               int $i$f$filter = 0;
               Collection destination$iv$iv = (Collection)(new ArrayList());
               int $i$f$filterTo = 0;

               for(Object element$iv$iv : $this$filter$iv) {
                  Biome it = (Biome)element$iv$iv;
                  int $i$a$-filter-ConfigKt$biomeList$1$1 = 0;
                  if (((Boolean)defaults.invoke(it)).booleanValue()) {
                     destination$iv$iv.add(element$iv$iv);
                  }
               }

               $this$filter$iv = (Iterable)((List)destination$iv$iv);
               $i$f$filter = 0;
               destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
               $i$f$filterTo = 0;

               for(Object item$iv$iv : $this$filter$iv) {
                  Biome var20 = (Biome)item$iv$iv;
                  int $i$a$-map-ConfigKt$biomeList$1$2 = 0;
                  Integer var11 = Biome.REGISTRY.getIDForObject(var20);
                  destination$iv$iv.add(var11);
               }

               Collection $this$toTypedArray$iv = (Collection)((List)destination$iv$iv);
               $i$f$filter = 0;
               Object[] var22 = $this$toTypedArray$iv.toArray(new Integer[0]);
               if (var22 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
               } else {
                  return (Integer[])var22;
               }
            }
         }
      });
      boolean var2 = false;
      boolean var3 = false;
      int $i$a$-apply-ConfigKt$biomeList$2 = 0;
      var1.setGuiClass(BiomeListConfigEntry.class);
      return var1;
   }

   private static final boolean filterTemp(@NotNull Biome $this$filterTemp, Float min, Float max) {
      return (min == null || min.floatValue() <= $this$filterTemp.getDefaultTemperature()) && (max == null || max.floatValue() >= $this$filterTemp.getDefaultTemperature());
   }

   private static final boolean filterRain(@NotNull Biome $this$filterRain, Float min, Float max) {
      return (min == null || min.floatValue() <= $this$filterRain.getRainfall()) && (max == null || max.floatValue() >= $this$filterRain.getRainfall());
   }

   private static final boolean filterClass(@NotNull Biome $this$filterClass, String... name) {
      int $i$f$any = 0;
      String[] var4 = name;
      int var5 = name.length;
      int var6 = 0;

      boolean var13;
      while(true) {
         if (var6 >= var5) {
            var13 = false;
            break;
         }

         Object element$iv = var4[var6];
         int $i$a$-any-ConfigKt$filterClass$1 = 0;
         String var10000 = $this$filterClass.getClass().getName();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "this.javaClass.name");
         String var10 = var10000;
         boolean var11 = false;
         if (var10 == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         var10000 = var10.toLowerCase();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).toLowerCase()");
         if (StringsKt.contains$default((CharSequence)var10000, (CharSequence)element$iv, false, 2, (Object)null)) {
            var13 = true;
            break;
         }

         ++var6;
      }

      return var13;
   }
}
