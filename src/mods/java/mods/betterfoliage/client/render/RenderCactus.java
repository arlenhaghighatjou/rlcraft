package mods.betterfoliage.client.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.Client;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.octarinecore.client.render.AbstractBlockRenderingHandler;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.Model;
import mods.octarinecore.client.render.ModelRenderer;
import mods.octarinecore.client.render.ModelRendererKt;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.RendererHolder;
import mods.octarinecore.client.render.ShadersKt;
import mods.octarinecore.client.render.ShadingContext;
import mods.octarinecore.client.render.ShadingKt;
import mods.octarinecore.client.resource.IconHolder;
import mods.octarinecore.client.resource.IconSet;
import mods.octarinecore.client.resource.ModelHolder;
import mods.octarinecore.client.resource.ModelSet;
import mods.octarinecore.common.Double3;
import mods.octarinecore.common.Int3;
import mods.octarinecore.common.Rotation;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0016J(\u0010$\u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*H\u0016R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u001a\u001a\u00020\u001b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d¨\u0006+"},
   d2 = {"Lmods/betterfoliage/client/render/RenderCactus;", "Lmods/octarinecore/client/render/AbstractBlockRenderingHandler;", "()V", "cactusArmRotation", "", "Lmods/octarinecore/common/Rotation;", "getCactusArmRotation", "()Ljava/util/List;", "cactusStemRadius", "", "getCactusStemRadius", "()D", "iconArm", "Lmods/octarinecore/client/resource/IconSet;", "getIconArm", "()Lmods/octarinecore/client/resource/IconSet;", "iconCross", "Lmods/octarinecore/client/resource/IconHolder;", "getIconCross", "()Lmods/octarinecore/client/resource/IconHolder;", "modelArm", "Lmods/octarinecore/client/resource/ModelSet;", "getModelArm", "()Lmods/octarinecore/client/resource/ModelSet;", "modelCross", "getModelCross", "modelStem", "Lmods/octarinecore/client/resource/ModelHolder;", "getModelStem", "()Lmods/octarinecore/client/resource/ModelHolder;", "afterPreStitch", "", "isEligible", "", "ctx", "Lmods/octarinecore/client/render/BlockContext;", "render", "dispatcher", "Lnet/minecraft/client/renderer/BlockRendererDispatcher;", "renderer", "Lnet/minecraft/client/renderer/BufferBuilder;", "layer", "Lnet/minecraft/util/BlockRenderLayer;", "BetterFoliage-MC1.12"}
)
public final class RenderCactus extends AbstractBlockRenderingHandler {
   private final double cactusStemRadius = 0.4375D;
   @NotNull
   private final List<Rotation> cactusArmRotation;
   @NotNull
   private final IconHolder iconCross;
   @NotNull
   private final IconSet iconArm;
   @NotNull
   private final ModelHolder modelStem;
   @NotNull
   private final ModelSet modelCross;
   @NotNull
   private final ModelSet modelArm;

   public final double getCactusStemRadius() {
      return this.cactusStemRadius;
   }

   @NotNull
   public final List<Rotation> getCactusArmRotation() {
      return this.cactusArmRotation;
   }

   @NotNull
   public final IconHolder getIconCross() {
      return this.iconCross;
   }

   @NotNull
   public final IconSet getIconArm() {
      return this.iconArm;
   }

   @NotNull
   public final ModelHolder getModelStem() {
      return this.modelStem;
   }

   @NotNull
   public final ModelSet getModelCross() {
      return this.modelCross;
   }

   @NotNull
   public final ModelSet getModelArm() {
      return this.modelArm;
   }

