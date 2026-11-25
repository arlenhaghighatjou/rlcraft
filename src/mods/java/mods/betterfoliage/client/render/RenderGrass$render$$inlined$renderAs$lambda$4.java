package mods.betterfoliage.client.render;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import mods.betterfoliage.client.texture.GrassInfo;
import mods.octarinecore.client.render.Quad;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\n\u00a2\u0006\u0002\b\u0006\u00a8\u0006\u0007"},
   d2 = {"<anonymous>", "", "qi", "", "<anonymous parameter 1>", "Lmods/octarinecore/client/render/Quad;", "invoke", "mods/betterfoliage/client/render/RenderGrass$render$1$1"}
)
final class RenderGrass$render$$inlined$renderAs$lambda$4 extends Lambda implements Function2<Integer, Quad, Boolean> {
   RenderGrass$render$$inlined$renderAs$lambda$4(WorldRenderer var1, List var2, GrassInfo var3, boolean var4, int var5) {
      super(2);
      this.$renderer$inlined = var1;
      this.$isHidden$inlined = var2;
      this.$grass$inlined = var3;
      this.$isSnowed$inlined = var4;
      this.$blockColor$inlined = var5;
   }

   public final boolean invoke(int qi, @NotNull Quad $noName_1) {
      Intrinsics.checkParameterIsNotNull($noName_1, "<anonymous parameter 1>");
      return !((Boolean)this.$isHidden$inlined.get(qi)).booleanValue();
   }
}
