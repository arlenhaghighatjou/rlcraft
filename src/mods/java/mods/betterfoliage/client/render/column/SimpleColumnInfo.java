package mods.betterfoliage.client.render.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.RendererHolder;
import mods.octarinecore.client.render.ShadingContext;
import mods.octarinecore.client.resource.ModelRenderKey;
import mods.octarinecore.client.resource.Utils;
import mods.octarinecore.common.GeometryKt;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\b\f\b\u0017\u0018\u00002\u00020\u0001:\u0001\"B-\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\b\u00a2\u0006\u0002\u0010\tR\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR2\u0010\f\u001a \u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u00050\rj\u0002`\u0011X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0019\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0017\u00a2\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u0018\u0010\u0019R2\u0010\u001b\u001a \u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u00050\rj\u0002`\u0011X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0013R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR2\u0010\u001f\u001a \u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u00050\rj\u0002`\u0011X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015\u00a8\u0006#"},
   d2 = {"Lmods/betterfoliage/client/render/column/SimpleColumnInfo;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "axis", "Lnet/minecraft/util/EnumFacing$Axis;", "topTexture", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "bottomTexture", "sideTextures", "", "(Lnet/minecraft/util/EnumFacing$Axis;Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;Ljava/util/List;)V", "getAxis", "()Lnet/minecraft/util/EnumFacing$Axis;", "bottom", "Lkotlin/Function3;", "Lmods/octarinecore/client/render/ShadingContext;", "", "Lmods/octarinecore/client/render/Quad;", "Lmods/octarinecore/client/render/QuadIconResolver;", "getBottom", "()Lkotlin/jvm/functions/Function3;", "getBottomTexture", "()Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "dirToIdx", "", "getDirToIdx", "()[Ljava/lang/Integer;", "[Ljava/lang/Integer;", "side", "getSide", "getSideTextures", "()Ljava/util/List;", "top", "getTop", "getTopTexture", "Key", "BetterFoliage-MC1.12"}
)
public class SimpleColumnInfo implements ColumnTextureInfo {
   @NotNull
   private final Integer[] dirToIdx;
   @NotNull
   private final Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> top;
   @NotNull
   private final Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> bottom;
   @NotNull
   private final Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> side;
   @Nullable
   private final Axis axis;
   @NotNull
   private final EaglerTextureAtlasSprite topTexture;
   @NotNull
   private final EaglerTextureAtlasSprite bottomTexture;
   @NotNull
   private final List<EaglerTextureAtlasSprite> sideTextures;

   @NotNull
   public final Integer[] getDirToIdx() {
      return this.dirToIdx;
   }

   @NotNull
   public Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> getTop() {
      return this.top;
   }

   @NotNull
   public Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> getBottom() {
      return this.bottom;
   }

   @NotNull
   public Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> getSide() {
      return this.side;
   }

   @Nullable
   public Axis getAxis() {
      return this.axis;
   }

   @NotNull
   public final EaglerTextureAtlasSprite getTopTexture() {
      return this.topTexture;
   }

   @NotNull
   public final EaglerTextureAtlasSprite getBottomTexture() {
      return this.bottomTexture;
   }

   @NotNull
   public final List<EaglerTextureAtlasSprite> getSideTextures() {
      return this.sideTextures;
   }

