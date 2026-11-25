package mods.octarinecore.metaprog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000<\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u001a'\u0010\u0007\u001a\u00020\b2\u001a\u0010\t\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u000b0\n\"\u0006\u0012\u0002\b\u00030\u000b\u00a2\u0006\u0002\u0010\f\u001a\u0014\u0010\r\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0003\u001a\n\u0010\u0010\u001a\u00020\u0011*\u00020\u0012\u001a$\u0010\u0013\u001a\u0004\u0018\u0001H\u0014\"\u0006\b\u0000\u0010\u0014\u0018\u0001*\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u0003H\u0086\b\u00a2\u0006\u0002\u0010\u0016\u001aM\u0010\u0017\u001a$\u0012 \u0012\u001e\u0012\f\u0012\n \u0018*\u0004\u0018\u00010\u00030\u0003\u0012\f\u0012\n \u0018*\u0004\u0018\u00010\u00040\u00040\u00020\u0001*\u00020\u00042\u001a\u0010\u0019\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u000e0\n\"\u0006\u0012\u0002\b\u00030\u000e\u00a2\u0006\u0002\u0010\u001a\u001a(\u0010\u001b\u001a\u0004\u0018\u0001H\u0014\"\u0006\b\u0000\u0010\u0014\u0018\u0001*\u0006\u0012\u0002\b\u00030\u000e2\u0006\u0010\u0015\u001a\u00020\u0003H\u0086\b\u00a2\u0006\u0002\u0010\u001c\"'\u0010\u0000\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00040\u00020\u0001*\u00020\u00048F\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u001d"},
   d2 = {"reflectNestedObjects", "", "Lkotlin/Pair;", "", "", "getReflectNestedObjects", "(Ljava/lang/Object;)Ljava/util/List;", "allAvailable", "", "codeElement", "", "Lmods/octarinecore/metaprog/Resolvable;", "([Lmods/octarinecore/metaprog/Resolvable;)Z", "getJavaClass", "Ljava/lang/Class;", "name", "array", "Lmods/octarinecore/metaprog/ClassRefArray;", "Lmods/octarinecore/metaprog/ClassRef;", "reflectField", "T", "field", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", "reflectFieldsOfType", "kotlin.jvm.PlatformType", "types", "(Ljava/lang/Object;[Ljava/lang/Class;)Ljava/util/List;", "reflectStaticField", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", "BetterFoliage-MC1.12"}
)
@JvmName(
   name = "Reflection"
)
public final class Reflection {
   @Nullable
   public static final Class<?> getJavaClass(@NotNull final String name) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      return (Class)Utils.tryDefault((Object)null, new Function0<Class<?>>() {
         public final Class<?> invoke() {
            return Class.forName(name);
         }
      });
   }

   @NotNull
   public static final List<Pair<String, Object>> getReflectNestedObjects(@NotNull Object $this$reflectNestedObjects) {
      Intrinsics.checkParameterIsNotNull($this$reflectNestedObjects, "$this$reflectNestedObjects");
      Class[] var10000 = $this$reflectNestedObjects.getClass().getDeclaredClasses();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "this.javaClass.declaredClasses");
      Object[] $this$map$iv = var10000;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.length));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         int $i$a$-map-Reflection$reflectNestedObjects$1 = 0;
         Pair var13 = (Pair)Utils.tryDefault((Object)null, new Reflection$reflectNestedObjects$1$1((Class)item$iv$iv));
         destination$iv$iv.add(var13);
      }

      return CollectionsKt.filterNotNull((Iterable)((List)destination$iv$iv));
   }

   @NotNull
   public static final List<Pair<String, Object>> reflectFieldsOfType(@NotNull Object $this$reflectFieldsOfType, @NotNull Class<?>... types) {
      Intrinsics.checkParameterIsNotNull($this$reflectFieldsOfType, "$this$reflectFieldsOfType");
      Intrinsics.checkParameterIsNotNull(types, "types");
      Field[] var10000 = $this$reflectFieldsOfType.getClass().getDeclaredFields();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "this.javaClass.declaredFields");
      Object[] $this$filter$iv = var10000;
      int $i$f$filter = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$filterTo = 0;

      for(Object element$iv$iv : $this$filter$iv) {
         Field field = (Field)element$iv$iv;
         int $i$a$-filter-Reflection$reflectFieldsOfType$1 = 0;
         int $i$f$any = 0;
         Class[] it = types;
         int var16 = types.length;
         int var17 = 0;

         while(true) {
            if (var17 >= var16) {
               var35 = false;
               break;
            }

            Object element$iv = it[var17];
            int $i$a$-any-Reflection$reflectFieldsOfType$1$1 = 0;
            Intrinsics.checkExpressionValueIsNotNull(field, "field");
            if (((Class)element$iv).isAssignableFrom(field.getType())) {
               var35 = true;
               break;
            }

            ++var17;
         }

         if (var35) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      $this$filter$iv = (Iterable)((List)destination$iv$iv);
      $i$f$filter = 0;
      destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
      $i$f$filterTo = 0;

      for(Object item$iv$iv : $this$filter$iv) {
         Field var29 = (Field)item$iv$iv;
         int $i$a$-map-Reflection$reflectFieldsOfType$2 = 0;
         Intrinsics.checkExpressionValueIsNotNull(var29, "field");
         String var31 = var29.getName();
         boolean $this$any$iv = false;
         boolean var32 = false;
         int $i$a$-let-Reflection$reflectFieldsOfType$2$1 = 0;
         var29.setAccessible(true);
         Object var34 = var29.get($this$reflectFieldsOfType);
         Pair var22 = TuplesKt.to(var31, var34);
         destination$iv$iv.add(var22);
      }

      return CollectionsKt.filterNotNull((Iterable)((List)destination$iv$iv));
   }

   public static final boolean allAvailable(@NotNull Resolvable<?>... codeElement) {
      Intrinsics.checkParameterIsNotNull(codeElement, "codeElement");
      int $i$f$all = 0;
      Resolvable[] var3 = codeElement;
      int var4 = codeElement.length;
      int var5 = 0;

      boolean var10000;
      while(true) {
         if (var5 >= var4) {
            var10000 = true;
            break;
         }

         Object element$iv = var3[var5];
         int $i$a$-all-Reflection$allAvailable$1 = 0;
         if (((Resolvable)element$iv).getElement() == null) {
            var10000 = false;
            break;
         }

         ++var5;
      }

      return var10000;
   }

   @NotNull
   public static final ClassRefArray array(@NotNull ClassRef $this$array) {
      Intrinsics.checkParameterIsNotNull($this$array, "$this$array");
      return new ClassRefArray($this$array.getName());
   }
}
