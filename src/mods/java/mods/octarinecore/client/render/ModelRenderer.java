package mods.octarinecore.client.render;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function6;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.common.Double3;
import mods.octarinecore.common.Rotation;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u00b1\u0001\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\u001a\b\u0002\u0010\u0013\u001a\u0014\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00120\u00142$\u0010\u0017\u001a \u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u0016\u0012\u0006\u0012\u0004\u0018\u00010\u00190\u0018j\u0002`\u001a29\u0010\u001b\u001a5\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020\b0\u001cj\u0002`\u001e\u00a2\u0006\u0002\b\u001fR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006 "},
   d2 = {"Lmods/octarinecore/client/render/ModelRenderer;", "Lmods/octarinecore/client/render/ShadingContext;", "()V", "temp", "Lmods/octarinecore/client/render/RenderVertex;", "getTemp", "()Lmods/octarinecore/client/render/RenderVertex;", "render", "", "worldRenderer", "Lnet/minecraft/client/renderer/WorldRenderer;", "model", "Lmods/octarinecore/client/render/Model;", "rot", "Lmods/octarinecore/common/Rotation;", "trans", "Lmods/octarinecore/common/Double3;", "forceFlat", "", "quadFilter", "Lkotlin/Function2;", "", "Lmods/octarinecore/client/render/Quad;", "icon", "Lkotlin/Function3;", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "Lmods/octarinecore/client/render/QuadIconResolver;", "postProcess", "Lkotlin/Function6;", "Lmods/octarinecore/client/render/Vertex;", "Lmods/octarinecore/client/render/PostProcessLambda;", "Lkotlin/ExtensionFunctionType;", "BetterFoliage-MC1.12"}
)
public final class ModelRenderer extends ShadingContext {
   @NotNull
   private final RenderVertex temp = new RenderVertex();

   @NotNull
   public final RenderVertex getTemp() {
      return this.temp;
   }

   public final void render(@NotNull WorldRenderer worldRenderer, @NotNull Model model, @NotNull Rotation rot, @NotNull Double3 trans, boolean forceFlat, @NotNull Function2<? super Integer, ? super Quad, Boolean> quadFilter, @NotNull Function3<? super ShadingContext, ? super Integer, ? super Quad, ? extends EaglerTextureAtlasSprite> icon, @NotNull Function6<? super RenderVertex, ? super ShadingContext, ? super Integer, ? super Quad, ? super Integer, ? super Vertex, Unit> postProcess) {
      Intrinsics.checkParameterIsNotNull(worldRenderer, "worldRenderer");
      Intrinsics.checkParameterIsNotNull(model, "model");
      Intrinsics.checkParameterIsNotNull(rot, "rot");
      Intrinsics.checkParameterIsNotNull(trans, "trans");
      Intrinsics.checkParameterIsNotNull(quadFilter, "quadFilter");
      Intrinsics.checkParameterIsNotNull(icon, "icon");
      Intrinsics.checkParameterIsNotNull(postProcess, "postProcess");
      this.setRotation(rot);
      this.setAoEnabled(Minecraft.isAmbientOcclusionEnabled());
      ModelRendererKt.ensureSpaceForQuads(worldRenderer, model.getQuads().size() + 1);
      Iterable $this$forEachIndexed$iv = (Iterable)model.getQuads();
      int $i$f$forEachIndexed = 0;
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int var14 = index$iv++;
         boolean var15 = false;
         if (var14 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Quad quad = (Quad)item$iv;
         int quadIdx = var14;
         int $i$a$-forEachIndexed-ModelRenderer$render$2 = 0;
         if (((Boolean)quadFilter.invoke(Integer.valueOf(var14), quad)).booleanValue()) {
            EaglerTextureAtlasSprite drawIcon = (EaglerTextureAtlasSprite)icon.invoke(this, Integer.valueOf(var14), quad);
            if (drawIcon != null) {
               Refs.INSTANCE.getQuadSprite().set(worldRenderer, drawIcon);
               Object[] $this$forEachIndexed$iv = quad.getVerts();
               int $i$f$forEachIndexed = 0;
               int index$iv = 0;

               for(Object item$iv : $this$forEachIndexed$iv) {
                  int vertIdx = index$iv++;
                  int $i$a$-forEachIndexed-ModelRenderer$render$2$1 = 0;
                  this.temp.init((Vertex)item$iv).rotate(this.getRotation()).translate(trans);
                  Shader shader = this.getAoEnabled() && !forceFlat ? ((Vertex)item$iv).getAoShader() : ((Vertex)item$iv).getFlatShader();
                  shader.shade(this, this.temp);
                  postProcess.invoke(this.temp, this, Integer.valueOf(quadIdx), quad, Integer.valueOf(vertIdx), item$iv);
                  RenderVertex this_$iv = this.temp;
                  int $i$f$setIcon = 0;
                  this_$iv.setU((double)(drawIcon.getMaxU() - drawIcon.getMinU()) * (this_$iv.getU() + 0.5D) + (double)drawIcon.getMinU());
                  this_$iv.setV((double)(drawIcon.getMaxV() - drawIcon.getMinV()) * (this_$iv.getV() + 0.5D) + (double)drawIcon.getMinV());
                  worldRenderer.pos(this.temp.getX(), this.temp.getY(), this.temp.getZ()).color(this.temp.getRed(), this.temp.getGreen(), this.temp.getBlue(), 1.0F).tex(this.temp.getU(), this.temp.getV()).lightmap(this.temp.getBrightness() >> 16 & '\uffff', this.temp.getBrightness() & '\uffff').endVertex();
               }
            }
         }
      }

   }

   public ModelRenderer() {
   }
}
