package mods.octarinecore.client.resource;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.texture.TextureMap;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0015\u0010\u000b\u001a\u00028\u00002\u0006\u0010\t\u001a\u00020\nH&¢\u0006\u0002\u0010\fR\u0014\u0010\u0003\u001a\u0004\u0018\u00010\u0004X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006¨\u0006\r"},
   d2 = {"Lmods/octarinecore/client/resource/ModelRenderKey;", "T", "", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "onPreStitch", "", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "resolveSprites", "(Lnet/minecraft/client/renderer/texture/TextureMap;)Ljava/lang/Object;", "BetterFoliage-MC1.12"}
)
public interface ModelRenderKey<T> {
   @Nullable
   Logger getLogger();

   void onPreStitch(@NotNull TextureMap var1);

   T resolveSprites(@NotNull TextureMap var1);

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      public static <T> void onPreStitch(ModelRenderKey<T> $this, @NotNull TextureMap atlas) {
         Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      }
   }
}
