package mods.betterfoliage.client.render.column;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function6;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref.BooleanRef;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.RenderVertex;
import mods.octarinecore.client.render.ShadingContext;
import mods.octarinecore.client.render.Vertex;
import mods.octarinecore.common.Rotation;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing.Axis;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\n¢\u0006\u0002\b\f¨\u0006\u000e"},
   d2 = {"<anonymous>", "", "Lmods/octarinecore/client/render/RenderVertex;", "<anonymous parameter 0>", "Lmods/octarinecore/client/render/ShadingContext;", "<anonymous parameter 1>", "", "<anonymous parameter 2>", "Lmods/octarinecore/client/render/Quad;", "<anonymous parameter 3>", "<anonymous parameter 4>", "Lmods/octarinecore/client/render/Vertex;", "invoke", "mods/betterfoliage/client/render/column/AbstractRenderColumn$render$1$1$1", "mods/betterfoliage/client/render/column/AbstractRenderColumn$$special$$inlined$forEachIndexed$lambda$1"}
)
final class AbstractRenderColumn$render$$inlined$renderAs$lambda$3 extends Lambda implements Function6<RenderVertex, ShadingContext, Integer, Quad, Integer, Vertex, Unit> {
   AbstractRenderColumn$render$$inlined$renderAs$lambda$3(BooleanRef var1, int var2, AbstractRenderColumn var3, Rotation var4, ColumnLayerData var5, BufferBuilder var6) {
      super(6);
      this.$isLidUp = var1;
      this.$idx = var2;
      this.this$0 = var3;
      this.$baseRotation$inlined = var4;
      this.$roundLog$inlined = var5;
      this.$renderer$inlined = var6;
   }

   public final void invoke(@NotNull RenderVertex $receiver, @NotNull ShadingContext $noName_1, int $noName_2, @NotNull Quad $noName_3, int $noName_4, @NotNull Vertex $noName_5) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      Intrinsics.checkParameterIsNotNull($noName_1, "<anonymous parameter 0>");
      Intrinsics.checkParameterIsNotNull($noName_3, "<anonymous parameter 2>");
      Intrinsics.checkParameterIsNotNull($noName_5, "<anonymous parameter 4>");
      if (this.$isLidUp.element) {
         int n$iv = this.$idx + (((ColumnLayerData.SpecialRender)this.$roundLog$inlined).getColumn().getAxis() == Axis.X ? 1 : 0);
         int $i$f$rotateUV = 0;
         switch(n$iv % 4) {
         case 1:
            double t$iv = $receiver.getV();
            $receiver.setV(-$receiver.getU());
            $receiver.setU(t$iv);
            break;
         case 2:
            $receiver.setU(-$receiver.getU());
            $receiver.setV(-$receiver.getV());
            break;
         case 3:
            double t$iv = -$receiver.getV();
            $receiver.setV($receiver.getU());
            $receiver.setU(t$iv);
         }
      }

   }
}
