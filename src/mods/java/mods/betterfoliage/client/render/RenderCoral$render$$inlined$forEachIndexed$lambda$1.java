package mods.betterfoliage.client.render;

import kotlin.Metadata;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref.IntRef;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.ShadingContext;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\n\u00a2\u0006\u0002\b\b\u00a8\u0006\t"},
   d2 = {"<anonymous>", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "<anonymous parameter 0>", "Lmods/octarinecore/client/render/ShadingContext;", "qi", "", "<anonymous parameter 2>", "Lmods/octarinecore/client/render/Quad;", "invoke", "mods/betterfoliage/client/render/RenderCoral$render$1$1"}
)
final class RenderCoral$render$$inlined$forEachIndexed$lambda$1 extends Lambda implements Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> {
   RenderCoral$render$$inlined$forEachIndexed$lambda$1(IntRef var1, RenderCoral var2, BlockContext var3, WorldRenderer var4) {
      super(3);
      this.$variation = var1;
      this.this$0 = var2;
      this.$ctx$inlined = var3;
      this.$renderer$inlined = var4;
   }

   @Nullable
   public final EaglerTextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int qi, @NotNull Quad $noName_2) {
      Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
      Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
      EaglerTextureAtlasSprite var10000;
      if (qi == 4) {
         var10000 = this.this$0.getCrustIcons().get(this.$variation.element);
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }
      } else {
         var10000 = this.this$0.getCoralIcons().get(this.$variation.element + (qi & 1));
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }
      }

      return var10000;
   }
}
