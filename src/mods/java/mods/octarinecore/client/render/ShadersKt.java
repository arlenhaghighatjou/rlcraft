package mods.octarinecore.client.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000B\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a9\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\r2\u0018\u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\u0011H\u0086\b\u001a*\u0010\u0012\u001a\u001e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00130\u0001j\u0002`\u00142\u0006\u0010\u0015\u001a\u00020\u0016\u001a8\u0010\u0017\u001a\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u00012\u0018\u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\r0\u0011\u001a:\u0010\u0018\u001a\u001e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00130\u0001j\u0002`\u00142\u0006\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\n\")\u0010\u0000\u001a\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0005\")\u0010\u0006\u001a\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00070\u0001¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0005\"\u000e\u0010\t\u001a\u00020\nX\u0086T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\nX\u0086T¢\u0006\u0002\n\u0000¨\u0006\u001c"},
   d2 = {"cornerAoMaxGreen", "Lkotlin/Function3;", "Lnet/minecraft/util/EnumFacing;", "Lmods/octarinecore/client/render/CornerTri;", "getCornerAoMaxGreen", "()Lkotlin/jvm/functions/Function3;", "cornerFlat", "Lmods/octarinecore/client/render/FaceFlat;", "getCornerFlat", "defaultCornerDimming", "", "defaultEdgeDimming", "accumulate", "Lmods/octarinecore/client/render/AoData;", "v1", "v2", "func", "Lkotlin/Function2;", "cornerAo", "Lmods/octarinecore/client/render/Shader;", "Lmods/octarinecore/client/render/CornerShaderFactory;", "fallbackAxis", "Lnet/minecraft/util/EnumFacing$Axis;", "cornerAoTri", "cornerInterpolate", "edgeAxis", "weight", "dimming", "BetterFoliage-MC1.12"}
)
public final class ShadersKt {
   public static final float defaultCornerDimming = 0.5F;
   public static final float defaultEdgeDimming = 0.8F;
   @NotNull
   private static final Function3<EnumFacing, EnumFacing, EnumFacing, FaceFlat> cornerFlat = (Function3)null.INSTANCE;
   @NotNull
   private static final Function3<EnumFacing, EnumFacing, EnumFacing, CornerTri> cornerAoMaxGreen = cornerAoTri(null.INSTANCE);

   @NotNull
   public static final Function3<EnumFacing, EnumFacing, EnumFacing, Shader> cornerAo(@NotNull final Axis fallbackAxis) {
      Intrinsics.checkParameterIsNotNull(fallbackAxis, "fallbackAxis");
      return (Function3)(new Function3<EnumFacing, EnumFacing, EnumFacing, CornerSingleFallback>() {
         @NotNull
         public final CornerSingleFallback invoke(@NotNull EnumFacing face, @NotNull EnumFacing dir1, @NotNull EnumFacing dir2) {
            Intrinsics.checkParameterIsNotNull(face, "face");
            Intrinsics.checkParameterIsNotNull(dir1, "dir1");
            Intrinsics.checkParameterIsNotNull(dir2, "dir2");
            Iterable var5 = (Iterable)CollectionsKt.listOf(new EnumFacing[]{face, dir1, dir2});
            boolean var6 = false;
            boolean var8 = false;
            Iterator var9 = var5.iterator();

            Object var10000;
            while(true) {
               if (var9.hasNext()) {
                  Object var10 = var9.next();
                  EnumFacing it = (EnumFacing)var10;
                  int $i$a$-find-ShadersKt$cornerAo$1$fallbackDir$1 = 0;
                  if (it.getAxis() != fallbackAxis) {
                     continue;
                  }

                  var10000 = var10;
                  break;
               }

               var10000 = null;
               break;
            }

            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            EnumFacing fallbackDir = (EnumFacing)var10000;
            return new CornerSingleFallback(face, dir1, dir2, fallbackDir, 0.0F, 16, (DefaultConstructorMarker)null);
         }
      });
   }

   @NotNull
   public static final Function3<EnumFacing, EnumFacing, EnumFacing, FaceFlat> getCornerFlat() {
      return cornerFlat;
   }

