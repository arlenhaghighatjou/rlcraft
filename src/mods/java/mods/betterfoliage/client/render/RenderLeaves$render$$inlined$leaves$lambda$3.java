package mods.betterfoliage.client.render;

import kotlin.Metadata;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import mods.betterfoliage.client.texture.LeafInfo;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.ShadingContext;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\n¢\u0006\u0002\b\b¨\u0006\t"},
   d2 = {"<anonymous>", "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", "<anonymous parameter 0>", "Lmods/octarinecore/client/render/ShadingContext;", "<anonymous parameter 1>", "", "<anonymous parameter 2>", "Lmods/octarinecore/client/render/Quad;", "invoke", "mods/betterfoliage/client/render/RenderLeaves$render$1$2"}
)
final class RenderLeaves$render$$inlined$leaves$lambda$3 extends Lambda implements Function3<ShadingContext, Integer, Quad, TextureAtlasSprite> {
   RenderLeaves$render$$inlined$leaves$lambda$3(Integer[] var1, RenderLeaves var2, BlockContext var3, BufferBuilder var4, LeafInfo var5, int var6, boolean var7) {
      super(3);
      this.$rand = var1;
      this.this$0 = var2;
      this.$ctx$inlined = var3;
      this.$renderer$inlined = var4;
      this.$leafInfo$inlined = var5;
      this.$blockColor$inlined = var6;
      this.$isSnowed$inlined = var7;
   }

   @NotNull
   public final TextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int $noName_1, @NotNull Quad $noName_2) {
      Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
      Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
      TextureAtlasSprite var10000 = this.this$0.getSnowedIcon().get(this.$rand[1].intValue());
      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      return var10000;
   }
}
