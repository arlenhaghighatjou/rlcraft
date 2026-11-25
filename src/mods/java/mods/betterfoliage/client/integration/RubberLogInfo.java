package mods.betterfoliage.client.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.betterfoliage.client.render.column.SimpleColumnInfo;
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
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001\u0019B=\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b¢\u0006\u0002\u0010\fR2\u0010\r\u001a \u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0011\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000ej\u0002`\u0012X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\t\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006\u001a"},
   d2 = {"Lmods/betterfoliage/client/integration/RubberLogInfo;", "Lmods/betterfoliage/client/render/column/SimpleColumnInfo;", "axis", "Lnet/minecraft/util/EnumFacing$Axis;", "spotDir", "Lnet/minecraft/util/EnumFacing;", "topTexture", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "bottomTexture", "spotTexture", "sideTextures", "", "(Lnet/minecraft/util/EnumFacing$Axis;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;Ljava/util/List;)V", "side", "Lkotlin/Function3;", "Lmods/octarinecore/client/render/ShadingContext;", "", "Lmods/octarinecore/client/render/Quad;", "Lmods/octarinecore/client/render/QuadIconResolver;", "getSide", "()Lkotlin/jvm/functions/Function3;", "getSpotDir", "()Lnet/minecraft/util/EnumFacing;", "getSpotTexture", "()Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "Key", "BetterFoliage-MC1.12"}
)
public final class RubberLogInfo extends SimpleColumnInfo {
   @NotNull
   private final Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> side;
   @NotNull
   private final EnumFacing spotDir;
   @NotNull
   private final EaglerTextureAtlasSprite spotTexture;

   @NotNull
   public Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite> getSide() {
      return this.side;
   }

   @NotNull
   public final EnumFacing getSpotDir() {
      return this.spotDir;
   }

   @NotNull
   public final EaglerTextureAtlasSprite getSpotTexture() {
      return this.spotTexture;
   }

   public RubberLogInfo(@Nullable Axis axis, @NotNull EnumFacing spotDir, @NotNull EaglerTextureAtlasSprite topTexture, @NotNull EaglerTextureAtlasSprite bottomTexture, @NotNull EaglerTextureAtlasSprite spotTexture, @NotNull List<? extends EaglerTextureAtlasSprite> sideTextures) {
      Intrinsics.checkParameterIsNotNull(spotDir, "spotDir");
      Intrinsics.checkParameterIsNotNull(topTexture, "topTexture");
      Intrinsics.checkParameterIsNotNull(bottomTexture, "bottomTexture");
      Intrinsics.checkParameterIsNotNull(spotTexture, "spotTexture");
      Intrinsics.checkParameterIsNotNull(sideTextures, "sideTextures");
      super(axis, topTexture, bottomTexture, sideTextures);
      this.spotDir = spotDir;
      this.spotTexture = spotTexture;
      this.side = (Function3)(new Function3<ShadingContext, Integer, Quad, EaglerTextureAtlasSprite>() {
         @Nullable
         public final EaglerTextureAtlasSprite invoke(@NotNull ShadingContext ctx, int idx, @NotNull Quad quad) {
            Intrinsics.checkParameterIsNotNull(ctx, "ctx");
            Intrinsics.checkParameterIsNotNull(quad, "quad");
            EnumFacing worldFace = GeometryKt.rotate((idx & 1) == 0 ? EnumFacing.SOUTH : EnumFacing.EAST, ctx.getRotation());
            EaglerTextureAtlasSprite var10000;
            if (worldFace == RubberLogInfo.this.getSpotDir()) {
               var10000 = RubberLogInfo.this.getSpotTexture();
            } else {
               int sideIdx = RubberLogInfo.this.getSideTextures().size() > 1 ? (RendererHolder.getBlockContext().random(1) + RubberLogInfo.this.getDirToIdx()[worldFace.ordinal()].intValue()) % RubberLogInfo.this.getSideTextures().size() : 0;
               var10000 = (EaglerTextureAtlasSprite)RubberLogInfo.this.getSideTextures().get(sideIdx);
            }

            return var10000;
         }
      });
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B-\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\u0002\u0010\fJ\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0019"},
      d2 = {"Lmods/betterfoliage/client/integration/RubberLogInfo$Key;", "Lmods/octarinecore/client/resource/ModelRenderKey;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "logger", "Lorg/apache/logging/log4j/Logger;", "axis", "Lnet/minecraft/util/EnumFacing$Axis;", "spotDir", "Lnet/minecraft/util/EnumFacing;", "textures", "", "", "(Lorg/apache/logging/log4j/Logger;Lnet/minecraft/util/EnumFacing$Axis;Lnet/minecraft/util/EnumFacing;Ljava/util/List;)V", "getAxis", "()Lnet/minecraft/util/EnumFacing$Axis;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "getSpotDir", "()Lnet/minecraft/util/EnumFacing;", "getTextures", "()Ljava/util/List;", "resolveSprites", "Lmods/betterfoliage/client/integration/RubberLogInfo;", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "BetterFoliage-MC1.12"}
   )
   public static final class Key implements ModelRenderKey<ColumnTextureInfo> {
      @NotNull
      private final Logger logger;
      @Nullable
      private final Axis axis;
      @NotNull
      private final EnumFacing spotDir;
      @NotNull
      private final List<String> textures;

      @NotNull
      public RubberLogInfo resolveSprites(@NotNull TextureMap atlas) {
         Intrinsics.checkParameterIsNotNull(atlas, "atlas");
         Axis var10000 = this.axis;
         EnumFacing var10001 = this.spotDir;
         EaglerTextureAtlasSprite var10002 = Utils.get(atlas, (String)this.textures.get(0));
         if (var10002 == null) {
            var10002 = atlas.getMissingSprite();
            Intrinsics.checkExpressionValueIsNotNull(var10002, "atlas.missingSprite");
         }

         EaglerTextureAtlasSprite var10003 = Utils.get(atlas, (String)this.textures.get(1));
         if (var10003 == null) {
            var10003 = atlas.getMissingSprite();
            Intrinsics.checkExpressionValueIsNotNull(var10003, "atlas.missingSprite");
         }

         EaglerTextureAtlasSprite var10004 = Utils.get(atlas, (String)this.textures.get(2));
         if (var10004 == null) {
            var10004 = atlas.getMissingSprite();
            Intrinsics.checkExpressionValueIsNotNull(var10004, "atlas.missingSprite");
         }

         Iterable $this$map$iv = (Iterable)CollectionsKt.drop((Iterable)this.textures, 3);
         EaglerTextureAtlasSprite var17 = var10004;
         EaglerTextureAtlasSprite var16 = var10003;
         EaglerTextureAtlasSprite var15 = var10002;
         EnumFacing var14 = var10001;
         Axis var13 = var10000;
         int $i$f$map = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
         int $i$f$mapTo = 0;

         for(Object item$iv$iv : $this$map$iv) {
            String it = (String)item$iv$iv;
            int $i$a$-map-RubberLogInfo$Key$resolveSprites$1 = 0;
            EaglerTextureAtlasSprite var26 = Utils.get(atlas, it);
            if (var26 == null) {
               var26 = atlas.getMissingSprite();
            }

            EaglerTextureAtlasSprite var19 = var26;
            destination$iv$iv.add(var19);
         }

         List var18 = (List)destination$iv$iv;
         return new RubberLogInfo(var13, var14, var15, var16, var17, var18);
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
      public final EnumFacing getSpotDir() {
         return this.spotDir;
      }

      @NotNull
      public final List<String> getTextures() {
         return this.textures;
      }

      public Key(@NotNull Logger logger, @Nullable Axis axis, @NotNull EnumFacing spotDir, @NotNull List<String> textures) {
         Intrinsics.checkParameterIsNotNull(logger, "logger");
         Intrinsics.checkParameterIsNotNull(spotDir, "spotDir");
         Intrinsics.checkParameterIsNotNull(textures, "textures");
         super();
         this.logger = logger;
         this.axis = axis;
         this.spotDir = spotDir;
         this.textures = textures;
      }

      public void onPreStitch(@NotNull TextureMap atlas) {
         Intrinsics.checkParameterIsNotNull(atlas, "atlas");
         ModelRenderKey.DefaultImpls.onPreStitch(this, atlas);
      }
   }
}
