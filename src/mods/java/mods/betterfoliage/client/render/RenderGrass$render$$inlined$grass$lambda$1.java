package mods.betterfoliage.client.render;

import kotlin.Metadata;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.texture.GrassInfo;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.ShadingContext;
import mods.octarinecore.client.resource.IconHolder;
import mods.octarinecore.client.resource.IconSet;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\n¢\u0006\u0002\b\b¨\u0006\t"},
   d2 = {"<anonymous>", "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", "<anonymous parameter 0>", "Lmods/octarinecore/client/render/ShadingContext;", "qi", "", "<anonymous parameter 2>", "Lmods/octarinecore/client/render/Quad;", "invoke", "mods/betterfoliage/client/render/RenderGrass$render$2$1"}
)
final class RenderGrass$render$$inlined$grass$lambda$1 extends Lambda implements Function3<ShadingContext, Integer, Quad, TextureAtlasSprite> {
   RenderGrass$render$$inlined$grass$lambda$1(RenderGrass var1, BufferBuilder var2, Integer[] var3, BlockContext var4, boolean var5, IconHolder var6, IconSet var7, GrassInfo var8, int var9) {
      super(3);
      this.this$0 = var1;
      this.$renderer$inlined = var2;
      this.$rand$inlined = var3;
      this.$ctx$inlined = var4;
      this.$isSnowed$inlined = var5;
      this.$iconGen$inlined = var6;
      this.$iconset$inlined = var7;
      this.$grass$inlined = var8;
      this.$blockColor$inlined = var9;
   }

   @Nullable
   public final TextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int qi, @NotNull Quad $noName_2) {
      Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
      Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
      TextureAtlasSprite var10000;
      if (Config.shortGrass.INSTANCE.getUseGenerated()) {
         var10000 = this.$iconGen$inlined.getIcon();
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }
      } else {
         var10000 = this.$iconset$inlined.get(this.$rand$inlined[qi & 1].intValue());
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }
      }

      return var10000;
   }
}
