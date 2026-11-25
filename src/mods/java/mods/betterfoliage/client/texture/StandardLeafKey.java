package mods.betterfoliage.client.texture;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.Client;
import mods.octarinecore.client.resource.ModelRenderKey;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u0019H\u0016R\u001a\u0010\b\u001a\u00020\tX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\u0006X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010¨\u0006\u001b"},
   d2 = {"Lmods/betterfoliage/client/texture/StandardLeafKey;", "Lmods/octarinecore/client/resource/ModelRenderKey;", "Lmods/betterfoliage/client/texture/LeafInfo;", "logger", "Lorg/apache/logging/log4j/Logger;", "textureName", "", "(Lorg/apache/logging/log4j/Logger;Ljava/lang/String;)V", "generated", "Lnet/minecraft/util/ResourceLocation;", "getGenerated", "()Lnet/minecraft/util/ResourceLocation;", "setGenerated", "(Lnet/minecraft/util/ResourceLocation;)V", "leafType", "getLeafType", "()Ljava/lang/String;", "setLeafType", "(Ljava/lang/String;)V", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "getTextureName", "onPreStitch", "", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "resolveSprites", "BetterFoliage-MC1.12"}
)
public final class StandardLeafKey implements ModelRenderKey<LeafInfo> {
   @NotNull
   public String leafType;
   @NotNull
   public ResourceLocation generated;
   @NotNull
   private final Logger logger;
   @NotNull
   private final String textureName;

   @NotNull
   public final String getLeafType() {
      String var10000 = this.leafType;
      if (this.leafType == null) {
         Intrinsics.throwUninitializedPropertyAccessException("leafType");
      }

      return var10000;
   }

   public final void setLeafType(@NotNull String var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.leafType = <set-?>;
   }

   @NotNull
   public final ResourceLocation getGenerated() {
      ResourceLocation var10000 = this.generated;
      if (this.generated == null) {
         Intrinsics.throwUninitializedPropertyAccessException("generated");
      }

      return var10000;
   }

   public final void setGenerated(@NotNull ResourceLocation var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.generated = <set-?>;
   }

   public void onPreStitch(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      String logName = "StandardLeafKey";
      String var10001 = LeafParticleRegistry.INSTANCE.getTypeMappings().getType(this.textureName);
      if (var10001 == null) {
         var10001 = "default";
      }

      this.leafType = var10001;
      LeafGenerator var3 = Client.INSTANCE.getGenLeaves();
      String var10002 = this.textureName;
      Pair[] var10003 = new Pair[1];
      String var10007 = this.leafType;
      if (this.leafType == null) {
         Intrinsics.throwUninitializedPropertyAccessException("leafType");
      }

      var10003[0] = TuplesKt.to("type", var10007);
      this.generated = var3.generatedResource(var10002, var10003);
      ResourceLocation var4 = this.generated;
      if (this.generated == null) {
         Intrinsics.throwUninitializedPropertyAccessException("generated");
      }

      atlas.registerSprite(var4);
      this.getLogger().log(Level.DEBUG, logName + ": leaf texture   " + this.textureName);
      Logger var10000 = this.getLogger();
      Level var5 = Level.DEBUG;
      StringBuilder var6 = (new StringBuilder()).append(logName).append(":      particle ");
      String var7 = this.leafType;
      if (this.leafType == null) {
         Intrinsics.throwUninitializedPropertyAccessException("leafType");
      }

      var10000.log(var5, var6.append(var7).toString());
   }

   @NotNull
   public LeafInfo resolveSprites(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      LeafInfo var10000 = new LeafInfo;
      ResourceLocation var10003 = this.generated;
      if (this.generated == null) {
         Intrinsics.throwUninitializedPropertyAccessException("generated");
      }

      EaglerTextureAtlasSprite var10002 = mods.octarinecore.client.resource.Utils.get(atlas, var10003);
      if (var10002 == null) {
         var10002 = atlas.getMissingSprite();
         Intrinsics.checkExpressionValueIsNotNull(var10002, "atlas.missingSprite");
      }

      String var2 = this.leafType;
      if (this.leafType == null) {
         Intrinsics.throwUninitializedPropertyAccessException("leafType");
      }

      var10000.<init>(var10002, var2, 0, 4, (DefaultConstructorMarker)null);
      return var10000;
   }

   @NotNull
   public Logger getLogger() {
      return this.logger;
   }

   @NotNull
   public final String getTextureName() {
      return this.textureName;
   }

   public StandardLeafKey(@NotNull Logger logger, @NotNull String textureName) {
      Intrinsics.checkParameterIsNotNull(logger, "logger");
      Intrinsics.checkParameterIsNotNull(textureName, "textureName");
      super();
      this.logger = logger;
      this.textureName = textureName;
   }
}
