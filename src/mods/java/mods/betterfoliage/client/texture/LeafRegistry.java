package mods.betterfoliage.client.texture;

import kotlin.Metadata;
import mods.octarinecore.client.resource.ModelRenderRegistryRoot;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
   d2 = {"Lmods/betterfoliage/client/texture/LeafRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryRoot;", "Lmods/betterfoliage/client/texture/LeafInfo;", "()V", "BetterFoliage-MC1.12"}
)
public final class LeafRegistry extends ModelRenderRegistryRoot<LeafInfo> {
   public static final LeafRegistry INSTANCE;

   private LeafRegistry() {
   }

   static {
      LeafRegistry var0 = new LeafRegistry();
      INSTANCE = var0;
   }
}
