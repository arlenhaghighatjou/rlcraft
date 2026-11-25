package mods.betterfoliage.client.render;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function6;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import mods.betterfoliage.client.texture.LeafInfo;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.RenderVertex;
import mods.octarinecore.client.render.ShadingContext;
import mods.octarinecore.client.render.Vertex;
import net.minecraft.client.renderer.BufferBuilder;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\n¢\u0006\u0002\b\f¨\u0006\u000e"},
   d2 = {"<anonymous>", "", "Lmods/octarinecore/client/render/RenderVertex;", "<anonymous parameter 0>", "Lmods/octarinecore/client/render/ShadingContext;", "<anonymous parameter 1>", "", "<anonymous parameter 2>", "Lmods/octarinecore/client/render/Quad;", "<anonymous parameter 3>", "<anonymous parameter 4>", "Lmods/octarinecore/client/render/Vertex;", "invoke", "mods/betterfoliage/client/render/RenderLeaves$render$1$1$2", "mods/betterfoliage/client/render/RenderLeaves$$special$$inlined$forEach$lambda$2"}
)
final class RenderLeaves$render$$inlined$leaves$lambda$5 extends Lambda implements Function6<RenderVertex, ShadingContext, Integer, Quad, Integer, Vertex, Unit> {
   RenderLeaves$render$$inlined$leaves$lambda$5(Integer[] var1, RenderLeaves var2, BlockContext var3, BufferBuilder var4, LeafInfo var5, int var6, boolean var7) {
      super(6);
      this.$rand$inlined = var1;
      this.this$0 = var2;
      this.$ctx$inlined = var3;
      this.$renderer$inlined = var4;
      this.$leafInfo$inlined = var5;
      this.$blockColor$inlined = var6;
      this.$isSnowed$inlined = var7;
   }

   public final void invoke(@NotNull RenderVertex $receiver, @NotNull ShadingContext $noName_1, int $noName_2, @NotNull Quad $noName_3, int $noName_4, @NotNull Vertex $noName_5) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      Intrinsics.checkParameterIsNotNull($noName_1, "<anonymous parameter 0>");
      Intrinsics.checkParameterIsNotNull($noName_3, "<anonymous parameter 2>");
      Intrinsics.checkParameterIsNotNull($noName_5, "<anonymous parameter 4>");
      int n$iv = this.$rand$inlined[1].intValue();
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

      n$iv = this.$blockColor$inlined;
      $i$f$rotateUV = 0;
      $receiver.setRed($receiver.getRed() * ((float)(n$iv >> 16 & 255) / 256.0F));
      $receiver.setGreen($receiver.getGreen() * ((float)(n$iv >> 8 & 255) / 256.0F));
      $receiver.setBlue($receiver.getBlue() * ((float)(n$iv & 255) / 256.0F));
   }
}
