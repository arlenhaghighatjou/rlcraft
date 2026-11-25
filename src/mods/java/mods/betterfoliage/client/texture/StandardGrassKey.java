package mods.betterfoliage.client.texture;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.config.Config;
import mods.octarinecore.client.render.HSB;
import mods.octarinecore.client.resource.ModelRenderKey;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0010\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000f"},
   d2 = {"Lmods/betterfoliage/client/texture/StandardGrassKey;", "Lmods/octarinecore/client/resource/ModelRenderKey;", "Lmods/betterfoliage/client/texture/GrassInfo;", "logger", "Lorg/apache/logging/log4j/Logger;", "textureName", "", "(Lorg/apache/logging/log4j/Logger;Ljava/lang/String;)V", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "getTextureName", "()Ljava/lang/String;", "resolveSprites", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "BetterFoliage-MC1.12"}
)
public final class StandardGrassKey implements ModelRenderKey<GrassInfo> {
   @NotNull
   private final Logger logger;
   @NotNull
   private final String textureName;

   @NotNull
   public GrassInfo resolveSprites(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      String logName = "StandardGrassKey";
      TextureAtlasSprite var10000 = mods.octarinecore.client.resource.Utils.get(atlas, this.textureName);
      if (var10000 == null) {
         var10000 = atlas.getMissingSprite();
      }

      TextureAtlasSprite texture = var10000;
      this.getLogger().log(Level.DEBUG, logName + ": texture " + this.textureName);
      HSB.Companion var6 = HSB.Companion;
      Intrinsics.checkExpressionValueIsNotNull(texture, "texture");
      Integer var10001 = mods.octarinecore.client.resource.Utils.getAverageColor(texture);
      HSB hsb = var6.fromColor(var10001 != null ? var10001.intValue() : 0);
      Integer var7;
      if ((double)hsb.getSaturation() >= Config.shortGrass.INSTANCE.getSaturationThreshold()) {
         this.getLogger().log(Level.DEBUG, logName + ":         brightness " + hsb.getBrightness());
         this.getLogger().log(Level.DEBUG, logName + ":         saturation " + hsb.getSaturation() + " >= " + Config.shortGrass.INSTANCE.getSaturationThreshold() + ", using texture color");
         var7 = HSB.copy$default(hsb, 0.0F, 0.0F, Math.min(0.9F, hsb.getBrightness() * 2.0F), 3, (Object)null).getAsColor();
      } else {
         this.getLogger().log(Level.DEBUG, logName + ":         saturation " + hsb.getSaturation() + " < " + Config.shortGrass.INSTANCE.getSaturationThreshold() + ", using block color");
         var7 = null;
      }

      Integer overrideColor = var7;
      return new GrassInfo(texture, overrideColor);
   }

   @NotNull
   public Logger getLogger() {
      return this.logger;
   }

   @NotNull
   public final String getTextureName() {
      return this.textureName;
   }

   public StandardGrassKey(@NotNull Logger logger, @NotNull String textureName) {
      Intrinsics.checkParameterIsNotNull(logger, "logger");
      Intrinsics.checkParameterIsNotNull(textureName, "textureName");
      super();
      this.logger = logger;
      this.textureName = textureName;
   }

   public void onPreStitch(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      ModelRenderKey.DefaultImpls.onPreStitch(this, atlas);
   }
}
