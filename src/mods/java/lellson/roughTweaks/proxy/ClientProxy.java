package lellson.roughTweaks.proxy;

import lellson.roughTweaks.items.RoughItems;

public class ClientProxy extends ServerProxy {
   public ClientProxy() {
   }

   public void registerRenderers() {
      RoughItems.registerRenderers();
   }
}
