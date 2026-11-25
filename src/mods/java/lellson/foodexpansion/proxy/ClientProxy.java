package lellson.foodexpansion.proxy;

import lellson.foodexpansion.FoodItems;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerRenders() {
      FoodItems.renderRegistry();
   }
}
