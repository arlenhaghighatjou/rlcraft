package com.chaosbuffalo.spartanfire.init;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import net.minecraft.item.Item;

public class ModelRenderRegistrySFire {
   public ModelRenderRegistrySFire() {
   }

   public static void addItemToRegistry(Item item, String modelLoc) {
      SpartanWeaponryAPI.addItemModelToRegistry(item, "spartanfire", modelLoc);
   }
}