   @NotNull
   public static final Function3<EnumFacing, EnumFacing, EnumFacing, CornerTri> cornerAoTri(@NotNull final Function2<? super AoData, ? super AoData, AoData> func) {
      Intrinsics.checkParameterIsNotNull(func, "func");
      return (Function3)(new Function3<EnumFacing, EnumFacing, EnumFacing, CornerTri>() {
         @NotNull
         public final CornerTri invoke(@NotNull EnumFacing face, @NotNull EnumFacing dir1, @NotNull EnumFacing dir2) {
            Intrinsics.checkParameterIsNotNull(face, "face");
            Intrinsics.checkParameterIsNotNull(dir1, "dir1");
            Intrinsics.checkParameterIsNotNull(dir2, "dir2");
            return new CornerTri(face, dir1, dir2, func);
         }
      });
   }

   @NotNull
   public static final Function3<EnumFacing, EnumFacing, EnumFacing, CornerTri> getCornerAoMaxGreen() {
      return cornerAoMaxGreen;
   }

   @NotNull
   public static final Function3<EnumFacing, EnumFacing, EnumFacing, Shader> cornerInterpolate(@NotNull final Axis edgeAxis, final float weight, final float dimming) {
      Intrinsics.checkParameterIsNotNull(edgeAxis, "edgeAxis");
      return (Function3)(new Function3<EnumFacing, EnumFacing, EnumFacing, CornerInterpolateDimming>() {
         @NotNull
         public final CornerInterpolateDimming invoke(@NotNull EnumFacing dir1, @NotNull EnumFacing dir2, @NotNull EnumFacing dir3) {
            Intrinsics.checkParameterIsNotNull(dir1, "dir1");
            Intrinsics.checkParameterIsNotNull(dir2, "dir2");
            Intrinsics.checkParameterIsNotNull(dir3, "dir3");
            Iterable faceDirs = (Iterable)CollectionsKt.listOf(new EnumFacing[]{dir1, dir2, dir3});
            boolean $this$filter$iv = false;
            boolean $this$filterTo$iv$iv = false;
            Iterator destination$iv$iv = faceDirs.iterator();

            Object var10000;
            while(true) {
               if (destination$iv$iv.hasNext()) {
                  Object $i$f$filterTo = destination$iv$iv.next();
                  EnumFacing it = (EnumFacing)$i$f$filterTo;
                  int $i$a$-find-ShadersKt$cornerInterpolate$1$edgeDir$1 = 0;
                  if (it.getAxis() != edgeAxis) {
                     continue;
                  }

                  var10000 = $i$f$filterTo;
                  break;
               }

               var10000 = null;
               break;
            }

            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            EnumFacing edgeDir = (EnumFacing)var10000;
            Iterable $this$filter$iv = (Iterable)CollectionsKt.listOf(new EnumFacing[]{dir1, dir2, dir3});
            int $i$f$filter = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList());
            int $i$f$filterTo = 0;

            for(Object element$iv$iv : $this$filter$iv) {
               EnumFacing it = (EnumFacing)element$iv$iv;
               int $i$a$-filter-ShadersKt$cornerInterpolate$1$faceDirs$1 = 0;
               if (it.getAxis() != edgeAxis) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            List faceDirs = (List)destination$iv$iv;
            return new CornerInterpolateDimming((EnumFacing)faceDirs.get(0), (EnumFacing)faceDirs.get(1), edgeDir, weight, dimming, 0.0F, 32, (DefaultConstructorMarker)null);
         }
      });
   }

   @Nullable
   public static final AoData accumulate(@Nullable AoData v1, @Nullable AoData v2, @NotNull Function2<? super AoData, ? super AoData, AoData> func) {
      int $i$f$accumulate = 0;
      Intrinsics.checkParameterIsNotNull(func, "func");
      boolean v1ok = v1 != null && v1.getValid();
      boolean v2ok = v2 != null && v2.getValid();
      if (v1ok && v2ok) {
         if (v1 == null) {
            Intrinsics.throwNpe();
         }

         if (v2 == null) {
            Intrinsics.throwNpe();
         }

         return (AoData)func.invoke(v1, v2);
      } else if (v1ok) {
         return v1;
      } else {
         return v2ok ? v2 : null;
      }
   }
}
