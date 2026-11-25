package mods.octarinecore.client.render;

import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.common.GeometryKt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0011\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"},
   d2 = {"fullCube", "Lmods/octarinecore/client/render/Model;", "getFullCube", "()Lmods/octarinecore/client/render/Model;", "BetterFoliage-MC1.12"}
)
public final class ModelKt {
   @NotNull
   private static final Model fullCube;

   @NotNull
   public static final Model getFullCube() {
      return fullCube;
   }

   static {
      Model var0 = new Model();
      boolean var1 = false;
      boolean var2 = false;
      Model $this$apply = var0;
      int $i$a$-apply-ModelKt$fullCube$1 = 0;
      Object[] $this$forEach$iv = GeometryKt.getForgeDirs();
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         int $i$a$-forEach-ModelKt$fullCube$1$1 = 0;
         Quad var10001 = $this$apply.faceQuad((EnumFacing)element$iv);
         Axis var10003 = ((EnumFacing)element$iv).getAxis();
         Intrinsics.checkExpressionValueIsNotNull(var10003, "it.axis");
         $this$apply.add(Quad.setFlatShader$default(Quad.setAoShader$default(var10001, ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(var10003), (Function2)null, 1, (Object)null), (Function2)null, 2, (Object)null), ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.getCornerFlat(), (Function2)null, 1, (Object)null), (Function2)null, 2, (Object)null));
      }

      fullCube = var0;
   }
}
