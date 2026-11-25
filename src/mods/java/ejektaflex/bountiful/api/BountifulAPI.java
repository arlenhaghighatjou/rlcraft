package ejektaflex.bountiful.api;

import kotlin.Metadata;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"},
   d2 = {"Lejektaflex/bountiful/api/BountifulAPI;", "Lejektaflex/bountiful/api/BountifulAPIProvider;", "()V", "Bountiful_api"}
)
public final class BountifulAPI extends BountifulAPIProvider {
   public static final BountifulAPI INSTANCE;

   private BountifulAPI() {
   }

   static {
      BountifulAPI var0 = new BountifulAPI();
      INSTANCE = var0;
   }
}
