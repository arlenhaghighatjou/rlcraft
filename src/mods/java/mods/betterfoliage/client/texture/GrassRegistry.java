package mods.betterfoliage.client.texture;

import kotlin.Metadata;
import mods.octarinecore.client.resource.ModelRenderRegistryRoot;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003\u00a8\u0006\u0004"},
   d2 = {"Lmods/betterfoliage/client/texture/GrassRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryRoot;", "Lmods/betterfoliage/client/texture/GrassInfo;", "()V", "BetterFoliage-MC1.12"}
)
public final class GrassRegistry extends ModelRenderRegistryRoot<GrassInfo> {
   public static final GrassRegistry INSTANCE;

   private GrassRegistry() {
   }

   static {
      GrassRegistry var0 = new GrassRegistry();
      INSTANCE = var0;
   }
}