   public void afterPreStitch() {
      Client var10000 = Client.INSTANCE;
      Level var10001 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "Level.INFO");
      var10000.log(var10001, "Registered " + this.iconArm.getNum() + " cactus arm textures");
   }

   public boolean isEligible(@NotNull BlockContext ctx) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      return Config.INSTANCE.getEnabled() && Config.cactus.INSTANCE.getEnabled() && Config.blocks.INSTANCE.getCactus().matchesClass(ctx.getBlock());
   }

   public boolean render(@NotNull final BlockContext ctx, @NotNull BlockRendererDispatcher dispatcher, @NotNull BufferBuilder renderer, @NotNull BlockRenderLayer layer) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      Intrinsics.checkParameterIsNotNull(dispatcher, "dispatcher");
      Intrinsics.checkParameterIsNotNull(renderer, "renderer");
      Intrinsics.checkParameterIsNotNull(layer, "layer");
      if (!Utils.isCutout(layer)) {
         return false;
      } else {
         RendererHolder.getModelRenderer().updateShading(Int3.Companion.getZero(), ModelRendererKt.getAllFaces());
         ColumnTextureInfo var10000 = (ColumnTextureInfo)StandardCactusRegistry.INSTANCE.get(ctx);
         if (var10000 != null) {
            final ColumnTextureInfo icons = var10000;
            ModelRenderer.render$default(RendererHolder.getModelRenderer(), renderer, this.modelStem.getModel(), Rotation.Companion.getIdentity(), (Double3)null, false, (Function2)null, new Function3<ShadingContext, Integer, Quad, TextureAtlasSprite>() {
               @Nullable
               public final TextureAtlasSprite invoke(@NotNull ShadingContext ctx, int qi, @NotNull Quad q) {
                  Intrinsics.checkParameterIsNotNull(ctx, "ctx");
                  Intrinsics.checkParameterIsNotNull(q, "q");
                  TextureAtlasSprite var10000;
                  switch(qi) {
                  case 0:
                     var10000 = (TextureAtlasSprite)icons.getBottom().invoke(ctx, Integer.valueOf(qi), q);
                     break;
                  case 1:
                     var10000 = (TextureAtlasSprite)icons.getTop().invoke(ctx, Integer.valueOf(qi), q);
                     break;
                  default:
                     var10000 = (TextureAtlasSprite)icons.getSide().invoke(ctx, Integer.valueOf(qi), q);
                  }

                  return var10000;
               }
            }, ModelRendererKt.getNoPost(), 56, (Object)null);
            ModelRenderer.render$default(RendererHolder.getModelRenderer(), renderer, this.modelCross.get(ctx.random(0)), Rotation.Companion.getIdentity(), (Double3)null, false, (Function2)null, new Function3<ShadingContext, Integer, Quad, TextureAtlasSprite>() {
               @NotNull
               public final TextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int $noName_1, @NotNull Quad $noName_2) {
                  Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
                  Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
                  TextureAtlasSprite var10000 = RenderCactus.this.getIconCross().getIcon();
                  if (var10000 == null) {
                     Intrinsics.throwNpe();
                  }

                  return var10000;
               }
            }, ModelRendererKt.getNoPost(), 56, (Object)null);
            ModelRenderer.render$default(RendererHolder.getModelRenderer(), renderer, this.modelArm.get(ctx.random(1)), (Rotation)this.cactusArmRotation.get(ctx.random(2) % 4), (Double3)null, false, (Function2)null, new Function3<ShadingContext, Integer, Quad, TextureAtlasSprite>() {
               @NotNull
               public final TextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int $noName_1, @NotNull Quad $noName_2) {
                  Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
                  Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
                  TextureAtlasSprite var10000 = RenderCactus.this.getIconArm().get(ctx.random(3));
                  if (var10000 == null) {
                     Intrinsics.throwNpe();
                  }

                  return var10000;
               }
            }, ModelRendererKt.getNoPost(), 56, (Object)null);
            return true;
         } else {
            return this.renderWorldBlockBase(ctx, dispatcher, renderer, (BlockRenderLayer)null);
         }
      }
   }

   public RenderCactus() {
      super("betterfoliage");
      Iterable $this$map$iv = (Iterable)CollectionsKt.listOf(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST});
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         EnumFacing it = (EnumFacing)item$iv$iv;
         int $i$a$-map-RenderCactus$cactusArmRotation$1 = 0;
         Rotation var12 = Rotation.Companion.getRot90()[it.ordinal()];
         destination$iv$iv.add(var12);
      }

      List var11 = (List)destination$iv$iv;
      this.cactusArmRotation = var11;
      this.iconCross = this.iconStatic("bettergrassandleaves", "blocks/better_cactus");
      this.iconArm = this.iconSet("bettergrassandleaves", "blocks/better_cactus_arm_%d");
      this.modelStem = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            double var10001 = -RenderCactus.this.getCactusStemRadius();
            double var10002 = RenderCactus.this.getCactusStemRadius();
            double var10003 = -RenderCactus.this.getCactusStemRadius();
            double var10004 = RenderCactus.this.getCactusStemRadius();
            double $this$forEach$iv = 0.5D;
            double var4 = var10004;
            double it = var10003;
            double var8 = var10002;
            Quad var10 = $this$model.horizontalRectangle(var10001, it, var8, var4, $this$forEach$iv).scaleUV(RenderCactus.this.getCactusStemRadius() * 2.0D);
            int $i$f$forEach = 0;
            boolean var13 = false;
            int $i$a$-let-RenderCactus$modelStem$1$1 = 0;
            Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(new Quad[]{var10.getFlipped().move(TuplesKt.to(Double.valueOf(1.0D), EnumFacing.DOWN)), var10});
            $i$f$forEach = 0;

            for(Object element$iv : $this$forEach$iv) {
               Quad it = (Quad)element$iv;
               int $i$a$-forEach-RenderCactus$modelStem$1$2 = 0;
               $this$model.add(Quad.setAoShader$default(it, ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(Axis.Y), (Function2)null, 1, (Object)null), (Function2)null, 2, (Object)null));
            }

            $this$model.addAll((Iterable)Utils.toCross(Quad.setAoShader$default($this$model.verticalRectangle(-0.5D, RenderCactus.this.getCactusStemRadius(), 0.5D, RenderCactus.this.getCactusStemRadius(), -0.5D, 0.5D), ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(Axis.Y), (Function2)null, 1, (Object)null), (Function2)null, 2, (Object)null), EnumFacing.UP));
         }
      });
      this.modelCross = this.modelSet(64, null.INSTANCE);
      this.modelArm = this.modelSet(64, null.INSTANCE);
   }
}