   public SimpleColumnInfo(@Nullable Axis axis, @NotNull EaglerTextureAtlasSprite topTexture, @NotNull EaglerTextureAtlasSprite bottomTexture, @NotNull List<? extends EaglerTextureAtlasSprite> sideTextures) {
      Intrinsics.checkParameterIsNotNull(topTexture, "topTexture");
      Intrinsics.checkParameterIsNotNull(bottomTexture, "bottomTexture");
      Intrinsics.checkParameterIsNotNull(sideTextures, "sideTextures");
      super();
      this.axis = axis;
      this.topTexture = topTexture;
      this.bottomTexture = bottomTexture;
      this.sideTextures = sideTextures;
      this.dirToIdx = new Integer[]{Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(5)};
      this.top = (Function3)(new Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite>() {
         @NotNull
         public final EaglerTextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int $noName_1, @NotNull Quad $noName_2) {
            Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
            Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
            return SimpleColumnInfo.this.getTopTexture();
         }
      });
      this.bottom = (Function3)(new Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite>() {
         @NotNull
         public final EaglerTextureAtlasSprite invoke(@NotNull ShadingContext $noName_0, int $noName_1, @NotNull Quad $noName_2) {
            Intrinsics.checkParameterIsNotNull($noName_0, "<anonymous parameter 0>");
            Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
            return SimpleColumnInfo.this.getBottomTexture();
         }
      });
      this.side = (Function3)(new Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite>() {
         @NotNull
         public final EaglerTextureAtlasSprite invoke(@NotNull ShadingContext ctx, int idx, @NotNull Quad $noName_2) {
            Intrinsics.checkParameterIsNotNull(ctx, "ctx");
            Intrinsics.checkParameterIsNotNull($noName_2, "<anonymous parameter 2>");
            EnumFacing worldFace = GeometryKt.rotate((idx & 1) == 0 ? EnumFacing.SOUTH : EnumFacing.EAST, ctx.getRotation());
            int sideIdx = SimpleColumnInfo.this.getSideTextures().size() > 1 ? (RendererHolder.getBlockContext().random(1) + SimpleColumnInfo.this.getDirToIdx()[worldFace.ordinal()].intValue()) % SimpleColumnInfo.this.getSideTextures().size() : 0;
            return (EaglerTextureAtlasSprite)SimpleColumnInfo.this.getSideTextures().get(sideIdx);
         }
      });
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0002\u0010\nJ\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0015"},
      d2 = {"Lmods/betterfoliage/client/render/column/SimpleColumnInfo$Key;", "Lmods/octarinecore/client/resource/ModelRenderKey;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "logger", "Lorg/apache/logging/log4j/Logger;", "axis", "Lnet/minecraft/util/EnumFacing$Axis;", "textures", "", "", "(Lorg/apache/logging/log4j/Logger;Lnet/minecraft/util/EnumFacing$Axis;Ljava/util/List;)V", "getAxis", "()Lnet/minecraft/util/EnumFacing$Axis;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "getTextures", "()Ljava/util/List;", "resolveSprites", "Lmods/betterfoliage/client/render/column/SimpleColumnInfo;", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "BetterFoliage-MC1.12"}
   )
   public static final class Key implements ModelRenderKey<ColumnTextureInfo> {
      @NotNull
      private final Logger logger;
      @Nullable
      private final Axis axis;
      @NotNull
      private final List<String> textures;

      @NotNull
      public SimpleColumnInfo resolveSprites(@NotNull TextureMap atlas) {
         Intrinsics.checkParameterIsNotNull(atlas, "atlas");
         Axis var10000 = this.axis;
         EaglerTextureAtlasSprite var10001 = Utils.get(atlas, (String)this.textures.get(0));
         if (var10001 == null) {
            var10001 = atlas.getMissingSprite();
            Intrinsics.checkExpressionValueIsNotNull(var10001, "atlas.missingSprite");
         }

         EaglerTextureAtlasSprite var10002 = Utils.get(atlas, (String)this.textures.get(1));
         if (var10002 == null) {
            var10002 = atlas.getMissingSprite();
            Intrinsics.checkExpressionValueIsNotNull(var10002, "atlas.missingSprite");
         }

         Iterable $this$map$iv = (Iterable)CollectionsKt.drop((Iterable)this.textures, 2);
         EaglerTextureAtlasSprite var15 = var10002;
         EaglerTextureAtlasSprite var14 = var10001;
         Axis var13 = var10000;
         int $i$f$map = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
         int $i$f$mapTo = 0;

         for(Object item$iv$iv : $this$map$iv) {
            String it = (String)item$iv$iv;
            int $i$a$-map-SimpleColumnInfo$Key$resolveSprites$1 = 0;
            EaglerTextureAtlasSprite var22 = Utils.get(atlas, it);
            if (var22 == null) {
               var22 = atlas.getMissingSprite();
            }

            EaglerTextureAtlasSprite var17 = var22;
            destination$iv$iv.add(var17);
         }

         List var16 = (List)destination$iv$iv;
         return new SimpleColumnInfo(var13, var14, var15, var16);
      }

      @NotNull
      public Logger getLogger() {
         return this.logger;
      }

      @Nullable
      public final Axis getAxis() {
         return this.axis;
      }

      @NotNull
      public final List<String> getTextures() {
         return this.textures;
      }

      public Key(@NotNull Logger logger, @Nullable Axis axis, @NotNull List<String> textures) {
         Intrinsics.checkParameterIsNotNull(logger, "logger");
         Intrinsics.checkParameterIsNotNull(textures, "textures");
         super();
         this.logger = logger;
         this.axis = axis;
         this.textures = textures;
      }

      public void onPreStitch(@NotNull TextureMap atlas) {
         Intrinsics.checkParameterIsNotNull(atlas, "atlas");
         ModelRenderKey.DefaultImpls.onPreStitch(this, atlas);
      }
   }
}
