package mods.octarinecore.client.resource;

import kotlin.Metadata;
import net.minecraft.client.renderer.texture.TextureMap;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0007"},
   d2 = {"Lmods/octarinecore/client/resource/IStitchListener;", "", "onPostStitch", "", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "onPreStitch", "BetterFoliage-MC1.12"}
)
public interface IStitchListener {
   void onPreStitch(@NotNull TextureMap var1);

   void onPostStitch(@NotNull TextureMap var1);
}
